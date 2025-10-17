<?php
include "conexao.php";

// Recebe os dados do formulário
$email = $_POST["email"];
$senha = $_POST["senha"];

// Variáveis para controlar o resultado do login
$usuario_encontrado = false;
$dados_usuario = null;
$tipo_usuario = "";

// Verifica na tabela cad_responsavel
$select_neurodivergentes = "SELECT * FROM cad_neurodivergentes WHERE email = '$email' AND senha = '$senha'";
$result_neurodivergentes = $sql->query($select_neurodivergentes);

if ($result_neurodivergentes->num_rows > 0) {
    $usuario_encontrado = true;
    $dados_usuario = $result_neurodivergentes->fetch_assoc();
    $tipo_usuario = "neurodivergentes";
}

// Processa o resultado do login
if ($usuario_encontrado) {
    // Inicia a sessão para manter o usuário logado
    session_start();
    
    // Salva os dados do usuário na sessão
    $_SESSION['usuario_logado'] = true;
    $_SESSION['tipo_usuario'] = $tipo_usuario;
    $_SESSION['dados_usuario'] = $dados_usuario;
    $_SESSION['email'] = $email;
    $_SESSION['nome'] = $dados_usuario['nome'];
    
    // Define o ID do usuário baseado no tipo
    switch ($tipo_usuario) {
        case 'neurodivergentes':
            $_SESSION['id_usuario'] = $dados_usuario['id_neuro'];
            break;
    }

} else {
    echo "Email ou senha incorretos!";
    // Opcional: redirecionar de volta para a página de login
     header("Location: ../login_unico.html");
     exit();
}
?>
