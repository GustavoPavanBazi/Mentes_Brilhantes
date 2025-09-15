<?php
include "conexao.php";

// Criação das variaveis dos especialistas
$nome = $_POST["nome"];
$email = $_POST["email"];
$cpf = $_POST["cpf"];
$formacao = $_POST["formacao"];
$certificado = $_FILES["certificado"]["name"];
$descricao = $_POST["descricao"];
$perfil = $_FILES["perfil"]["name"];
$pasta = "imgp/";
$voltar = "../";

$ext = strtolower(pathinfo($perfil, PATHINFO_EXTENSION));
$perfilf = $cpf . '.' . $ext;
$perfilbd = $pasta . $perfilf;

$select = "SELECT * FROM cad_especialista";
$result = mysqli_query($sql, $select);
$comparar = mysqli_fetch_assoc($result);

if (mysqli_num_rows($result) > 0) {
    while ($especialista = mysqli_fetch_assoc($result)) {
        if ($especialista['cpf'] == $cpf) {
            die("CPF já cadastrado!");
        }
        if ($especialista['email'] == $email) {
            die("Email já cadastrado!");
        }
    }
    if (move_uploaded_file($_FILES['perfil']['tmp_name'], $voltar . $pasta . $perfilf)) {
    } else {
        $result_massage = "Nâo foi possível concluir o upload da imagem.";
    }

    // Query com os nomes das variaveis
    $sql->query("INSERT INTO cad_especialista(nome, email, cpf, formacao, certificado, descricao, perfil)VALUES('$nome', '$email', '$cpf', '$formacao', '$certificado', '$descricao', '$perfilbd' )");

    echo "Dados salvos com sucesso!!!";
   }

   /*Fazer a verificação do certificado assim como foi feito com o perfil, mas é preciso limitar para somente arquivos em pdp.*/ 

?>

