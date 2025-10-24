<?php
session_start();

// Destrói todas as variáveis de sessão
$_SESSION = array();

// Destrói o cookie de sessão se existir
if (isset($_COOKIE[session_name()])) {
    setcookie(session_name(), '', time()-3600, '/');
}

// Destrói a sessão
session_destroy();

// Redireciona para a página inicial
header("Location: ../index.html");
exit();
?>