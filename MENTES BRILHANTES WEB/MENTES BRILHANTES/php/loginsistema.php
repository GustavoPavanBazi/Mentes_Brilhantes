<?php
include "conexao.php";

// Inicia a sessão no início
session_start();

// Função para limpar CPF (remove pontos, traços e espaços)
function limparCPF($cpf) {
    return preg_replace('/[^0-9]/', '', trim($cpf));
}

// Função para verificar senha (suporta hash bcrypt e texto plano)
function verificarSenha($senhaDigitada, $senhaArmazenada) {
    // Primeiro tenta verificar como hash bcrypt
    if (password_verify($senhaDigitada, $senhaArmazenada)) {
        return true;
    }
    
    // Se não funcionar, compara diretamente (para senhas antigas em texto plano)
    return $senhaDigitada === $senhaArmazenada;
}

// Recebe os dados do formulário
$identificador = trim($_POST["email"]); // Pode ser email ou CPF
$senha = $_POST["senha"];

// Normaliza o email (lowercase e trim)
$identificador_email = strtolower(trim($identificador));

// Limpa o CPF caso o identificador seja um CPF
$identificador_limpo = limparCPF($identificador);

// Log do início da tentativa de login
error_log("=== NOVA TENTATIVA DE LOGIN ===");
error_log("Identificador recebido: {$identificador}");
error_log("Identificador email (normalizado): {$identificador_email}");
error_log("Identificador limpo (CPF): {$identificador_limpo}");

// Variáveis para controlar o resultado do login
$usuario_encontrado = false;
$dados_usuario = null;
$tipo_usuario = "";

// Define as tabelas e configurações para cada tipo de usuário
$tipos_usuarios = [
    'responsavel' => [
        'tabela' => 'cad_responsavel',
        'id_campo' => 'id_responsa',
        'redirect' => '../html/adm_responsavel.html'
    ],
    'neurodivergente' => [
        'tabela' => 'cad_neurodivergentes',
        'id_campo' => 'id_neuro',
        'redirect' => '../html/adm_neurodivergente.html'
    ],
    'especialista' => [
        'tabela' => 'cad_especialista',
        'id_campo' => 'id_especial',
        'redirect' => '../html/adm_especialista.html'
    ]
];

// Tenta fazer login em cada tabela
foreach ($tipos_usuarios as $tipo => $config) {
    error_log("Verificando tabela: {$config['tabela']}");
    
    // CORREÇÃO: Busca por email (case-insensitive) OU CPF (com e sem formatação)
    // Usa LOWER() para comparação case-insensitive do email
    $stmt = $sql->prepare("SELECT * FROM {$config['tabela']} WHERE LOWER(TRIM(email)) = ? OR cpf = ? OR cpf = ? LIMIT 1");
    $stmt->bind_param("sss", $identificador_email, $identificador, $identificador_limpo);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $usuario = $result->fetch_assoc();
        
        error_log("Usuário encontrado na tabela {$config['tabela']}: {$usuario['nome']}");
        error_log("Email do usuário no banco: {$usuario['email']}");
        error_log("CPF do usuário: {$usuario['cpf']}");
        
        // Debug: mostrar informações sobre a senha
        $senhaArmazenada = $usuario['senha'];
        $isBcrypt = strlen($senhaArmazenada) == 60 && substr($senhaArmazenada, 0, 4) === '$2y$';
        error_log("Tipo de senha armazenada: " . ($isBcrypt ? 'Hash bcrypt' : 'Texto plano ou outro formato'));
        error_log("Tamanho da senha armazenada: " . strlen($senhaArmazenada));
        
        // Verifica a senha (suporta hash e texto plano)
        if (verificarSenha($senha, $senhaArmazenada)) {
            error_log("✓ Senha verificada com sucesso!");
            $usuario_encontrado = true;
            $dados_usuario = $usuario;
            $tipo_usuario = $tipo;
            break; // Sai do loop quando encontrar o usuário
        } else {
            error_log("✗ Senha incorreta para o usuário {$usuario['nome']}");
            error_log("Senha digitada (length): " . strlen($senha));
        }
    } else {
        error_log("Nenhum usuário encontrado na tabela {$config['tabela']} com o identificador fornecido");
    }
    
    $stmt->close();
}

// Processa o resultado do login
if ($usuario_encontrado) {
    // Salva os dados do usuário na sessão
    $_SESSION['usuario_logado'] = true;
    $_SESSION['tipo_usuario'] = $tipo_usuario;
    $_SESSION['dados_usuario'] = $dados_usuario;
    $_SESSION['email'] = $dados_usuario['email'];
    $_SESSION['nome'] = $dados_usuario['nome'];
    $_SESSION['cpf'] = $dados_usuario['cpf'];
    $_SESSION['id_usuario'] = $dados_usuario[$tipos_usuarios[$tipo_usuario]['id_campo']];
    
    // Log de sucesso
    error_log("✓ LOGIN BEM-SUCEDIDO: {$dados_usuario['nome']} ({$tipo_usuario})");
    error_log("Redirecionando para: {$tipos_usuarios[$tipo_usuario]['redirect']}");
    error_log("=== FIM DA TENTATIVA DE LOGIN ===\n");
    
    // Redireciona baseado no tipo de usuário
    header("Location: " . $tipos_usuarios[$tipo_usuario]['redirect']);
    exit();
    
} else {
    // Login falhou
    error_log("✗ LOGIN FALHOU para: {$identificador}");
    error_log("=== FIM DA TENTATIVA DE LOGIN ===\n");
    
    // Redireciona de volta para a página de login com mensagem de erro
    $_SESSION['erro_login'] = "Email/CPF ou senha incorretos!";
    header("Location: ../html/login.html");
    exit();
}

$sql->close();
?>
