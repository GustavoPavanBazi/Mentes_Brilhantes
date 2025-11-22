<?php
session_start();

// Destruir todas as variáveis de sessão
$_SESSION = array();

// Destruir o cookie de sessão se existir
if (isset($_COOKIE[session_name()])) {
    setcookie(session_name(), '', time() - 3600, '/');
}

// Destruir a sessão
session_destroy();

// Redirecionar para a página de login do responsável
header("Location: ../html/login.php");
exit();
?>
