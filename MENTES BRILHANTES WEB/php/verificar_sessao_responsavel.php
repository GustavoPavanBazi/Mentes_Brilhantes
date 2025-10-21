<?php
// Iniciar sessão
session_start();

// Ativar exibição de erros para debug
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Definir cabeçalho JSON
header('Content-Type: application/json');

// Incluir arquivo de conexão
include "conexao.php";

// Verificar se o usuário está logado
if (!isset($_SESSION['id_responsavel'])) {
    echo json_encode([
        'success' => false,
        'logado' => false,
        'message' => 'Usuário não autenticado'
    ]);
    exit;
}

// Verificar conexão com banco de dados
if ($sql->connect_error) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro de conexão com o banco de dados'
    ]);
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'];
// Buscar dados básicos do responsável
$stmt = $sql->prepare("SELECT id_responsa, nome, email, perfil FROM cad_responsavel WHERE id_responsa = ?");


if (!$stmt) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro ao preparar consulta'
    ]);
    exit;
}

$stmt->bind_param("i", $id_responsavel);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $dados = $result->fetch_assoc();
    
    // Obter iniciais do nome
    $nomes = explode(' ', trim($dados['nome']));
    if (count($nomes) >= 2) {
        $iniciais = strtoupper(substr($nomes[0], 0, 1) . substr($nomes[count($nomes) - 1], 0, 1));
    } else {
        $iniciais = strtoupper(substr($dados['nome'], 0, 1));
    }
    
    // Obter primeiro nome
    $primeiro_nome = $nomes[0];
    
    echo json_encode([
        'success' => true,
        'logado' => true,
        'dados' => [
            'id' => $dados['id_responsa'],
            'nome' => $dados['nome'],
            'primeiro_nome' => $primeiro_nome,
            'email' => $dados['email'],
            'perfil' => $dados['perfil'],
            'iniciais' => $iniciais
        ]
    ]);
    
} else {
    echo json_encode([
        'success' => false,
        'logado' => false,
        'message' => 'Responsável não encontrado'
    ]);
}

$stmt->close();
$sql->close();
?>