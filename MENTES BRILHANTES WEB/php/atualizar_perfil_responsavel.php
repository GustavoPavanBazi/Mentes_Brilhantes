<?php
session_start();

// Verificar se está logado
if (!isset($_SESSION['id_responsavel'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Usuário não autenticado'
    ]);
    exit;
}

// Incluir conexão com o banco de dados
include "conexao.php";

// Verificar conexão
if ($sql->connect_error) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro de conexão com o banco de dados'
    ]);
    exit;
}

// Verificar se os dados foram enviados
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        'success' => false,
        'message' => 'Método não permitido'
    ]);
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'];

try {
    // Receber dados do formulário
    $nome = trim($_POST['nome'] ?? '');
    $email = trim($_POST['email'] ?? '');
    $celular = trim($_POST['celular'] ?? '');
    $cpf = trim($_POST['cpf'] ?? '');
    $data_nascimento = trim($_POST['data_nascimento'] ?? '');
    $rua = trim($_POST['rua'] ?? '');
    $numero = trim($_POST['numero'] ?? '');
    $complemento = trim($_POST['complemento'] ?? '');
    $bairro = trim($_POST['bairro'] ?? '');
    $cidade = trim($_POST['cidade'] ?? '');
    $cep = trim($_POST['cep'] ?? '');
    
    // Validações básicas
    if (empty($nome)) {
        throw new Exception('O nome é obrigatório');
    }
    
    if (empty($email)) {
        throw new Exception('O email é obrigatório');
    }
    
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        throw new Exception('Email inválido');
    }
    
    // Verificar se o email já está em uso por outro responsável
    $stmt = $sql->prepare("SELECT id_responsa FROM cad_responsavel WHERE email = ? AND id_responsa != ?");
    $stmt->bind_param("si", $email, $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        throw new Exception('Este email já está em uso por outro usuário');
    }
    $stmt->close();
    
    // Limpar formatação dos campos
    $celular_limpo = preg_replace('/\D/', '', $celular);
    $cpf_limpo = preg_replace('/\D/', '', $cpf);
    $cep_limpo = preg_replace('/\D/', '', $cep);
    
    // Validar CPF
    if (!empty($cpf_limpo) && strlen($cpf_limpo) !== 11) {
        throw new Exception('CPF inválido');
    }
    
    // Validar celular
    if (!empty($celular_limpo) && strlen($celular_limpo) !== 11) {
        throw new Exception('Celular inválido. Use o formato (11) 99999-9999');
    }
    
    // Validar CEP
    if (!empty($cep_limpo) && strlen($cep_limpo) !== 8) {
        throw new Exception('CEP inválido');
    }
    
    // Converter data de DD/MM/AAAA para AAAA-MM-DD
    $data_bd = null;
    if (!empty($data_nascimento)) {
        $data = DateTime::createFromFormat('d/m/Y', $data_nascimento);
        if ($data) {
            $data_bd = $data->format('Y-m-d');
        }
    }
    
    // Atualizar dados no banco
    $stmt = $sql->prepare("
        UPDATE cad_responsavel 
        SET nome = ?,
            email = ?,
            celular = ?,
            cpf = ?,
            data_nascimento = ?,
            rua = ?,
            numero = ?,
            complemento = ?,
            bairro = ?,
            cidade = ?,
            cep = ?
        WHERE id_responsa = ?
    ");
    
    if (!$stmt) {
        throw new Exception('Erro ao preparar atualização: ' . $sql->error);
    }
    
    $stmt->bind_param(
        "sssssssssssi",
        $nome,
        $email,
        $celular_limpo,
        $cpf_limpo,
        $data_bd,
        $rua,
        $numero,
        $complemento,
        $bairro,
        $cidade,
        $cep_limpo,
        $id_responsavel
    );
    
    if (!$stmt->execute()) {
        throw new Exception('Erro ao atualizar dados: ' . $stmt->error);
    }
    
    if ($stmt->affected_rows === 0) {
        // Pode não ter alterado nada se os dados forem iguais
        echo json_encode([
            'success' => true,
            'message' => 'Nenhuma alteração foi detectada'
        ]);
    } else {
        echo json_encode([
            'success' => true,
            'message' => 'Perfil atualizado com sucesso!'
        ]);
    }
    
    $stmt->close();
    
} catch (Exception $e) {
    error_log("Erro ao atualizar perfil do responsável - ID: " . $id_responsavel . " - " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}

$sql->close();
?>
