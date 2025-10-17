<?php
// Iniciar sessão
session_start();

// Configurar para retornar JSON
header('Content-Type: application/json; charset=utf-8');

// Ativar exibição de erros para debug (remover em produção)
error_reporting(E_ALL);
ini_set('display_errors', 0); // Desabilitado para não interferir no JSON

// Função para retornar erro e parar execução
function returnError($message) {
    echo json_encode([
        'success' => false,
        'message' => $message
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// Função para retornar sucesso
function returnSuccess($message, $data = []) {
    echo json_encode(array_merge([
        'success' => true,
        'message' => $message
    ], $data), JSON_UNESCAPED_UNICODE);
    exit;
}

// Incluir arquivo de conexão
if (!file_exists("conexao.php")) {
    returnError('Arquivo de conexão não encontrado');
}

include "conexao.php";

// Verificar se o formulário foi enviado via POST
if ($_SERVER["REQUEST_METHOD"] != "POST") {
    returnError('Método de requisição inválido');
}

// Verificar conexão com banco de dados
if (!isset($sql) || $sql->connect_error) {
    returnError('Erro de conexão com o banco de dados');
}

// ============================================
// VERIFICAR SE RESPONSÁVEL ESTÁ LOGADO
// ============================================

$id_responsa = null;

// Verificar se há sessão do responsável
if (isset($_SESSION['id_responsa']) && !empty($_SESSION['id_responsa'])) {
    $id_responsa = intval($_SESSION['id_responsa']);
    
    // Verificar se o responsável existe no banco
    $stmt_check = $sql->prepare("SELECT id_responsa FROM cad_responsavel WHERE id_responsa = ?");
    $stmt_check->bind_param("i", $id_responsa);
    $stmt_check->execute();
    $result_check = $stmt_check->get_result();
    
    if ($result_check->num_rows == 0) {
        $id_responsa = null;
    }
    $stmt_check->close();
}

// Se não encontrou na sessão, pegar o último cadastrado (TEMPORÁRIO)
if (!$id_responsa) {
    $result = $sql->query("SELECT id_responsa FROM cad_responsavel ORDER BY id_responsa DESC LIMIT 1");
    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $id_responsa = intval($row['id_responsa']);
    }
}

// Se ainda não encontrou o ID do responsável
if (!$id_responsa) {
    returnError('Você precisa estar logado como responsável para cadastrar um neurodivergente. Por favor, faça login primeiro.');
}

// ============================================
// VERIFICAR SE COLUNA data_nascimento EXISTE
// ============================================

$columns_check = $sql->query("SHOW COLUMNS FROM cad_neurodivergentes LIKE 'data_nascimento'");
$has_data_nascimento = ($columns_check && $columns_check->num_rows > 0);

// ============================================
// SANITIZAÇÃO DOS DADOS
// ============================================

function sanitize($data) {
    if ($data === null) return "";
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data, ENT_QUOTES, 'UTF-8');
    return $data;
}

// Receber e sanitizar dados
$nome = sanitize($_POST["nome"] ?? "");
$email = filter_var(trim($_POST["email"] ?? ""), FILTER_SANITIZE_EMAIL);
$data_nascimento = sanitize($_POST["data_nascimento"] ?? "");
$rg = preg_replace('/[^0-9]/', '', $_POST["rg"] ?? "");
$cpf = preg_replace('/[^0-9]/', '', $_POST["cpf"] ?? "");
$sexo = sanitize($_POST["sexo"] ?? "");
$celular = preg_replace('/[^0-9]/', '', $_POST["celular"] ?? "");
$cep = preg_replace('/[^0-9]/', '', $_POST["cep"] ?? "");
$rua = sanitize($_POST["rua"] ?? "");
$bairro = sanitize($_POST["bairro"] ?? "");
$cidade = sanitize($_POST["cidade"] ?? "");
$numero = sanitize($_POST["numero"] ?? "");
$complemento = sanitize($_POST["complemento"] ?? "");
$senha = $_POST["senha"] ?? "";

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

// Validar data de nascimento
if ($has_data_nascimento && !empty($data_nascimento)) {
    $date = DateTime::createFromFormat('Y-m-d', $data_nascimento);
    if (!$date || $date->format('Y-m-d') !== $data_nascimento) {
        $errors[] = "Data de nascimento inválida";
    } else {
        $hoje = new DateTime();
        $idade = $hoje->diff($date)->y;
        if ($date > $hoje || $idade > 120) {
            $errors[] = "Data de nascimento inválida";
        }
    }
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

// Validar sexo
if (empty($sexo) || !in_array($sexo, ['Masculino', 'Feminino'])) {
    $errors[] = "Sexo deve ser selecionado";
}

// Validar celular (opcional)
if (!empty($celular) && strlen($celular) != 11) {
    $errors[] = "Celular deve ter 11 dígitos";
}

// Validar CEP
if (empty($cep) || strlen($cep) != 8) {
    $errors[] = "CEP deve ter 8 dígitos";
}

// Validar endereço
if (empty($rua)) $errors[] = "Rua é obrigatória";
if (empty($bairro)) $errors[] = "Bairro é obrigatório";
if (empty($cidade)) $errors[] = "Cidade é obrigatória";
if (empty($numero)) $errors[] = "Número é obrigatório";

// Validar senha
if (empty($senha) || strlen($senha) < 6) {
    $errors[] = "Senha deve ter no mínimo 6 caracteres";
}

// Se houver erros, retornar
if (!empty($errors)) {
    returnError(implode(", ", $errors));
}

// ============================================
// VERIFICAR SE CPF OU RG JÁ EXISTEM
// ============================================

$stmt = $sql->prepare("SELECT cpf, rg FROM cad_neurodivergentes WHERE cpf = ? OR rg = ?");
if (!$stmt) {
    returnError('Erro ao preparar consulta: ' . $sql->error);
}

$stmt->bind_param("ss", $cpf, $rg);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    if ($row['cpf'] == $cpf) {
        $stmt->close();
        returnError('CPF já cadastrado!');
    }
    if ($row['rg'] == $rg) {
        $stmt->close();
        returnError('RG já cadastrado!');
    }
}
$stmt->close();

// ============================================
// PROCESSAR UPLOAD DA FOTO
// ============================================

$perfilbd = "";
$upload_message = "";

if (isset($_FILES["perfil"]) && $_FILES["perfil"]["error"] == UPLOAD_ERR_OK) {
    $pasta = "img_neuro/";
    $voltar = "../";
    $arquivo = $_FILES["perfil"];
    
    // Validar tipo de arquivo
    $extensoes_permitidas = ['jpg', 'jpeg', 'png', 'gif'];
    $ext = strtolower(pathinfo($arquivo["name"], PATHINFO_EXTENSION));
    
    if (!in_array($ext, $extensoes_permitidas)) {
        returnError('Apenas imagens JPG, PNG ou GIF são permitidas!');
    }
    
    // Validar tamanho (máximo 5MB)
    if ($arquivo["size"] > 5242880) {
        returnError('Arquivo muito grande! Tamanho máximo: 5MB');
    }
    
    // Validar se é realmente uma imagem
    $check = getimagesize($arquivo["tmp_name"]);
    if ($check === false) {
        returnError('O arquivo não é uma imagem válida!');
    }
    
    // Criar nome único do arquivo
    $perfilf = $cpf . '_' . time() . '.' . $ext;
    $perfilbd = $pasta . $perfilf;
    $caminho_completo = $voltar . $perfilbd;
    
    // Criar diretório se não existir
    if (!file_exists($voltar . $pasta)) {
        if (!mkdir($voltar . $pasta, 0755, true)) {
            returnError('Erro ao criar diretório para imagens');
        }
    }
    
    // Mover arquivo
    if (!move_uploaded_file($arquivo['tmp_name'], $caminho_completo)) {
        $upload_message = " (Imagem não pôde ser salva)";
        $perfilbd = "";
    }
}

// ============================================
// HASH DA SENHA
// ============================================

$senha_hash = password_hash($senha, PASSWORD_DEFAULT);

// ============================================
// INSERIR DADOS NO BANCO
// ============================================

// Preparar query com ou sem data_nascimento
if ($has_data_nascimento && !empty($data_nascimento)) {
    $sql_insert = "INSERT INTO cad_neurodivergentes (nome, email, data_nascimento, rg, cpf, sexo, celular, cep, rua, bairro, cidade, numero, complemento, senha, perfil, id_responsa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $sql->prepare($sql_insert);
    
    if (!$stmt) {
        returnError('Erro ao preparar inserção: ' . $sql->error);
    }
    
    $stmt->bind_param("sssssssssssssssi", $nome, $email, $data_nascimento, $rg, $cpf, $sexo, $celular, $cep, $rua, $bairro, $cidade, $numero, $complemento, $senha_hash, $perfilbd, $id_responsa);
} else {
    $sql_insert = "INSERT INTO cad_neurodivergentes (nome, email, rg, cpf, sexo, celular, cep, rua, bairro, cidade, numero, complemento, senha, perfil, id_responsa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $sql->prepare($sql_insert);
    
    if (!$stmt) {
        returnError('Erro ao preparar inserção: ' . $sql->error);
    }
    
    $stmt->bind_param("ssssssssssssssi", $nome, $email, $rg, $cpf, $sexo, $celular, $cep, $rua, $bairro, $cidade, $numero, $complemento, $senha_hash, $perfilbd, $id_responsa);
}

if ($stmt->execute()) {
    $id_inserido = $stmt->insert_id;
    $stmt->close();
    $sql->close();
    
    $message = 'Cadastro do neurodivergente realizado com sucesso!' . $upload_message;
    
    returnSuccess($message, [
        'id' => $id_inserido,
        'id_responsa' => $id_responsa
    ]);
} else {
    $error_msg = $stmt->error;
    $stmt->close();
    $sql->close();
    returnError('Erro ao salvar dados: ' . $error_msg);
}
?>
