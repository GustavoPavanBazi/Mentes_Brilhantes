<?php
include "conexao.php";

// Criação das variaveis dos responsaveis
$nome = $_POST["nome"];
$email = $_POST["email"];
$cpf = $_POST["cpf"];
$celular = $_POST["celular"];
$cep = $_POST["cep"];
$rua = $_POST["rua"];
$bairro = $_POST["bairro"];
$cidade = $_POST["cidade"];
$numero = $_POST["numero"];
$complemento = $_POST["complemento"];
$senha = $_POST["senha"];
$perfil = $_FILES["perfil"]["name"];
$pasta = "img_cad/responsavel";
$voltar = "../";

$ext = strtolower(pathinfo($perfil, PATHINFO_EXTENSION));
$perfilf = $cpf . '.' . $ext;
$perfilbd = $pasta . $perfilf;

$select = "SELECT * FROM cad_responsavel";
$result = mysqli_query($sql, $select);
$select = "SELECT * FROM cad_responsavel";
$result = mysqli_query($sql, $select);
$cpf_existe = false;
$email_existe = false;

if (mysqli_num_rows($result) > 0) {
    while ($responsavel = mysqli_fetch_assoc($result)) {
        if ($responsavel['cpf'] == $cpf) {
            $cpf_existe = true;
            break;
        }
        if ($responsavel['email'] == $email) {
            $email_existe = true;
            break;
        }
    }
}

if ($cpf_existe) {
    die("CPF já cadastrado!");
}

if ($email_existe) {
    die("Email já cadastrado!");
}

// Se não existir, insere
$inserir = $sql->query("INSERT INTO cad_responsavel(nome, email, cpf, celular, cep, rua, bairro, cidade, numero, complemento, senha, perfil) VALUES ('$nome', '$email', '$cpf', '$celular', '$cep', '$rua', '$bairro', '$cidade', '$numero', '$complemento', '$senha', '$perfilbd')");

if ($inserir) {
    echo "Dados salvos com sucesso!!!";

    if (move_uploaded_file($_FILES['perfil']['tmp_name'], $voltar . $pasta . $perfilf)) {
        // upload ok
    } else {
        echo "Não foi possível concluir o upload da imagem.";
    }

    /* header("Location: ../login_responsavel.php"); */
    exit();
} else {
    echo "Erro ao salvar dados: " . $sql->error;
}

/* ver com o quaiati se é valido dividir a pasta imgp/ em sub pastas para ficar mais organizado */
