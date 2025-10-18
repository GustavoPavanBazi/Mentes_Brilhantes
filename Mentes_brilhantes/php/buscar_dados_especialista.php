<?php
session_start();
header('Content-Type: application/json; charset=utf-8'); // ✅ ADICIONAR ESTA LINHA

include "conexao.php";

// Verifica se o usuário está logado e é um especialista
if (!isset($_SESSION['usuario_logado']) || $_SESSION['tipo_usuario'] !== 'especialista') {
    echo json_encode(['success' => false, 'message' => 'Usuário não autenticado']);
    exit();
}

// Pega o ID do especialista da sessão
$id_especialista = $_SESSION['id_usuario'];

// Busca os dados completos do especialista
$query = "SELECT * FROM cad_especialista WHERE id_especial = ?";
$stmt = $sql->prepare($query);
$stmt->bind_param("i", $id_especialista);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $especialista = $result->fetch_assoc();
    
    // Formata os dados para retornar
    $dados = [
        'success' => true,
        'nome' => $especialista['nome'],
        'email' => $especialista['email'],
        'cpf' => $especialista['cpf'],
        'data_nascimento' => $especialista['data_nascimento'],
        'sexo' => $especialista['sexo'],
        'formacao' => $especialista['formacao'],
        'descricao' => $especialista['descricao'] ?? '',
        'perfil' => $especialista['perfil'] ?? ''
    ];
    
    // Adiciona o tratamento (Dr./Dra.) baseado no sexo
    if ($especialista['sexo'] === 'Masculino') {
        $dados['tratamento'] = 'Dr.';
    } else {
        $dados['tratamento'] = 'Dra.';
    }
    
    // Pega as iniciais do nome para o avatar
    $nomes = explode(' ', $especialista['nome']);
    $iniciais = '';
    if (count($nomes) >= 2) {
        $iniciais = strtoupper(substr($nomes[0], 0, 1) . substr($nomes[count($nomes) - 1], 0, 1));
    } else {
        $iniciais = strtoupper(substr($nomes[0], 0, 2));
    }
    $dados['iniciais'] = $iniciais;
    
    // Formata a data de nascimento para exibição
    $dados['data_nascimento_formatada'] = date('d/m/Y', strtotime($especialista['data_nascimento']));
    
    // Calcula a idade
    $hoje = new DateTime();
    $nascimento = new DateTime($especialista['data_nascimento']);
    $dados['idade'] = $hoje->diff($nascimento)->y;
    
    echo json_encode($dados);
} else {
    echo json_encode(['success' => false, 'message' => 'Especialista não encontrado']);
}

$stmt->close();
$sql->close();
?>