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

$id_responsavel = $_SESSION['id_responsavel'];

try {
    // Carregar apenas conversas NÃO deletadas
    $stmt = $sql->prepare("
        SELECT id_conversa, titulo, data_criacao, data_ultima_mensagem
        FROM conversas
        WHERE id_responsavel = ? AND deletada = 0
        ORDER BY data_ultima_mensagem DESC
    ");
    $stmt->bind_param("i", $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $conversas = [];
    while ($row = $result->fetch_assoc()) {
        $conversas[] = [
            'id_conversa' => $row['id_conversa'],
            'titulo' => $row['titulo'],
            'data_criacao' => $row['data_criacao'],
            'data_ultima_mensagem' => $row['data_ultima_mensagem']
        ];
    }
    
    echo json_encode([
        'sucesso' => true,
        'conversas' => $conversas
    ]);
    
    $stmt->close();
} catch (Exception $e) {
    echo json_encode([
        'sucesso' => false,
        'erro' => $e->getMessage()
    ]);
}

$sql->close();
?>
