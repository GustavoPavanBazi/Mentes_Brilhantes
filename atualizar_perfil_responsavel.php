<?php
session_start();

// Incluir conexão com o banco de dados
$conexao_path = __DIR__ . '/conexao.php';
if (file_exists($conexao_path)) {
    require_once($conexao_path);
} else {
    echo json_encode(['success' => false, 'message' => 'Erro: Arquivo conexao.php não encontrado']);
    exit;
}

// Verificar se a conexão foi estabelecida
if (!isset($sql)) {
    echo json_encode(['success' => false, 'message' => 'Erro de conexão com banco de dados']);
    exit;
}

// Verificar autenticação
if (!isset($_SESSION['usuario_logado']) || $_SESSION['tipo_usuario'] != 'responsavel') {
    echo json_encode(['success' => false, 'message' => 'Usuário não autenticado']);
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'];

// PROCESSAR ATUALIZAÇÃO DE CELULAR
if (isset($_POST['celular']) && !empty($_POST['celular'])) {
    
    $celular = $_POST['celular'];
    
    // Remover formatação do celular
    $celular_limpo = preg_replace('/[^0-9]/', '', $celular);
    
    // Validar formato (11 dígitos)
    if (strlen($celular_limpo) != 11) {
        echo json_encode(['success' => false, 'message' => 'Celular inválido! Use o formato (11) 99999-9999']);
        exit;
    }
    
    // Atualizar celular no banco
    $stmt = $sql->prepare("UPDATE cad_responsavel SET celular = ? WHERE id_responsa = ?");
    $stmt->bind_param("si", $celular_limpo, $id_responsavel);
    
    if ($stmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Celular atualizado com sucesso!'
        ]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Erro ao atualizar celular: ' . $stmt->error]);
    }
    
    $stmt->close();
    $sql->close();
    exit;
}

// PROCESSAR UPLOAD DE FOTO
if (isset($_FILES['perfil']) && $_FILES['perfil']['error'] === UPLOAD_ERR_OK) {
    
    $file = $_FILES['perfil'];
    $file_name = $file['name'];
    $file_tmp = $file['tmp_name'];
    $file_size = $file['size'];
    
    // Validar tipo de arquivo
    $allowed_types = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    $file_type = mime_content_type($file_tmp);
    
    if (!in_array($file_type, $allowed_types)) {
        echo json_encode(['success' => false, 'message' => 'Tipo de arquivo inválido! Use apenas JPG, PNG ou GIF']);
        exit;
    }
    
    // Validar tamanho (5MB = 5242880 bytes)
    if ($file_size > 5242880) {
        echo json_encode(['success' => false, 'message' => 'Arquivo muito grande! Tamanho máximo: 5MB']);
        exit;
    }
    
    // Diretório para salvar as fotos
    $upload_dir = __DIR__ . '/../uploads/responsaveis/';
    
    // Criar diretório se não existir
    if (!is_dir($upload_dir)) {
        if (!mkdir($upload_dir, 0777, true)) {
            echo json_encode(['success' => false, 'message' => 'Erro ao criar diretório de upload']);
            exit;
        }
    }
    
    // Buscar foto antiga para deletar
    $stmt = $sql->prepare("SELECT perfil FROM cad_responsavel WHERE id_responsa = ?");
    $stmt->bind_param("i", $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $dados = $result->fetch_assoc();
        $foto_antiga = $dados['perfil'];
        
        // Deletar foto antiga se existir
        if (!empty($foto_antiga) && file_exists(__DIR__ . '/../' . $foto_antiga)) {
            unlink(__DIR__ . '/../' . $foto_antiga);
        }
    }
    $stmt->close();
    
    // Gerar nome único para o arquivo
    $extensao = pathinfo($file_name, PATHINFO_EXTENSION);
    $novo_nome = 'responsavel_' . $id_responsavel . '_' . time() . '.' . $extensao;
    $caminho_completo = $upload_dir . $novo_nome;
    $caminho_banco = 'uploads/responsaveis/' . $novo_nome;
    
    // Mover arquivo para o diretório
    if (move_uploaded_file($file_tmp, $caminho_completo)) {
        
        // Atualizar no banco de dados
        $stmt = $sql->prepare("UPDATE cad_responsavel SET perfil = ? WHERE id_responsa = ?");
        $stmt->bind_param("si", $caminho_banco, $id_responsavel);
        
        if ($stmt->execute()) {
            echo json_encode([
                'success' => true,
                'message' => 'Foto atualizada com sucesso!',
                'foto_url' => $caminho_banco
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Erro ao atualizar no banco de dados: ' . $stmt->error]);
        }
        
        $stmt->close();
        
    } else {
        echo json_encode(['success' => false, 'message' => 'Erro ao mover arquivo para o servidor']);
    }
    
} else {
    echo json_encode(['success' => false, 'message' => 'Nenhuma operação válida foi solicitada']);
}

// Fechar conexão
$sql->close();
?>
