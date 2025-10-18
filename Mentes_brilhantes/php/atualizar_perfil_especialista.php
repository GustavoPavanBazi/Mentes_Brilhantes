<?php
session_start();
header('Content-Type: application/json; charset=utf-8');

// Verificar se o usuário está logado
if (!isset($_SESSION['usuario_logado']) || $_SESSION['tipo_usuario'] !== 'especialista') {
    echo json_encode([
        'success' => false,
        'message' => 'Usuário não autenticado'
    ]);
    exit;
}

// Incluir conexão com o banco de dados
require_once 'conexao.php';

try {
    // Verificar se a requisição é POST
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Método de requisição inválido');
    }
    
    // Receber e sanitizar dados do formulário
    $id_usuario = $_SESSION['id_usuario'];
    $descricao = trim($_POST['descricao'] ?? '');
    
    // Validações
    if (empty($descricao)) {
        throw new Exception('A descrição não pode estar vazia');
    }
    
    if (strlen($descricao) < 50) {
        throw new Exception('A descrição deve ter no mínimo 50 caracteres');
    }
    
    if (strlen($descricao) > 1000) {
        throw new Exception('A descrição não pode ter mais de 1000 caracteres');
    }
    
    // Buscar primeiro para verificar se o especialista existe
    $stmt_check = $sql->prepare("SELECT id_especial FROM cad_especialista WHERE id_especial = ?");
    $stmt_check->bind_param("i", $id_usuario);
    $stmt_check->execute();
    $result_check = $stmt_check->get_result();
    
    if ($result_check->num_rows === 0) {
        throw new Exception('Especialista não encontrado');
    }
    
    $stmt_check->close();
    
    // Preparar query de atualização
    $stmt = $sql->prepare("
        UPDATE cad_especialista 
        SET descricao = ?
        WHERE id_especial = ?
    ");
    
    if (!$stmt) {
        throw new Exception('Erro ao preparar query: ' . $sql->error);
    }
    
    $stmt->bind_param("si", $descricao, $id_usuario);
    
    // Executar a atualização
    if (!$stmt->execute()) {
        throw new Exception('Erro ao executar atualização: ' . $stmt->error);
    }
    
    // Verificar se alguma linha foi afetada
    if ($stmt->affected_rows > 0) {
        $response = [
            'success' => true,
            'message' => 'Perfil atualizado com sucesso!'
        ];
    } else {
        // Verificar se os dados são os mesmos
        $stmt_verify = $sql->prepare("SELECT descricao FROM cad_especialista WHERE id_especial = ?");
        $stmt_verify->bind_param("i", $id_usuario);
        $stmt_verify->execute();
        $result_verify = $stmt_verify->get_result();
        $current_data = $result_verify->fetch_assoc();
        $stmt_verify->close();
        
        if ($current_data && $current_data['descricao'] === $descricao) {
            $response = [
                'success' => true,
                'message' => 'Nenhuma alteração foi necessária. Os dados já estão atualizados.'
            ];
        } else {
            throw new Exception('Erro: Nenhuma linha foi atualizada');
        }
    }
    
    $stmt->close();
    
    echo json_encode($response);
    
} catch (Exception $e) {
    error_log("Erro ao atualizar perfil - User ID: " . ($_SESSION['id_usuario'] ?? 'unknown') . " - " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
    
} finally {
    if (isset($sql) && $sql) {
        $sql->close();
    }
}
?>