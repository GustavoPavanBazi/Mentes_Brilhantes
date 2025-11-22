<?php
// INICIAR SESSÃO ANTES DE QUALQUER OUTPUT
session_start();

// Definir charset UTF-8
header('Content-Type: application/json; charset=utf-8');

// Função para retornar erro
function returnError($message) {
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'success' => false,
        'message' => $message
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

function returnSuccess($message, $data = []) {
    $_SESSION['cadastro_neurodivergente_sucesso'] = true;
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'success' => true,
        'message' => $message,
        'data' => $data
    ], JSON_UNESCAPED_UNICODE);
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
// BUSCAR ID DO RESPONSÁVEL
// ============================================
$id_responsa = null;

// OPÇÃO 1: Verificar se há sessão do responsável (id_responsavel)
if (isset($_SESSION['id_responsavel']) && !empty($_SESSION['id_responsavel'])) {
    $id_responsa = intval($_SESSION['id_responsavel']);
}
// OPÇÃO 2: Verificar se há sessão com id_responsa
elseif (isset($_SESSION['id_responsa']) && !empty($_SESSION['id_responsa'])) {
    $id_responsa = intval($_SESSION['id_responsa']);
}
// OPÇÃO 3: Receber via POST (quando o responsável acabou de se cadastrar)
elseif (isset($_POST['id_responsavel']) && !empty($_POST['id_responsavel'])) {
    $id_responsa = intval($_POST['id_responsavel']);
}

// Se não encontrou o ID do responsável de nenhuma forma
if (!$id_responsa) {
    returnError('ID do responsável não encontrado. Por favor, faça login ou forneça o ID do responsável.');
}

// Validar se o responsável realmente existe no banco
$stmt_check = $sql->prepare("SELECT id_responsa FROM cad_responsavel WHERE id_responsa = ?");
if (!$stmt_check) {
    returnError('Erro ao verificar responsável: ' . $sql->error);
}

$stmt_check->bind_param("i", $id_responsa);
if (!$stmt_check->execute()) {
    $stmt_check->close();
    returnError('Erro ao executar verificação do responsável');
}

$result_check = $stmt_check->get_result();
if ($result_check->num_rows == 0) {
    $stmt_check->close();
    returnError('Responsável não encontrado no sistema. ID: ' . $id_responsa);
}
$stmt_check->close();

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
$data_nascimento = sanitize($_POST["data_nascimento"] ?? ($_POST["dataNascimento"] ?? ""));
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
        if ($date > $hoje) {
            $errors[] = "Data de nascimento não pode ser no futuro";
        }
    }
}

// Validar RG (opcional - apenas se preenchido)
if (!empty($rg) && strlen($rg) < 7) {
    $errors[] = "RG deve ter no mínimo 7 dígitos";
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
if (!empty($celular) && strlen($celular) < 10) {
    $errors[] = "Celular deve ter pelo menos 10 dígitos";
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
    returnError(implode("; ", $errors));
}

// ============================================
// VERIFICAR SE CPF OU RG JÁ EXISTEM
// ============================================
$stmt_cpf = $sql->prepare("SELECT id_neuro FROM cad_neurodivergentes WHERE cpf = ?");
if (!$stmt_cpf) {
    returnError('Erro ao preparar consulta de CPF: ' . $sql->error);
}

$stmt_cpf->bind_param("s", $cpf);
if (!$stmt_cpf->execute()) {
    $stmt_cpf->close();
    returnError('Erro ao verificar CPF');
}

$stmt_cpf->store_result();
if ($stmt_cpf->num_rows > 0) {
    $stmt_cpf->close();
    returnError('CPF já cadastrado!');
}
$stmt_cpf->close();

// Verificar RG (somente se foi preenchido)
if (!empty($rg)) {
    $stmt_rg = $sql->prepare("SELECT id_neuro FROM cad_neurodivergentes WHERE rg = ?");
    if (!$stmt_rg) {
        returnError('Erro ao preparar consulta de RG: ' . $sql->error);
    }

    $stmt_rg->bind_param("s", $rg);
    $stmt_rg->execute();
    $result_rg = $stmt_rg->get_result();
    if ($result_rg->num_rows > 0) {
        $stmt_rg->close();
        returnError('RG já cadastrado!');
    }

    $stmt_rg->close();
}



// ============================================
// PROCESSAR UPLOAD DA FOTO (OPCIONAL)
// ============================================
$perfilbd = "";
$caminho_completo = "";

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
        returnError('Erro ao salvar a foto de perfil');
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
    
    $message = 'Cadastro do neurodivergente realizado com sucesso!';
    returnSuccess($message, [
        'id' => $id_inserido,
        'id_responsa' => $id_responsa,
        'foto_perfil' => $perfilbd
    ]);
} else {
    $error_msg = $stmt->error;
    $stmt->close();
    $sql->close();
    
    // Se houver erro no banco, deletar a foto que foi enviada
    if (!empty($caminho_completo) && file_exists($caminho_completo)) {
        unlink($caminho_completo);
    }
    
    returnError('Erro ao salvar dados no banco: ' . $error_msg);
}
?>
