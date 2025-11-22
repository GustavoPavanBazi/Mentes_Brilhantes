<?php
session_start();
require_once 'conexao.php';

// Verificar autenticação
if (!isset($_SESSION['id_responsavel']) || $_SESSION['tipo_usuario'] !== 'responsavel') {
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

// Validar dados recebidos
if (!isset($dados['mensagem']) || empty(trim($dados['mensagem']))) {
    echo json_encode([
        'sucesso' => false,
        'erro' => 'Mensagem vazia'
    ]);
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'];
$mensagem_usuario = trim($dados['mensagem']);
$id_conversa = isset($dados['id_conversa']) ? intval($dados['id_conversa']) : null;

try {
    // Iniciar transação
    $sql->begin_transaction();
    
    // Se não há conversa ativa, criar uma nova
    if (empty($id_conversa)) {
        // Gerar título baseado na primeira mensagem
        $titulo = mb_substr($mensagem_usuario, 0, 50);
        if (mb_strlen($mensagem_usuario) > 50) {
            $titulo .= '...';
        }
        
        $stmt = $sql->prepare("INSERT INTO conversas (id_responsavel, titulo, data_criacao, data_ultima_mensagem) VALUES (?, ?, NOW(), NOW())");
        $stmt->bind_param("is", $id_responsavel, $titulo);
        if (!$stmt->execute()) {
            throw new Exception('Erro ao criar conversa');
        }
        
        $id_conversa = $sql->insert_id;
        $stmt->close();
    } else {
        // Verificar se a conversa pertence ao usuário logado
        $stmt = $sql->prepare("SELECT id_conversa FROM conversas WHERE id_conversa = ? AND id_responsavel = ?");
        $stmt->bind_param("ii", $id_conversa, $id_responsavel);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows === 0) {
            throw new Exception('Conversa não encontrada ou acesso negado');
        }
        
        $stmt->close();
    }
    
    // Salvar mensagem do usuário
    $remetente_usuario = 'usuario';
    $stmt = $sql->prepare("INSERT INTO mensagens (id_conversa, remetente, conteudo, data_envio) VALUES (?, ?, ?, NOW())");
    $stmt->bind_param("iss", $id_conversa, $remetente_usuario, $mensagem_usuario);
    if (!$stmt->execute()) {
        throw new Exception('Erro ao salvar mensagem do usuário');
    }
    
    $stmt->close();
    
    // Buscar histórico da conversa para contexto
    $historico = obterHistoricoConversa($id_conversa, $sql);
    
    // Obter resposta da IA
    $resposta_ia = obterRespostaIA($mensagem_usuario, $historico);
    
    // Salvar resposta da IA
    $remetente_ia = 'ia';
    $stmt = $sql->prepare("INSERT INTO mensagens (id_conversa, remetente, conteudo, data_envio) VALUES (?, ?, ?, NOW())");
    $stmt->bind_param("iss", $id_conversa, $remetente_ia, $resposta_ia);
    if (!$stmt->execute()) {
        throw new Exception('Erro ao salvar resposta da IA');
    }
    
    $stmt->close();
    
    // Atualizar data_ultima_mensagem da conversa
    $stmt = $sql->prepare("UPDATE conversas SET data_ultima_mensagem = NOW() WHERE id_conversa = ?");
    $stmt->bind_param("i", $id_conversa);
    $stmt->execute();
    $stmt->close();
    
    // Confirmar transação
    $sql->commit();
    
    // Retornar sucesso
    echo json_encode([
        'sucesso' => true,
        'id_conversa' => $id_conversa,
        'resposta' => $resposta_ia,
        'timestamp' => date('Y-m-d H:i:s')
    ]);
    
} catch (Exception $e) {
    // Reverter transação em caso de erro
    $sql->rollback();
    echo json_encode([
        'sucesso' => false,
        'erro' => $e->getMessage()
    ]);
}

$sql->close();

/**
 * Obter histórico da conversa para contexto
 */
function obterHistoricoConversa($id_conversa, $conexao) {
    $historico = [];
    
    $stmt = $conexao->prepare("
        SELECT remetente, conteudo 
        FROM mensagens 
        WHERE id_conversa = ? 
        ORDER BY data_envio ASC 
        LIMIT 10
    ");
    $stmt->bind_param("i", $id_conversa);
    $stmt->execute();
    $result = $stmt->get_result();
    
    while ($row = $result->fetch_assoc()) {
        $historico[] = [
            'role' => $row['remetente'] === 'usuario' ? 'user' : 'assistant',
            'content' => $row['conteudo']
        ];
    }
    
    $stmt->close();
    return $historico;
}

/**
 * Obter resposta da IA usando Groq API (GRATUITA)
 * Para obter sua chave: https://console.groq.com/
 */
function obterRespostaIA($mensagem, $historico = []) {
    // COLOQUE SUA NOVA CHAVE AQUI
    $api_key = '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';
    
    // System prompt REDUZIDO para evitar erro 400
    $system_prompt = "Você é Luna, uma assistente virtual especializada em Transtorno do Espectro Autista (TEA). Forneça informações baseadas em evidências científicas, seja empática, acolhedora e não-julgadora. Use linguagem clara e acessível em português do Brasil. Sugira estratégias práticas e personalizadas. Oriente sobre rotinas visuais, comunicação alternativa e regulação sensorial. Você NÃO substitui profissionais de saúde - sempre recomende acompanhamento profissional quando apropriado. Respeite a neurodiversidade e valorize as conquistas de cada indivíduo.";
    
    // Montar mensagens com histórico
    $messages = [
        ['role' => 'system', 'content' => $system_prompt]
    ];
    
    // Adicionar histórico (últimas 10 mensagens)
    foreach ($historico as $msg) {
        // Validar que content é string e não está vazio
        if (isset($msg['content']) && is_string($msg['content']) && !empty(trim($msg['content']))) {
            $messages[] = [
                'role' => $msg['role'],
                'content' => mb_convert_encoding(trim($msg['content']), 'UTF-8', 'UTF-8')
            ];
        }
    }
    
    // Adicionar mensagem atual (validada)
    $mensagem_limpa = mb_convert_encoding(trim($mensagem), 'UTF-8', 'UTF-8');
    $messages[] = ['role' => 'user', 'content' => $mensagem_limpa];
    
  // Preparar dados para API - MODELO ATUALIZADO
$data = [
    'model' => 'llama-3.3-70b-versatile',  
    'messages' => $messages,
    'temperature' => 0.7,
    'max_tokens' => 1024,
    'top_p' => 0.9
];

    
   // Fazer requisição para Groq API
$ch = curl_init('https://api.groq.com/openai/v1/chat/completions');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Authorization: Bearer ' . $api_key,
    'Content-Type: application/json'
]);
curl_setopt($ch, CURLOPT_TIMEOUT, 60); // Aumentar para 60 segundos
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10); // Timeout de conexão
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false); // Desativar verificação SSL (temporariamente)

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
$curl_errno = curl_errno($ch);
curl_close($ch);
    
    // Processar resposta
    if ($http_code === 200 && $response) {
        $result = json_decode($response, true);
        if (isset($result['choices'][0]['message']['content'])) {
            return trim($result['choices'][0]['message']['content']);
        }
    }
    
    // LOGGING DETALHADO PARA DEBUG
    $error_response = json_decode($response, true);
    $error_details = [
        'http_code' => $http_code,
        'curl_error' => $curl_error,
        'error_message' => isset($error_response['error']['message']) ? $error_response['error']['message'] : 'Desconhecido',
        'error_type' => isset($error_response['error']['type']) ? $error_response['error']['type'] : 'Desconhecido',
        'api_key_length' => strlen($api_key),
        'messages_count' => count($messages),
        'last_message_length' => strlen($mensagem_limpa),
        'full_response' => substr($response, 0, 500) // Primeiros 500 caracteres da resposta
    ];
    error_log("=== ERRO GROQ API DETALHADO ===\n" . json_encode($error_details, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
    
    // Mensagens de erro específicas para o usuário
    if ($http_code === 401) {
        return "❌ **Chave de API inválida**\n\nA chave configurada não está funcionando.\n\n🔄 **Solução:**\n1. Acesse: https://console.groq.com/keys\n2. Delete a chave antiga\n3. Crie uma NOVA chave\n4. Copie TODA a chave (começa com 'gsk_')\n5. Cole no salvar_mensagem.php na linha \$api_key\n\n💡 Verifique se copiou a chave completa!";
    } else if ($http_code === 429) {
        return "⚠️ **Limite de requisições atingido**\n\nVocê excedeu o limite gratuito temporariamente.\n\n⏰ **Aguarde 1 minuto e tente novamente**\n\nO Groq tem limite de requisições por minuto no plano gratuito.";
    } else if ($http_code === 400) {
        $error_msg = isset($error_response['error']['message']) ? $error_response['error']['message'] : 'Formato de requisição inválido';
        return "⚠️ **Erro técnico temporário (400)**\n\n📋 Detalhes: {$error_msg}\n\n🔄 **Tente:**\n1. Aguardar alguns segundos\n2. Enviar uma mensagem mais curta\n3. Verificar o log do PHP para detalhes completos\n\n💡 Verifique o arquivo de log do servidor para o erro completo.";
    } else {
        return "⚠️ **Erro técnico temporário**\n\nCódigo: {$http_code}\n\n🔄 **Tente:**\n1. Aguardar alguns segundos\n2. Enviar a mensagem novamente\n3. Verificar sua conexão com internet\n\n📝 Verifique os logs do PHP para mais detalhes.";
    }
}
?>
