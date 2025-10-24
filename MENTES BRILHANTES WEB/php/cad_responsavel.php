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
// SANITIZAÇÃO E VALIDAÇÃO DOS DADOS
// ============================================

// Função para sanitizar strings
function sanitize($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}

// Receber e sanitizar dados do formulário
$nome = sanitize($_POST["nome"]);
$email = filter_var(trim($_POST["email"]), FILTER_SANITIZE_EMAIL);
$dataNascimento = isset($_POST["dataNascimento"]) ? $_POST["dataNascimento"] : "";
$cpf = preg_replace('/[^0-9]/', '', $_POST["cpf"]); // Remove formatação
$celular = preg_replace('/[^0-9]/', '', $_POST["celular"]);
$sexo = isset($_POST["sexo"]) ? sanitize($_POST["sexo"]) : "";
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

// Array para armazenar erros
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
if (empty($dataNascimento)) {
    $errors[] = "Data de nascimento é obrigatória";
} else {
    // Validar formato da data
    $date = DateTime::createFromFormat('Y-m-d', $dataNascimento);
    if (!$date || $date->format('Y-m-d') !== $dataNascimento) {
        $errors[] = "Data de nascimento inválida";
    } else {
        // Verificar se a pessoa tem pelo menos 18 anos
        $today = new DateTime();
        $age = $today->diff($date)->y;
        if ($age < 18) {
            $errors[] = "Você deve ter pelo menos 18 anos para se cadastrar como responsável";
        }
    }
}

// Validar CPF
if (empty($cpf) || strlen($cpf) != 11) {
    $errors[] = "CPF deve ter 11 dígitos";
}

// Validar CPF (algoritmo)
function validarCPF($cpf) {
    if (strlen($cpf) != 11) return false;
    
    // Verifica sequências iguais
    if (preg_match('/(\d)\1{10}/', $cpf)) return false;
    
    // Valida primeiro dígito verificador
    $soma = 0;
    for ($i = 0; $i < 9; $i++) {
        $soma += intval($cpf[$i]) * (10 - $i);
    }
    $digito1 = ($soma * 10) % 11;
    if ($digito1 == 10) $digito1 = 0;
    
    // Valida segundo dígito verificador
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

// Validar celular
if (empty($celular) || strlen($celular) != 11) {
    $errors[] = "Celular deve ter 11 dígitos (DDD + número)";
}

// Validar sexo (opcional)
$sexosValidos = ['Masculino', 'Feminino', ''];
if (!in_array($sexo, $sexosValidos)) {
    $errors[] = "Sexo inválido";
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
// VERIFICAR SE CPF OU EMAIL JÁ EXISTEM
// ============================================

$stmt = $sql->prepare("SELECT cpf, email FROM cad_responsavel WHERE cpf = ? OR email = ?");
if (!$stmt) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro ao preparar consulta: ' . $sql->error
    ]);
    exit;
}

$stmt->bind_param("ss", $cpf, $email);
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
    if ($row['email'] == $email) {
        echo json_encode([
            'success' => false,
            'message' => 'Email já cadastrado!'
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
    $pasta = "img_cad/";
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
        $perfilbd = ""; // Limpar caminho se falhou
    }
} elseif (isset($_FILES["perfil"]) && $_FILES["perfil"]["error"] != UPLOAD_ERR_NO_FILE) {
    // Se houve erro no upload (exceto "nenhum arquivo")
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
// INSERIR DADOS NO BANCO
// ============================================

// Preparar valores NULL para campos opcionais vazios
$sexo_value = !empty($sexo) ? $sexo : NULL;
$dataNascimento_value = !empty($dataNascimento) ? $dataNascimento : NULL;

$stmt = $sql->prepare("INSERT INTO cad_responsavel (nome, email, data_nascimento, cpf, sexo, celular, cep, rua, bairro, cidade, numero, complemento, senha, perfil) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

if (!$stmt) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro ao preparar inserção: ' . $sql->error
    ]);
    exit;
}

$stmt->bind_param("ssssssssssssss", $nome, $email, $dataNascimento_value, $cpf, $sexo_value, $celular, $cep, $rua, $bairro, $cidade, $numero, $complemento, $senha_hash, $perfilbd);

if ($stmt->execute()) {
    // Pegar ID do usuário inserido
    $id_inserido = $stmt->insert_id;
    
    // Preparar mensagem de sucesso
    $message = 'Cadastro realizado com sucesso!';
    if (!empty($upload_message)) {
        $message .= ' ' . $upload_message;
    }
    
    echo json_encode([
        'success' => true,
        'message' => $message,
        'id' => $id_inserido
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