<?php
session_start();
header('Content-Type: application/json; charset=utf-8');

// Função para retornar erro
function returnError($message) {
    $_SESSION['erro_vinculo'] = $message;
    echo json_encode([
        'success' => false,
        'message' => $message
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// Função para retornar sucesso
function returnSuccess($message, $redirect = null) {
    $_SESSION['sucesso_vinculo'] = $message;
    echo json_encode([
        'success' => true,
        'message' => $message,
        'redirect' => $redirect
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// Função para verificar senha
function verificarSenha($senha_digitada, $senha_hash) {
    // Tentar verificar como hash
    if (password_verify($senha_digitada, $senha_hash)) {
        return true;
    }
    // Se não for hash, comparar diretamente (compatibilidade)
    if ($senha_digitada === $senha_hash) {
        return true;
    }
    return false;
}

// ============================================
// VERIFICAÇÕES INICIAIS
// ============================================

// Verificar se responsável está logado
if (!isset($_SESSION['id_responsavel']) && !isset($_SESSION['id_responsa'])) {
    returnError('Você precisa estar logado como responsável para vincular um neurodivergente!');
}

$id_responsavel = $_SESSION['id_responsavel'] ?? $_SESSION['id_responsa'] ?? null;

if (!$id_responsavel) {
    returnError('ID do responsável não encontrado na sessão!');
}

// Verificar método POST
if ($_SERVER["REQUEST_METHOD"] != "POST") {
    returnError('Método de requisição inválido');
}

// Incluir conexão
if (!file_exists("conexao.php")) {
    returnError('Arquivo de conexão não encontrado');
}

include "conexao.php";

// Verificar conexão
if (!isset($sql) || $sql->connect_error) {
    returnError('Erro de conexão com o banco de dados');
}

// ============================================
// RECEBER E VALIDAR DADOS
// ============================================

$identificador = trim($_POST["identificador"] ?? "");
$senha = $_POST["senha"] ?? "";

// Validações básicas
if (empty($identificador)) {
    returnError('CPF ou E-mail é obrigatório');
}

if (empty($senha)) {
    returnError('Senha é obrigatória');
}

// Limpar identificador (remover pontos, traços, etc)
$identificador_limpo = preg_replace('/[^a-zA-Z0-9@._-]/', '', $identificador);

// ============================================
// BUSCAR NEURODIVERGENTE NO BANCO
// ============================================

// Verificar se é email ou CPF
$is_email = filter_var($identificador_limpo, FILTER_VALIDATE_EMAIL);

if ($is_email) {
    // Buscar por email
    $stmt = $sql->prepare("
        SELECT 
            id_neuro, 
            nome, 
            email, 
            cpf, 
            senha, 
            id_responsa 
        FROM cad_neurodivergentes 
        WHERE email = ?
    ");
    $stmt->bind_param("s", $identificador_limpo);
} else {
    // Buscar por CPF
    $stmt = $sql->prepare("
        SELECT 
            id_neuro, 
            nome, 
            email, 
            cpf, 
            senha, 
            id_responsa 
        FROM cad_neurodivergentes 
        WHERE cpf = ?
    ");
    $stmt->bind_param("s", $identificador_limpo);
}

if (!$stmt) {
    returnError('Erro ao preparar consulta: ' . $sql->error);
}

$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $sql->close();
    returnError('Neurodivergente não encontrado. Verifique o CPF/E-mail digitado.');
}

$neurodivergente = $result->fetch_assoc();
$stmt->close();

// ============================================
// VERIFICAR SENHA
// ============================================

if (!verificarSenha($senha, $neurodivergente['senha'])) {
    $sql->close();
    returnError('Senha incorreta!');
}

// ============================================
// VERIFICAR SE JÁ ESTÁ VINCULADO
// ============================================

// Verificar se é o responsável original
if ($neurodivergente['id_responsa'] == $id_responsavel) {
    $sql->close();
    returnError('Este neurodivergente já está vinculado à sua conta como responsável principal!');
}

// Verificar se já existe vínculo secundário
$stmt_check = $sql->prepare("
    SELECT id_vinculo 
    FROM vinculo_responsavel_neurodivergente 
    WHERE id_responsa = ? AND id_neuro = ?
");

if (!$stmt_check) {
    $sql->close();
    returnError('Erro ao verificar vínculos existentes');
}

$stmt_check->bind_param("ii", $id_responsavel, $neurodivergente['id_neuro']);
$stmt_check->execute();
$result_check = $stmt_check->get_result();

if ($result_check->num_rows > 0) {
    $stmt_check->close();
    $sql->close();
    returnError('Este neurodivergente já está vinculado à sua conta!');
}

$stmt_check->close();

// ============================================
// CRIAR VÍNCULO SECUNDÁRIO
// ============================================

$tipo_vinculo = 'secundario';
$data_vinculo = date('Y-m-d H:i:s');

$stmt_insert = $sql->prepare("
    INSERT INTO vinculo_responsavel_neurodivergente 
    (id_responsa, id_neuro, tipo_vinculo, data_vinculo) 
    VALUES (?, ?, ?, ?)
");

if (!$stmt_insert) {
    $sql->close();
    returnError('Erro ao preparar inserção de vínculo: ' . $sql->error);
}

$stmt_insert->bind_param(
    "iiss",
    $id_responsavel,
    $neurodivergente['id_neuro'],
    $tipo_vinculo,
    $data_vinculo
);

if (!$stmt_insert->execute()) {
    $stmt_insert->close();
    $sql->close();
    returnError('Erro ao criar vínculo: ' . $stmt_insert->error);
}

$stmt_insert->close();
$sql->close();

// ============================================
// SUCESSO!
// ============================================

returnSuccess(
    'Neurodivergente "' . $neurodivergente['nome'] . '" vinculado com sucesso à sua conta!',
    'adm_responsavel.php'
);
?>
