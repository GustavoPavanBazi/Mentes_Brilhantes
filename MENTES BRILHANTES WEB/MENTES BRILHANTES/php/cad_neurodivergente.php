<?php

include "conexao.php";

// Criação das variaveis dos neurodivergentes
$nome = $_POST["nome"];
$email = $_POST["email"];
$cpf = $_POST["cpf"];
$rg = $_POST["rg"]; 
$celular = $_POST["celular"];
$cep = $_POST["cep"];
$rua = $_POST["rua"];
$bairro = $_POST["bairro"];
$cidade = $_POST["cidade"];
$numero = $_POST["numero"];
$complemento = $_POST["complemento"];
$senha = $_POST["senha"];
$perfil = $_FILES["perfil"]["name"];
$pasta = "imgp/";
$voltar = "../";

$ext = strtolower(pathinfo($perfil, PATHINFO_EXTENSION));
$perfilf = $cpf . '.' . $ext;
$perfilbd = $pasta . $perfilf;

$select = "SELECT * FROM cad_neurodivergentes";
$result = mysqli_query($sql, $select);
$comparar = mysqli_fetch_assoc($result);

/* Terminar o neurodivergente e além disso ver como fazer para o responsável gerar um código que precisará ser preenchido nesse cadastro */  

?>