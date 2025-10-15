<?php
// Iniciar sessão
session_start();

// Ativar exibição de erros para debug (remover em produção)
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Incluir arquivo de conexão
include "conexao.php";

// Verificar se o formulário foi enviado via POST
if ($_SERVER["REQUEST_METHOD"] != "POST") {
    echo json_encode([
        'success' => false,
        'message' => 'Método de requisição inválido'
    ]);
    exit;
}

// Verificar conexão com banco de dados
if ($sql->connect_error) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro de conexão com o banco de dados: ' . $sql->connect_error
    ]);
    exit;
}

// ============================================
// VERIFICAR SE RESPONSÁVEL ESTÁ LOGADO
// ============================================

$id_responsa = null;

// Opção 1: Verificar se há sessão do responsável (RECOMENDADO)
if (isset($_SESSION['id_responsa'])) {
    $id_responsa = $_SESSION['id_responsa'];
}
// Opção 2: Pegar o último responsável cadastrado (TEMPORÁRIO - apenas para testes)
else {
    $result = $sql->query("SELECT id_responsa FROM cad_responsavel ORDER BY id_responsa DESC LIMIT 1");
    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $id_responsa = $row['id_responsa'];
    }
}

// Se não encontrou o ID do responsável
if (!$id_responsa) {
    echo json_encode([
        'success' => false,
        'message' => 'Você precisa estar logado como responsável para cadastrar um neurodivergente. Por favor, faça login primeiro.'
    ]);
    exit;
}

// ============================================
// SANITIZAÇÃO E VALIDAÇÃO DOS DADOS
// ============================================

function sanitize($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}

$nome = sanitize($_POST["nome"]);
$email = filter_var(trim($_POST["email"]), FILTER_SANITIZE_EMAIL);
$rg = preg_replace('/[^0-9]/', '', $_POST["rg"]);
$cpf = preg_replace('/[^0-9]/', '', $_POST["cpf"]);
$sexo = sanitize($_POST["sexo"]); // CAMPO ADICIONADO
$celular = isset($_POST["celular"]) ? preg_replace('/[^0-9]/', '', $_POST["celular"]) : "";
$cep = preg_replace('/[^0-9]/', '', $_POST["cep"]);
$rua = sanitize($_POST["rua"]);
$bairro = sanitize($_POST["bairro"]);
$cidade = sanitize($_POST["cidade"]);
$numero = sanitize($_POST["numero"]);
$complemento = isset($_POST["complemento"]) ? sanitize($_POST["complemento"]) : "";
$senha = $_POST["senha"];

// ============================================
// VALIDAÇÕES
// ============================================

$errors = [];

// Validar nome
if (empty($nome) || strlen($nome) < 2) {
    $errors[] = "Nome completo é obrigatório";
}

// Validar email
if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $errors[] = "Email inválido";
}

// Validar RG
if (empty($rg) || strlen($rg) < 8) {
    $errors[] = "RG deve ter no mínimo 8 dígitos";
}

// Validar CPF
if (empty($cpf) || strlen($cpf) != 11) {
    $errors[] = "CPF deve ter 11 dígitos";
}

// Função de validação de CPF
function validarCPF($cpf) {
    if (strlen($cpf) != 11) return false;
    if (preg_match('/(\d)\1{10}/', $cpf)) return false;
    
    $soma = 0;
    for ($i = 0; $i < 9; $i++) {
        $soma += intval($cpf[$i]) * (10 - $i);
    }
    $digito1 = ($soma * 10) % 11;
    if ($digito1 == 10) $digito1 = 0;
    
    $soma = 0;
    for ($i = 0; $i < 10; $i++) {
        $soma += intval($cpf[$i]) * (11 - $i);
    }
    $digito2 = ($soma * 10) % 11;
    if ($digito2 == 10) $digito2 = 0;
    
    return ($digito1 == intval($cpf[9]) && $digito2 == intval($cpf[10]));
}

if (!validarCPF($cpf)) {
    $errors[] = "CPF inválido";
}

// Validar sexo - VALIDAÇÃO ADICIONADA
if (empty($sexo) || !in_array($sexo, ['Masculino', 'Feminino'])) {
    $errors[] = "Sexo deve ser selecionado";
}

// Validar celular (opcional)
if (!empty($celular) && strlen($celular) != 11) {
    $errors[] = "Celular deve ter 11 dígitos (DDD + número)";
}

// Validar CEP
if (empty($cep) || strlen($cep) != 8) {
    $errors[] = "CEP deve ter 8 dígitos";
}

// Validar endereço
if (empty($rua)) {
    $errors[] = "Rua é obrigatória";
}

if (empty($bairro)) {
    $errors[] = "Bairro é obrigatório";
}

if (empty($cidade)) {
    $errors[] = "Cidade é obrigatória";
}

if (empty($numero)) {
    $errors[] = "Número é obrigatório";
}

// Validar senha
if (empty($senha) || strlen($senha) < 6) {
    $errors[] = "Senha deve ter no mínimo 6 caracteres";
}

// Se houver erros, retornar
if (!empty($errors)) {
    echo json_encode([
        'success' => false,
        'message' => implode(", ", $errors)
    ]);
    exit;
}

// ============================================
// VERIFICAR SE CPF OU RG JÁ EXISTEM
// ============================================

$stmt = $sql->prepare("SELECT cpf, rg FROM cad_neurodivergentes WHERE cpf = ? OR rg = ?");
if (!$stmt) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro ao preparar consulta: ' . $sql->error
    ]);
    exit;
}

$stmt->bind_param("ss", $cpf, $rg);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    if ($row['cpf'] == $cpf) {
        echo json_encode([
            'success' => false,
            'message' => 'CPF já cadastrado!'
        ]);
        $stmt->close();
        $sql->close();
        exit;
    }
    if ($row['rg'] == $rg) {
        echo json_encode([
            'success' => false,
            'message' => 'RG já cadastrado!'
        ]);
        $stmt->close();
        $sql->close();
        exit;
    }
}
$stmt->close();

// ============================================
// PROCESSAR UPLOAD DA FOTO
// ============================================

$perfilbd = "";
$upload_success = true;
$upload_message = "";

if (isset($_FILES["perfil"]) && $_FILES["perfil"]["error"] == UPLOAD_ERR_OK) {
    $pasta = "img_neuro/";
    $voltar = "../";
    $arquivo = $_FILES["perfil"];
    
    // Validar tipo de arquivo
    $extensoes_permitidas = ['jpg', 'jpeg', 'png', 'gif'];
    $ext = strtolower(pathinfo($arquivo["name"], PATHINFO_EXTENSION));
    
    if (!in_array($ext, $extensoes_permitidas)) {
        echo json_encode([
            'success' => false,
            'message' => 'Apenas imagens JPG, PNG ou GIF são permitidas!'
        ]);
        exit;
    }
    
    // Validar tamanho (máximo 5MB)
    if ($arquivo["size"] > 5242880) {
        echo json_encode([
            'success' => false,
            'message' => 'Arquivo muito grande! Tamanho máximo: 5MB'
        ]);
        exit;
    }
    
    // Validar se é realmente uma imagem
    $check = getimagesize($arquivo["tmp_name"]);
    if ($check === false) {
        echo json_encode([
            'success' => false,
            'message' => 'O arquivo não é uma imagem válida!'
        ]);
        exit;
    }
    
    // Criar nome único do arquivo
    $perfilf = $cpf . '_' . time() . '.' . $ext;
    $perfilbd = $pasta . $perfilf;
    $caminho_completo = $voltar . $perfilbd;
    
    // Criar diretório se não existir
    if (!file_exists($voltar . $pasta)) {
        if (!mkdir($voltar . $pasta, 0755, true)) {
            echo json_encode([
                'success' => false,
                'message' => 'Erro ao criar diretório para imagens'
            ]);
            exit;
        }
    }
    
    // Mover arquivo
    if (!move_uploaded_file($arquivo['tmp_name'], $caminho_completo)) {
        $upload_success = false;
        $upload_message = "Aviso: Não foi possível fazer upload da imagem";
        $perfilbd = "";
    }
} elseif (isset($_FILES["perfil"]) && $_FILES["perfil"]["error"] != UPLOAD_ERR_NO_FILE) {
    $upload_errors = [
        UPLOAD_ERR_INI_SIZE => 'Arquivo muito grande (limite do servidor)',
        UPLOAD_ERR_FORM_SIZE => 'Arquivo muito grande',
        UPLOAD_ERR_PARTIAL => 'Upload incompleto',
        UPLOAD_ERR_NO_TMP_DIR => 'Diretório temporário não encontrado',
        UPLOAD_ERR_CANT_WRITE => 'Erro ao gravar arquivo',
        UPLOAD_ERR_EXTENSION => 'Extensão não permitida'
    ];
    
    $error_code = $_FILES["perfil"]["error"];
    $upload_message = "Aviso: " . (isset($upload_errors[$error_code]) ? $upload_errors[$error_code] : "Erro desconhecido no upload");
}

// ============================================
// HASH DA SENHA
// ============================================

$senha_hash = password_hash($senha, PASSWORD_DEFAULT);

// ============================================
// INSERIR DADOS NO BANCO - ATUALIZADO COM SEXO
// ============================================

$stmt = $sql->prepare("INSERT INTO cad_neurodivergentes (nome, email, rg, cpf, sexo, celular, cep, rua, bairro, cidade, numero, complemento, senha, perfil, id_responsa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

if (!$stmt) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro ao preparar inserção: ' . $sql->error
    ]);
    exit;
}

$stmt->bind_param("ssssssssssssssi", $nome, $email, $rg, $cpf, $sexo, $celular, $cep, $rua, $bairro, $cidade, $numero, $complemento, $senha_hash, $perfilbd, $id_responsa);

if ($stmt->execute()) {
    $id_inserido = $stmt->insert_id;
    
    $message = 'Cadastro do neurodivergente realizado com sucesso!';
    if (!empty($upload_message)) {
        $message .= ' ' . $upload_message;
    }
    
    echo json_encode([
        'success' => true,
        'message' => $message,
        'id' => $id_inserido,
        'id_responsa' => $id_responsa
    ]);
    
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Erro ao salvar dados: ' . $stmt->error
    ]);
}

$stmt->close();
$sql->close();
?>
