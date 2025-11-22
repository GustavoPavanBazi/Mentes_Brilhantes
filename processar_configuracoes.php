<?php
session_start();
require_once 'conexao.php'; // Seu arquivo de conexão com o banco

// Verificar se o usuário está logado
if (!isset($_SESSION['id_responsa'])) {
    echo json_encode(['success' => false, 'message' => 'Usuário não autenticado']);
    exit;
}

$id_responsa = $_SESSION['id_responsa'];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? '';
    
    if ($acao === 'salvar_configuracoes') {
        // Receber dados do formulário
        $notif_email_atividades = isset($_POST['notif_email_atividades']) ? 1 : 0;
        $notif_email_lembretes = isset($_POST['notif_email_lembretes']) ? 1 : 0;
        $notif_email_novidades = isset($_POST['notif_email_novidades']) ? 1 : 0;
        $notif_push_mensagens = isset($_POST['notif_push_mensagens']) ? 1 : 0;
        $notif_push_alertas = isset($_POST['notif_push_alertas']) ? 1 : 0;
        
        $privacidade_perfil = $_POST['privacidade_perfil'] ?? 'privado';
        $mostrar_email = isset($_POST['mostrar_email']) ? 1 : 0;
        $mostrar_telefone = isset($_POST['mostrar_telefone']) ? 1 : 0;
        
        $tema = $_POST['tema'] ?? 'claro';
        $tamanho_fonte = $_POST['tamanho_fonte'] ?? 'medio';
        $modo_alto_contraste = isset($_POST['modo_alto_contraste']) ? 1 : 0;
        $animacoes_reduzidas = isset($_POST['animacoes_reduzidas']) ? 1 : 0;
        
        $idioma = $_POST['idioma'] ?? 'pt-BR';
        $fuso_horario = $_POST['fuso_horario'] ?? 'America/Sao_Paulo';
        
        $autenticacao_dois_fatores = isset($_POST['autenticacao_dois_fatores']) ? 1 : 0;
        
        try {
            // Verificar se já existe configuração para este usuário
            $stmt = $pdo->prepare("SELECT id_config FROM configuracoes_usuario WHERE id_responsa = ?");
            $stmt->execute([$id_responsa]);
            $config_existe = $stmt->fetch();
            
            if ($config_existe) {
                // Atualizar configurações existentes
                $sql = "UPDATE configuracoes_usuario SET 
                    notif_email_atividades = ?,
                    notif_email_lembretes = ?,
                    notif_email_novidades = ?,
                    notif_push_mensagens = ?,
                    notif_push_alertas = ?,
                    privacidade_perfil = ?,
                    mostrar_email = ?,
                    mostrar_telefone = ?,
                    tema = ?,
                    tamanho_fonte = ?,
                    modo_alto_contraste = ?,
                    animacoes_reduzidas = ?,
                    idioma = ?,
                    fuso_horario = ?,
                    autenticacao_dois_fatores = ?
                    WHERE id_responsa = ?";
                
                $stmt = $pdo->prepare($sql);
                $stmt->execute([
                    $notif_email_atividades, $notif_email_lembretes, $notif_email_novidades,
                    $notif_push_mensagens, $notif_push_alertas,
                    $privacidade_perfil, $mostrar_email, $mostrar_telefone,
                    $tema, $tamanho_fonte, $modo_alto_contraste, $animacoes_reduzidas,
                    $idioma, $fuso_horario, $autenticacao_dois_fatores,
                    $id_responsa
                ]);
            } else {
                // Inserir novas configurações
                $sql = "INSERT INTO configuracoes_usuario 
                    (id_responsa, notif_email_atividades, notif_email_lembretes, notif_email_novidades,
                     notif_push_mensagens, notif_push_alertas, privacidade_perfil, mostrar_email, 
                     mostrar_telefone, tema, tamanho_fonte, modo_alto_contraste, animacoes_reduzidas,
                     idioma, fuso_horario, autenticacao_dois_fatores) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                $stmt = $pdo->prepare($sql);
                $stmt->execute([
                    $id_responsa,
                    $notif_email_atividades, $notif_email_lembretes, $notif_email_novidades,
                    $notif_push_mensagens, $notif_push_alertas,
                    $privacidade_perfil, $mostrar_email, $mostrar_telefone,
                    $tema, $tamanho_fonte, $modo_alto_contraste, $animacoes_reduzidas,
                    $idioma, $fuso_horario, $autenticacao_dois_fatores
                ]);
            }
            
            echo json_encode(['success' => true, 'message' => 'Configurações salvas com sucesso!']);
            
        } catch (PDOException $e) {
            echo json_encode(['success' => false, 'message' => 'Erro ao salvar configurações: ' . $e->getMessage()]);
        }
    }
    
    elseif ($acao === 'alterar_senha') {
        $senha_atual = $_POST['senha_atual'] ?? '';
        $senha_nova = $_POST['senha_nova'] ?? '';
        $senha_confirmar = $_POST['senha_confirmar'] ?? '';
        
        if ($senha_nova !== $senha_confirmar) {
            echo json_encode(['success' => false, 'message' => 'As senhas não coincidem']);
            exit;
        }
        
        try {
            // Buscar senha atual do banco
            $stmt = $pdo->prepare("SELECT senha FROM cad_responsavel WHERE id_responsa = ?");
            $stmt->execute([$id_responsa]);
            $usuario = $stmt->fetch();
            
            // Verificar senha atual (assumindo que você usa hash)
            if (password_verify($senha_atual, $usuario['senha']) || $senha_atual === $usuario['senha']) {
                // Atualizar para nova senha
                $senha_hash = password_hash($senha_nova, PASSWORD_DEFAULT);
                $stmt = $pdo->prepare("UPDATE cad_responsavel SET senha = ? WHERE id_responsa = ?");
                $stmt->execute([$senha_hash, $id_responsa]);
                
                echo json_encode(['success' => true, 'message' => 'Senha alterada com sucesso!']);
            } else {
                echo json_encode(['success' => false, 'message' => 'Senha atual incorreta']);
            }
            
        } catch (PDOException $e) {
            echo json_encode(['success' => false, 'message' => 'Erro ao alterar senha: ' . $e->getMessage()]);
        }
    }
    
    elseif ($acao === 'exportar_dados') {
        try {
            // Buscar todos os dados do usuário
            $stmt = $pdo->prepare("
                SELECT r.*, c.* 
                FROM cad_responsavel r 
                LEFT JOIN configuracoes_usuario c ON r.id_responsa = c.id_responsa 
                WHERE r.id_responsa = ?
            ");
            $stmt->execute([$id_responsa]);
            $dados = $stmt->fetch(PDO::FETCH_ASSOC);
            
            // Remover senha dos dados exportados
            unset($dados['senha']);
            
            // Retornar JSON para download
            header('Content-Type: application/json');
            header('Content-Disposition: attachment; filename="meus_dados_mentes_brilhantes.json"');
            echo json_encode($dados, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
            exit;
            
        } catch (PDOException $e) {
            echo json_encode(['success' => false, 'message' => 'Erro ao exportar dados']);
        }
    }
}

// Buscar configurações atuais do usuário
elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
    try {
        $stmt = $pdo->prepare("SELECT * FROM configuracoes_usuario WHERE id_responsa = ?");
        $stmt->execute([$id_responsa]);
        $config = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$config) {
            // Criar configurações padrão se não existirem
            $stmt = $pdo->prepare("INSERT INTO configuracoes_usuario (id_responsa) VALUES (?)");
            $stmt->execute([$id_responsa]);
            
            // Buscar novamente
            $stmt = $pdo->prepare("SELECT * FROM configuracoes_usuario WHERE id_responsa = ?");
            $stmt->execute([$id_responsa]);
            $config = $stmt->fetch(PDO::FETCH_ASSOC);
        }
        
        echo json_encode(['success' => true, 'config' => $config]);
        
    } catch (PDOException $e) {
        echo json_encode(['success' => false, 'message' => 'Erro ao buscar configurações']);
    }
}
?>
