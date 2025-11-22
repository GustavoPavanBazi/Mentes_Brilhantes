<?php
session_start();
require_once('conexao.php');

// Função para verificar senha
function verificarSenha($senhaDigitada, $senhaArmazenada) {
    // Verifica se é hash bcrypt válido (60 caracteres, começa com $2y$)
    if (strlen($senhaArmazenada) == 60 && substr($senhaArmazenada, 0, 4) === '$2y$') {
        return password_verify($senhaDigitada, $senhaArmazenada);
    }
    // Fallback: compara senha em texto plano (NÃO RECOMENDADO)
    return $senhaDigitada === $senhaArmazenada;
}

// Validação de método
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    $_SESSION['erro_login'] = "Método não permitido!";
    header("Location: ../html/login.php");
    exit();
}

// Captura dados (aceita 'email' ou 'identificador')
$identificador = '';
if (isset($_POST['email']) && !empty(trim($_POST['email']))) {
    $identificador = trim($_POST['email']);
} elseif (isset($_POST['identificador']) && !empty(trim($_POST['identificador']))) {
    $identificador = trim($_POST['identificador']);
}

$senha = isset($_POST['senha']) ? $_POST['senha'] : '';

// Valida campos vazios
if (empty($identificador) || empty($senha)) {
    $_SESSION['erro_login'] = "Todos os campos são obrigatórios!";
    header("Location: ../html/login.php");
    exit();
}

// Prepara variáveis
$identificador_limpo = preg_replace('/\D/', '', $identificador);
$identificador_email = strtolower(trim($identificador));

error_log("=== TENTATIVA DE LOGIN ===");
error_log("Email: {$identificador_email}");

// ========================================
// VERIFICAÇÃO PRIORITÁRIA: ADMINISTRADOR
// ========================================
if ($identificador_email === 'adm_mentes.brilhantes@gmail.com') {
    error_log("Login de ADMINISTRADOR detectado");
    
    $stmt = $sql->prepare("SELECT * FROM cad_administrador WHERE LOWER(TRIM(email)) = ? LIMIT 1");
    
    if (!$stmt) {
        error_log("ERRO SQL: " . $sql->error);
        $_SESSION['erro_login'] = "Erro no sistema. Tente novamente.";
        header("Location: ../html/login.php");
        exit();
    }
    
    $stmt->bind_param("s", $identificador_email);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $admin = $result->fetch_assoc();
        error_log("Administrador encontrado - Hash length: " . strlen($admin['senha']));
        
        // Verifica a senha
        if (verificarSenha($senha, $admin['senha'])) {
            error_log("✓ Login de ADMINISTRADOR bem-sucedido");
            
            // Cria sessão do administrador
            $_SESSION['usuario_logado'] = true;
            $_SESSION['tipo_usuario'] = 'administrador';
            $_SESSION['admin_id'] = $admin['id_admin'];
            $_SESSION['email'] = $admin['email'];
            $_SESSION['nome'] = 'Administrador';
            
            $stmt->close();
            $sql->close();
            
            header("Location: ../html/adm.html");
            exit();
        } else {
            error_log("✗ Senha incorreta para administrador");
        }
    } else {
        error_log("✗ Email de administrador não encontrado");
    }
    
    $stmt->close();
    
    // Login de admin falhou
    $_SESSION['erro_login'] = "Email ou senha incorretos!";
    $sql->close();
    header("Location: ../html/login.php");
    exit();
}

// ========================================
// VERIFICAÇÃO: OUTROS USUÁRIOS
// ========================================
$usuario_encontrado = false;
$dados_usuario = null;
$tipo_usuario = '';

$tipos_usuarios = [
    'responsavel' => [
        'tabela' => 'cad_responsavel',
        'id_campo' => 'id_responsa',
        'redirect' => '../html/adm_responsavel.php'
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

// Loop pelos tipos de usuários
foreach ($tipos_usuarios as $tipo => $config) {
    error_log("Verificando tabela: {$config['tabela']}");
    
    $stmt = $sql->prepare("SELECT * FROM {$config['tabela']} WHERE LOWER(TRIM(email)) = ? OR cpf = ? OR cpf = ? LIMIT 1");
    $stmt->bind_param("sss", $identificador_email, $identificador, $identificador_limpo);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $usuario = $result->fetch_assoc();
        error_log("Usuário encontrado: {$usuario['nome']}");
        
        if (verificarSenha($senha, $usuario['senha'])) {
    error_log("✓ Senha correta!");
    
    // BLOQUEIA LOGIN DE NEURODIVERGENTE NA WEB
    if ($tipo === 'neurodivergente') {
        error_log("✗ Tentativa de login de neurodivergente na web bloqueada");
        $_SESSION['erro_login'] = "Acesso restrito! Por favor, realize o login através do nosso aplicativo móvel.";
        $stmt->close();
        $sql->close();
        header("Location: ../html/login.php");
        exit();
    }
    
    $usuario_encontrado = true;
    $dados_usuario = $usuario;
    $tipo_usuario = $tipo;
    $stmt->close();
    break;
}
 else {
            error_log("✗ Senha incorreta");
        }
    }
    
    $stmt->close();
}

// Processa resultado - CORRIGIDO: usando as variáveis corretas com underline
if ($usuario_encontrado) {
    $_SESSION['usuario_logado'] = true;
    $_SESSION['tipo_usuario'] = $tipo_usuario;
    $_SESSION['dados_usuario'] = $dados_usuario;
    $_SESSION['email'] = $dados_usuario['email'];
    $_SESSION['nome'] = $dados_usuario['nome'];
    $_SESSION['cpf'] = $dados_usuario['cpf'];
    $_SESSION['id_usuario'] = $dados_usuario[$tipos_usuarios[$tipo_usuario]['id_campo']];

    // CRÍTICO: Criar variável de sessão específica para cada tipo
    if ($tipo_usuario === 'responsavel') {
        $_SESSION['id_responsavel'] = $dados_usuario[$tipos_usuarios[$tipo_usuario]['id_campo']];
        error_log("✓ Sessão criada - id_responsavel: " . $_SESSION['id_responsavel']);
    } elseif ($tipo_usuario === 'especialista') {
        $_SESSION['id_especialista'] = $dados_usuario[$tipos_usuarios[$tipo_usuario]['id_campo']];
        error_log("✓ Sessão criada - id_especialista: " . $_SESSION['id_especialista']);
    } elseif ($tipo_usuario === 'neurodivergente') {
        $_SESSION['id_neurodivergente'] = $dados_usuario[$tipos_usuarios[$tipo_usuario]['id_campo']];
        error_log("✓ Sessão criada - id_neurodivergente: " . $_SESSION['id_neurodivergente']);
    }

    $sql->close();
    
    // Redirecionamento
    header("Location: " . $tipos_usuarios[$tipo_usuario]['redirect']);
    exit();
} else {
    // Login falhou
    $_SESSION['erro_login'] = "Email ou senha incorretos!";
    $sql->close();
    header("Location: ../html/login.php");
    exit();
}
?>
