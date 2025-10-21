<?php
session_start();

// Verificar se está logado
if (!isset($_SESSION['id_responsavel'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Usuário não autenticado'
    ]);
    exit;
}

// Incluir conexão com o banco de dados
include "conexao.php";

// Verificar conexão
if ($sql->connect_error) {
    echo json_encode([
        'success' => false,
        'message' => 'Erro de conexão com o banco de dados'
    ]);
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'];

try {
    // Buscar TODOS os dados do responsável
    $stmt = $sql->prepare("
        SELECT 
            id_responsa,
            nome,
            email,
            celular,
            cpf,
            data_nascimento,
            rua,
            numero,
            complemento,
            bairro,
            cidade,
            cep,
            perfil
        FROM cad_responsavel 
        WHERE id_responsa = ?
    ");
    
    if (!$stmt) {
        throw new Exception('Erro ao preparar consulta: ' . $sql->error);
    }
    
    $stmt->bind_param("i", $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        throw new Exception('Responsável não encontrado');
    }
    
    $dados = $result->fetch_assoc();
    
    // Formatar dados para exibição
    $dados_formatados = [
        'id' => $dados['id_responsa'],
        'nome' => $dados['nome'],
        'email' => $dados['email'],
        'celular' => $dados['celular'],
        'cpf' => $dados['cpf'],
        'data_nascimento' => $dados['data_nascimento'],
        'rua' => $dados['rua'] ?? '',
        'numero' => $dados['numero'] ?? '',
        'complemento' => $dados['complemento'] ?? '',
        'bairro' => $dados['bairro'] ?? '',
        'cidade' => $dados['cidade'] ?? '',
        'cep' => $dados['cep'] ?? '',
        'perfil' => $dados['perfil'] ?? ''
    ];
    
    // Formatar celular: (11) 99999-9999
    if (!empty($dados['celular'])) {
        $celular = preg_replace('/\D/', '', $dados['celular']);
        if (strlen($celular) === 11) {
            $dados_formatados['celular_formatado'] = '(' . substr($celular, 0, 2) . ') ' . 
                                                      substr($celular, 2, 5) . '-' . 
                                                      substr($celular, 7, 4);
        } else {
            $dados_formatados['celular_formatado'] = $dados['celular'];
        }
    } else {
        $dados_formatados['celular_formatado'] = '';
    }
    
    // Formatar CPF: 123.456.789-10
    if (!empty($dados['cpf'])) {
        $cpf = preg_replace('/\D/', '', $dados['cpf']);
        if (strlen($cpf) === 11) {
            $dados_formatados['cpf_formatado'] = substr($cpf, 0, 3) . '.' . 
                                                  substr($cpf, 3, 3) . '.' . 
                                                  substr($cpf, 6, 3) . '-' . 
                                                  substr($cpf, 9, 2);
        } else {
            $dados_formatados['cpf_formatado'] = $dados['cpf'];
        }
    } else {
        $dados_formatados['cpf_formatado'] = '';
    }
    
    // Formatar CEP: 12345-678
    if (!empty($dados['cep'])) {
        $cep = preg_replace('/\D/', '', $dados['cep']);
        if (strlen($cep) === 8) {
            $dados_formatados['cep_formatado'] = substr($cep, 0, 5) . '-' . substr($cep, 5, 3);
        } else {
            $dados_formatados['cep_formatado'] = $dados['cep'];
        }
    } else {
        $dados_formatados['cep_formatado'] = '';
    }
    
    // Formatar data de nascimento: DD/MM/AAAA para exibição
    if (!empty($dados['data_nascimento'])) {
        $data = DateTime::createFromFormat('Y-m-d', $dados['data_nascimento']);
        if ($data) {
            $dados_formatados['data_nascimento_formatada'] = $data->format('d/m/Y');
        } else {
            $dados_formatados['data_nascimento_formatada'] = $dados['data_nascimento'];
        }
    } else {
        $dados_formatados['data_nascimento_formatada'] = '';
    }
    
    // Obter iniciais do nome
    $nomes = explode(' ', trim($dados['nome']));
    if (count($nomes) >= 2) {
        $dados_formatados['iniciais'] = strtoupper(
            substr($nomes[0], 0, 1) . substr($nomes[count($nomes) - 1], 0, 1)
        );
    } else {
        $dados_formatados['iniciais'] = strtoupper(substr($dados['nome'], 0, 1));
    }
    
    echo json_encode([
        'success' => true,
        'dados' => $dados_formatados
    ]);
    
    $stmt->close();
    
} catch (Exception $e) {
    error_log("Erro ao buscar dados do responsável - ID: " . $id_responsavel . " - " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}

$sql->close();
?>
