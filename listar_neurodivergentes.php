<?php
session_start();
header('Content-Type: application/json');

include "conexao.php";

if (!isset($_SESSION['id_responsavel'])) {
    echo json_encode(['success' => false, 'message' => 'Não autenticado']);
    exit;
}

$id_responsavel = intval($_SESSION['id_responsavel']);

// ============================================
// BUSCAR TODOS (cadastrados + vinculados)
// ============================================
$query = "
    SELECT DISTINCT
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
        n.perfil,
        CASE 
            WHEN n.id_responsa = ? THEN 'principal'
            ELSE 'secundario'
        END as tipo_vinculo
    FROM cad_neurodivergentes n
    LEFT JOIN vinculo_responsavel_neurodivergente v 
        ON n.id_neuro = v.id_neuro AND v.id_responsa = ?
    WHERE n.id_responsa = ? OR v.id_responsa = ?
    ORDER BY n.nome ASC
";

$stmt = $sql->prepare($query);

if (!$stmt) {
    echo json_encode(['success' => false, 'message' => 'Erro ao preparar consulta: ' . $sql->error]);
    exit;
}

$stmt->bind_param('iiii', $id_responsavel, $id_responsavel, $id_responsavel, $id_responsavel);

if (!$stmt->execute()) {
    echo json_encode(['success' => false, 'message' => 'Erro ao executar consulta: ' . $stmt->error]);
    exit;
}

$result = $stmt->get_result();
$neurodivergentes = [];

while ($row = $result->fetch_assoc()) {
    // Calcular idade
    if (!empty($row['data_nascimento'])) {
        $data_nascimento = new DateTime($row['data_nascimento']);
        $hoje = new DateTime();
        $idade = $hoje->diff($data_nascimento)->y;
    } else {
        $idade = 0;
    }
    
    // Formatar dados
    $row['idade'] = $idade;
    $row['iniciais'] = strtoupper(substr($row['nome'], 0, 1) . (strpos($row['nome'], ' ') !== false ? substr(explode(' ', $row['nome'])[1], 0, 1) : ''));
    $row['data_nascimento_formatada'] = !empty($row['data_nascimento']) ? date('d/m/Y', strtotime($row['data_nascimento'])) : '';
    $row['cpf_formatado'] = substr($row['cpf'], 0, 3) . '.' . substr($row['cpf'], 3, 3) . '.' . substr($row['cpf'], 6, 3) . '-' . substr($row['cpf'], 9);
    $row['rg_formatado'] = $row['rg'];
    $row['cep_formatado'] = substr($row['cep'], 0, 5) . '-' . substr($row['cep'], 5);
    $row['celular_formatado'] = !empty($row['celular']) ? '(' . substr($row['celular'], 0, 2) . ') ' . substr($row['celular'], 2, 5) . '-' . substr($row['celular'], 7) : '';
    
    $neurodivergentes[] = $row;
}

$stmt->close();

echo json_encode([
    'success' => true,
    'neurodivergentes' => $neurodivergentes,
    'total' => count($neurodivergentes)
], JSON_UNESCAPED_UNICODE);
?>
