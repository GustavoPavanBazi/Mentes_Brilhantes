<?php
session_start();
include "conexao.php";

// Verificar se o formulário foi enviado
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    
    // Receber e sanitizar dados do formulário
    $nome = mysqli_real_escape_string($sql, trim($_POST["nome"]));
    $email = mysqli_real_escape_string($sql, trim($_POST["email"]));
    $cpf = mysqli_real_escape_string($sql, preg_replace('/[^0-9]/', '', $_POST["cpf"]));
    $data_nascimento = mysqli_real_escape_string($sql, $_POST["data_nascimento"]);
    $sexo = mysqli_real_escape_string($sql, $_POST["sexo"]);
    $formacao = mysqli_real_escape_string($sql, trim($_POST["formacao"]));
    $descricao = mysqli_real_escape_string($sql, trim($_POST["descricao"]));
    $senha = password_hash($_POST["senha"], PASSWORD_DEFAULT);
    
    // Definir pastas
    $pastaImagens = "imgp/";
    $pastaCertificados = "certificados/";
    $voltar = "../";
    
    // Variáveis para os arquivos
    $perfilbd = "";
    $certificadobd = "";
    
    // Validações básicas
    $erros = array();
    
    if (empty($nome) || strlen($nome) < 2) {
        $erros[] = "Nome inválido";
    }
    
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $erros[] = "E-mail inválido";
    }
    
    if (strlen($cpf) != 11) {
        $erros[] = "CPF deve ter 11 dígitos";
    }
    
    // Validar data de nascimento
    if (empty($data_nascimento)) {
        $erros[] = "Data de nascimento é obrigatória";
    } else {
        $hoje = new DateTime();
        $nascimento = new DateTime($data_nascimento);
        $idade = $hoje->diff($nascimento)->y;
        
        if ($idade < 18 || $idade > 100) {
            $erros[] = "Idade deve estar entre 18 e 100 anos";
        }
    }
    
    // Validar sexo
    $sexosPermitidos = array('Masculino', 'Feminino');
    if (empty($sexo) || !in_array($sexo, $sexosPermitidos)) {
        $erros[] = "Sexo deve ser selecionado";
    }
    
    if (empty($formacao)) {
        $erros[] = "Formação é obrigatória";
    }
    
    if (empty($descricao) || strlen($descricao) < 50) {
        $erros[] = "Descrição deve ter pelo menos 50 caracteres";
    }
    
    // Verificar se CPF ou email já existem - VERIFICAÇÃO INDIVIDUAL
    $cpfDuplicado = false;
    $emailDuplicado = false;
    
    $selectCPF = "SELECT cpf FROM cad_especialista WHERE cpf = '$cpf'";
    $resultCPF = mysqli_query($sql, $selectCPF);
    if (mysqli_num_rows($resultCPF) > 0) {
        $cpfDuplicado = true;
        $erros[] = "CPF já cadastrado!";
    }
    
    $selectEmail = "SELECT email FROM cad_especialista WHERE email = '$email'";
    $resultEmail = mysqli_query($sql, $selectEmail);
    if (mysqli_num_rows($resultEmail) > 0) {
        $emailDuplicado = true;
        $erros[] = "E-mail já cadastrado!";
    }
    
    // Processar upload da foto de perfil (OBRIGATÓRIO)
    if (!isset($_FILES['perfilfoto']) || $_FILES['perfilfoto']['error'] != 0) {
        $erros[] = "Foto de perfil é obrigatória";
    } else {
        $perfil = $_FILES["perfilfoto"]["name"];
        $ext = strtolower(pathinfo($perfil, PATHINFO_EXTENSION));
        
        $extensoesPermitidas = array('jpg', 'jpeg', 'png', 'gif');
        if (!in_array($ext, $extensoesPermitidas)) {
            $erros[] = "Formato de imagem inválido. Use JPG, JPEG, PNG ou GIF";
        } else {
            if ($_FILES['perfilfoto']['size'] > 5 * 1024 * 1024) {
                $erros[] = "Foto muito grande. Tamanho máximo: 5MB";
            } else {
                $perfilf = $cpf . '_perfil.' . $ext;
                $caminhoCompleto = $voltar . $pastaImagens . $perfilf;
                
                if (!file_exists($voltar . $pastaImagens)) {
                    mkdir($voltar . $pastaImagens, 0777, true);
                }
                
                if (move_uploaded_file($_FILES['perfilfoto']['tmp_name'], $caminhoCompleto)) {
                    $perfilbd = $pastaImagens . $perfilf;
                } else {
                    $erros[] = "Não foi possível fazer upload da foto de perfil";
                }
            }
        }
    }
    
    // Processar upload do certificado (OBRIGATÓRIO)
    if (!isset($_FILES['certificado']) || $_FILES['certificado']['error'] != 0) {
        $erros[] = "Certificado é obrigatório";
    } else {
        $certificado = $_FILES["certificado"]["name"];
        $ext = strtolower(pathinfo($certificado, PATHINFO_EXTENSION));
        
        $extensoesPermitidas = array('pdf', 'jpg', 'jpeg', 'png');
        if (!in_array($ext, $extensoesPermitidas)) {
            $erros[] = "Formato de certificado inválido. Use PDF, JPG, JPEG ou PNG";
        } else {
            if ($_FILES['certificado']['size'] > 5 * 1024 * 1024) {
                $erros[] = "Certificado muito grande. Tamanho máximo: 5MB";
            } else {
                $certificadof = $cpf . '_certificado.' . $ext;
                $caminhoCompleto = $voltar . $pastaCertificados . $certificadof;
                
                if (!file_exists($voltar . $pastaCertificados)) {
                    mkdir($voltar . $pastaCertificados, 0777, true);
                }
                
                if (move_uploaded_file($_FILES['certificado']['tmp_name'], $caminhoCompleto)) {
                    $certificadobd = $pastaCertificados . $certificadof;
                } else {
                    $erros[] = "Não foi possível fazer upload do certificado";
                }
            }
        }
    }
    
    // Se não houver erros, inserir no banco de dados
    if (empty($erros)) {
        $insert = "INSERT INTO cad_especialista (nome, email, cpf, data_nascimento, sexo, formacao, certificado, descricao, perfil, senha) 
                   VALUES ('$nome', '$email', '$cpf', '$data_nascimento', '$sexo', '$formacao', '$certificadobd', '$descricao', '$perfilbd', '$senha')";
        
        if (mysqli_query($sql, $insert)) {
            // RETORNAR JSON DE SUCESSO
            echo json_encode(array('success' => true, 'message' => 'Cadastro realizado com sucesso!'));
            exit();
        } else {
            // Erro ao inserir
            echo json_encode(array('success' => false, 'message' => 'Erro ao salvar no banco de dados'));
            exit();
        }
    }
    
    // Se houver erros, retornar JSON com os erros específicos
    if (!empty($erros)) {
        $response = array(
            'success' => false,
            'message' => implode(", ", $erros),
            'cpf_duplicado' => $cpfDuplicado,
            'email_duplicado' => $emailDuplicado
        );
        echo json_encode($response);
        exit();
    }
    
} else {
    // Se não for POST, retornar erro
    echo json_encode(array('success' => false, 'message' => 'Método inválido'));
    exit();
}

mysqli_close($sql);
?>
