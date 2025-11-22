<?php
session_start();
header('Content-Type: application/json; charset=utf-8');

require_once 'conexao.php';

// Validação de requisição POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array('status' => 'error', 'message' => 'Método não permitido'));
    exit;
}

// Receber dados JSON
$json = file_get_contents('php://input');
$dados = json_decode($json, true);

// Validar se JSON é válido
if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode(array('status' => 'error', 'message' => 'JSON inválido'));
    exit;
}

// Extrair e validar campos
$inputEmail = isset($dados['email']) ? trim($dados['email']) : '';
$inputPass = isset($dados['senha']) ? trim($dados['senha']) : '';

// Verificar se campos estão vazios
if (empty($inputEmail) || empty($inputPass)) {
    http_response_code(400);
    echo json_encode(array('status' => 'error', 'message' => 'Email e senha são obrigatórios'));
    exit;
}

// Preparar consulta SQL
$sql_query = "SELECT id_admin, email, senha FROM cad_administrador WHERE email = ?";
$stmt = mysqli_prepare($sql, $sql_query);

if (!$stmt) {
    http_response_code(500);
    echo json_encode(array('status' => 'error', 'message' => 'Falha ao preparar consulta'));
    exit;
}

// Bind e executar
mysqli_stmt_bind_param($stmt, "s", $inputEmail);

if (!mysqli_stmt_execute($stmt)) {
    http_response_code(500);
    echo json_encode(array('status' => 'error', 'message' => 'Falha ao executar consulta'));
    exit;
}

// Obter resultado
$admin = null;
$res = mysqli_stmt_get_result($stmt);
if ($res && mysqli_num_rows($res) === 1) {
    $admin = mysqli_fetch_assoc($res);
}

// Verificar credenciais
if (!$admin || !password_verify($inputPass, $admin['senha'])) {
    http_response_code(401);
    echo json_encode(array('status' => 'error', 'message' => 'Email ou senha inválidos'));
    exit;
}

// Criar sessão
$_SESSION['admin_logado'] = true;
$_SESSION['admin_id'] = $admin['id_admin'];
$_SESSION['admin_email'] = $admin['email'];
$_SESSION['tipo_usuario'] = 'administrador';

// Resposta de sucesso
$data = array(
    'id' => $admin['id_admin'],
    'email' => $admin['email'],
    'tipo' => 'administrador'
);

http_response_code(200);
echo json_encode(array('data' => array($data), 'status' => 'success', 'message' => 'Login realizado com sucesso'));

mysqli_stmt_close($stmt);
mysqli_close($sql);
?>
