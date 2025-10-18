<?php
session_start();

header('Content-Type: application/json');

// Verifica se o usuário está logado
if (isset($_SESSION['usuario_logado']) && $_SESSION['usuario_logado'] === true) {
    echo json_encode([
        'autenticado' => true,
        'tipo' => $_SESSION['tipo_usuario'],
        'nome' => $_SESSION['nome'],
        'email' => $_SESSION['email']
    ]);
} else {
    echo json_encode([
        'autenticado' => false
    ]);
}
?>