<?php
session_start();
require_once('conexao.php');

// Verificar autenticação
if (!isset($_SESSION['id_responsavel'])) {
    echo json_encode([
        'sucesso' => false,
        'erro' => 'Usuário não autenticado'
    ]);
    exit;
}

// Verificar método POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        'sucesso' => false,
        'erro' => 'Método inválido'
    ]);
    exit;
}

// Receber dados JSON
$json = file_get_contents('php://input');
$dados = json_decode($json, true);

if (!isset($dados['id_conversa']) || empty($dados['id_conversa'])) {
    echo json_encode([
        'sucesso' => false,
        'erro' => 'ID da conversa não fornecido'
    ]);
    exit;
}

$id_conversa = intval($dados['id_conversa']);
$id_responsavel = $_SESSION['id_responsavel'];

try {
    // Verificar se a conversa pertence ao usuário
    $stmt = $sql->prepare("SELECT id_conversa FROM conversas WHERE id_conversa = ? AND id_responsavel = ?");
    $stmt->bind_param("ii", $id_conversa, $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        echo json_encode([
            'sucesso' => false,
            'erro' => 'Conversa não encontrada ou acesso negado'
        ]);
        exit;
    }
    
    $stmt->close();
    
    // SOFT DELETE: Apenas marcar como deletada, não apagar do banco
    $stmt = $sql->prepare("UPDATE conversas SET deletada = 1 WHERE id_conversa = ?");
    $stmt->bind_param("i", $id_conversa);
    
    if ($stmt->execute()) {
        echo json_encode([
            'sucesso' => true,
            'mensagem' => 'Conversa ocultada com sucesso'
        ]);
    } else {
        throw new Exception('Erro ao ocultar conversa');
    }
    
    $stmt->close();
} catch (Exception $e) {
    echo json_encode([
        'sucesso' => false,
        'erro' => $e->getMessage()
    ]);
}

$sql->close();
?>
