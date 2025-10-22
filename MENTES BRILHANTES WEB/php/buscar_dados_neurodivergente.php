<?php
session_start();
header('Content-Type: application/json; charset=utf-8');

// Verificar se o responsável está logado
if (!isset($_SESSION['id_responsavel']) && !isset($_SESSION['id_responsa'])) {
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

// Pegar o ID do responsável da sessão
$id_responsavel = $_SESSION['id_responsavel'] ?? $_SESSION['id_responsa'] ?? null;

if (!$id_responsavel) {
    echo json_encode([
        'success' => false,
        'message' => 'ID do responsável não encontrado'
    ]);
    exit;
}

try {
    // Buscar TODOS os neurodivergentes vinculados ao responsável
    $stmt = $sql->prepare("
        SELECT 
            n.id_neuro,
            n.nome,
            n.email,
            n.data_nascimento,
            n.rg,
            n.cpf,
            n.sexo,
            n.celular,
            n.cep,
            n.rua,
            n.bairro,
            n.cidade,
            n.numero,
            n.complemento,
            n.perfil,
            n.data_cadastro,
            r.nome as nome_responsavel,
            r.celular as celular_responsavel
        FROM cad_neurodivergentes n
        INNER JOIN cad_responsavel r ON n.id_responsa = r.id_responsa
        WHERE n.id_responsa = ?
        ORDER BY n.data_cadastro DESC
    ");
    
    if (!$stmt) {
        throw new Exception('Erro ao preparar consulta: ' . $sql->error);
    }
    
    $stmt->bind_param("i", $id_responsavel);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $neurodivergentes = [];
    
    while ($dados = $result->fetch_assoc()) {
        // Formatar dados para exibição
        $dados_formatados = [
            'id' => $dados['id_neuro'],
            'nome' => $dados['nome'],
            'email' => $dados['email'],
            'data_nascimento' => $dados['data_nascimento'],
            'rg' => $dados['rg'],
            'cpf' => $dados['cpf'],
            'sexo' => $dados['sexo'],
            'celular' => $dados['celular'] ?? '',
            'cep' => $dados['cep'],
            'rua' => $dados['rua'],
            'bairro' => $dados['bairro'],
            'cidade' => $dados['cidade'],
            'numero' => $dados['numero'],
            'complemento' => $dados['complemento'] ?? '',
            'perfil' => $dados['perfil'] ?? '',
            'data_cadastro' => $dados['data_cadastro'],
            'nome_responsavel' => $dados['nome_responsavel'],
            'celular_responsavel' => $dados['celular_responsavel']
        ];
        
        // Formatar RG: XX.XXX.XXX-X
        if (!empty($dados['rg'])) {
            $rg = preg_replace('/\D/', '', $dados['rg']);
            if (strlen($rg) >= 9) {
                $dados_formatados['rg_formatado'] = substr($rg, 0, 2) . '.' .
                                                     substr($rg, 2, 3) . '.' .
                                                     substr($rg, 5, 3) . '-' .
                                                     substr($rg, 8);
            } else {
                $dados_formatados['rg_formatado'] = $dados['rg'];
            }
        } else {
            $dados_formatados['rg_formatado'] = '';
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
            $dados_formatados['celular_formatado'] = 'Não informado';
        }
        
        // Formatar celular do responsável
        if (!empty($dados['celular_responsavel'])) {
            $cel_resp = preg_replace('/\D/', '', $dados['celular_responsavel']);
            if (strlen($cel_resp) === 11) {
                $dados_formatados['celular_responsavel_formatado'] = '(' . substr($cel_resp, 0, 2) . ') ' .
                                                                      substr($cel_resp, 2, 5) . '-' .
                                                                      substr($cel_resp, 7, 4);
            } else {
                $dados_formatados['celular_responsavel_formatado'] = $dados['celular_responsavel'];
            }
        } else {
            $dados_formatados['celular_responsavel_formatado'] = '';
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
        
        // Formatar data de nascimento: DD/MM/AAAA
        if (!empty($dados['data_nascimento'])) {
            $data = DateTime::createFromFormat('Y-m-d', $dados['data_nascimento']);
            if ($data) {
                $dados_formatados['data_nascimento_formatada'] = $data->format('d/m/Y');
                
                // Calcular idade
                $hoje = new DateTime();
                $idade = $hoje->diff($data)->y;
                $dados_formatados['idade'] = $idade;
            } else {
                $dados_formatados['data_nascimento_formatada'] = $dados['data_nascimento'];
                $dados_formatados['idade'] = 0;
            }
        } else {
            $dados_formatados['data_nascimento_formatada'] = '';
            $dados_formatados['idade'] = 0;
        }
        
        // Formatar endereço completo
        $endereco_completo = $dados['rua'] . ', ' . $dados['numero'];
        if (!empty($dados['complemento'])) {
            $endereco_completo .= ' - ' . $dados['complemento'];
        }
        $endereco_completo .= ' - ' . $dados['bairro'] . ', ' . $dados['cidade'];
        $dados_formatados['endereco_completo'] = $endereco_completo;
        
        // Obter iniciais do nome
        $nomes = explode(' ', trim($dados['nome']));
        if (count($nomes) >= 2) {
            $dados_formatados['iniciais'] = strtoupper(
                substr($nomes[0], 0, 1) . substr($nomes[count($nomes) - 1], 0, 1)
            );
        } else {
            $dados_formatados['iniciais'] = strtoupper(substr($dados['nome'], 0, 1));
        }
        
        $neurodivergentes[] = $dados_formatados;
    }
    
    echo json_encode([
        'success' => true,
        'dados' => $neurodivergentes,
        'total' => count($neurodivergentes)
    ], JSON_UNESCAPED_UNICODE);
    
    $stmt->close();
    
} catch (Exception $e) {
    error_log("Erro ao buscar neurodivergentes - ID Responsável: " . $id_responsavel . " - " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}

$sql->close();
?>
