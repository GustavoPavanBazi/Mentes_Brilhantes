<?php
session_start();
require_once('conexao.php');

header('Content-Type: application/json');

// Verificar autenticação
if (!isset($_SESSION['usuario_logado']) || $_SESSION['tipo_usuario'] !== 'responsavel') {
    echo json_encode([
        'sucesso' => false,
        'erro' => 'Usuário não autenticado'
    ]);
    exit;
}

// Verificar se o ID da conversa foi fornecido
if (!isset($_GET['id_conversa']) || empty($_GET['id_conversa'])) {
    echo json_encode([
        'sucesso' => false,
        'erro' => 'ID da conversa não fornecido'
    ]);
    exit;
}

$id_conversa = intval($_GET['id_conversa']);
$id_responsavel = $_SESSION['id_responsavel'];

try {
    // Verificar se a conversa pertence ao usuário
    $stmt = $sql->prepare("SELECT id_conversa, titulo FROM conversas WHERE id_conversa = ? AND id_responsavel = ?");
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
    
    $conversa = $result->fetch_assoc();
    $stmt->close();
    
    // Buscar todas as mensagens da conversa
    $stmt = $sql->prepare("
        SELECT 
            id_mensagem,
            remetente,
            conteudo,
            data_envio
        FROM mensagens
        WHERE id_conversa = ?
        ORDER BY data_envio ASC
    ");
    
    $stmt->bind_param("i", $id_conversa);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $mensagens = [];
    while ($row = $result->fetch_assoc()) {
        $mensagens[] = [
            'id_mensagem' => $row['id_mensagem'],
            'remetente' => $row['remetente'],
            'conteudo' => $row['conteudo'],
            'data_envio' => $row['data_envio']
        ];
    }
    
    $stmt->close();
    
    echo json_encode([
        'sucesso' => true,
        'conversa' => $conversa,
        'mensagens' => $mensagens,
        'total' => count($mensagens)
    ]);
    
} catch (Exception $e) {
    echo json_encode([
        'sucesso' => false,
        'erro' => $e->getMessage()
    ]);
}

$sql->close();
?>
