<?php
session_start();

// Verificar se o usuário está autenticado
if (!isset($_SESSION['id_responsa']) && !isset($_SESSION['id_responsavel'])) {
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

$id_responsavel = $_SESSION['id_responsavel'] ?? $_SESSION['id_responsa'] ?? null;

// Validar se o ID do responsável existe
if (!$id_responsavel) {
    echo json_encode([
        'success' => false,
        'message' => 'ID do responsável não encontrado na sessão'
    ]);
    exit;
}

try {
    // Receber ID do neurodivergente
    $id_neuro = intval($_GET['id_neuro'] ?? 0);
    
    // Validação básica
    if ($id_neuro <= 0) {
        throw new Exception('ID do neurodivergente inválido');
    }
    
    // Buscar dados do neurodivergente específico
    $stmt = $sql->prepare("
        SELECT 
            n.id_neuro,
            n.nome,
            n.email,
            n.data_nascimento,
            n.rg,
            n.cpf,
            n.sexo,
            n.celular,
            n.cep,
            n.rua,
            n.bairro,
            n.cidade,
            n.numero,
            n.complemento,
            n.perfil
        FROM cad_neurodivergentes n
        WHERE n.id_neuro = ? AND n.id_responsa = ?
    ");
    
    if (!$stmt) {
        throw new Exception('Erro ao preparar consulta: ' . $sql->error);
    }
    
    $stmt->bind_param("ii", $id_neuro, $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        throw new Exception('Neurodivergente não encontrado ou você não tem permissão');
    }
    
    $dados = $result->fetch_assoc();
    
    // Formatar data de nascimento: DD/MM/AAAA
    if (!empty($dados['data_nascimento'])) {
        $data = DateTime::createFromFormat('Y-m-d', $dados['data_nascimento']);
        if ($data) {
            $dados['data_nascimento_formatada'] = $data->format('d/m/Y');
        } else {
            $dados['data_nascimento_formatada'] = $dados['data_nascimento'];
        }
    } else {
        $dados['data_nascimento_formatada'] = '';
    }
    
    echo json_encode([
        'success' => true,
        'dados' => $dados
    ], JSON_UNESCAPED_UNICODE);
    
    $stmt->close();
    
} catch (Exception $e) {
    error_log("Erro ao buscar neurodivergente por ID - " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}

$sql->close();
?>
