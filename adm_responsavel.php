 <?php
// ============================================
// INICIAR SESSÃO
// ============================================
session_start();

// ============================================
// INCLUIR CONEXÃO COM O BANCO DE DADOS
// ============================================

// Caminho absoluto para conexao.php
$conexao_path = __DIR__ . '/../php/conexao.php';

// Incluir com verificação
if (file_exists($conexao_path)) {
    require_once($conexao_path);
} else {
    die('Erro: Arquivo conexao.php não encontrado em: ' . $conexao_path);
}

// Verificar se a conexão foi estabelecida
if (!isset($sql) || $sql->connect_error) {
    die('Erro de conexão com banco de dados: ' . (isset($sql) ? $sql->connect_error : 'Conexão não inicializada'));
}

// ============================================
// VERIFICAR AUTENTICAÇÃO
// ============================================

// Verificar se usuário está logado
if (!isset($_SESSION['usuario_logado']) || $_SESSION['tipo_usuario'] !== 'responsavel') {
    header('Location: ../html/login.html');
    exit;
}

// ============================================
// BUSCAR DADOS DO RESPONSÁVEL
// ============================================

$id_responsavel = $_SESSION['id_responsavel'];
$stmt = $sql->prepare("SELECT nome, email, perfil, sexo FROM cad_responsavel WHERE id_responsa = ?");

$stmt->bind_param("i", $id_responsavel);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $dados = $result->fetch_assoc();
    $nome_completo = $dados['nome'];
    $email = $dados['email'];
    $foto_perfil = $dados['perfil'];
    
    // Obter primeiro nome
    $nomes = explode(' ', trim($nome_completo));
    $primeiro_nome = $nomes[0];
    $sexo = $dados['sexo'];
    $saudacao = ($sexo == 'Feminino') ? 'Bem-vinda' : 'Bem-vindo';

    
    // Obter iniciais
    if (count($nomes) >= 2) {
        $iniciais = strtoupper(substr($nomes[0], 0, 1) . substr($nomes[count($nomes) - 1], 0, 1));
    } else {
        $iniciais = strtoupper(substr($nome_completo, 0, 1));
    }
} else {
    header("Location: ../html/login.html");
    exit;
}

$stmt->close();
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mentes Brilhantes</title>
    <link rel="stylesheet" href="../css/adm_responsavel.css">
    <link rel="icon" type="image/x-icon" href="../img/LogoMB.png">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- Bootstrap CSS (necessário para o modal) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- jQuery Mask Plugin -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.min.js"></script>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    </head>
<body>
    
    <!-- ========== ONBOARDING MODAL ========== -->
    <!-- ONBOARDING MODAL -->
<div class="onboarding-modal" id="onboardingModal">
    <div class="onboarding-content">

        <div class="onboarding-progress">
            <div class="onboarding-progress-bar">
                <div id="onboardingProgress"></div>
            </div>
            <div class="onboarding-step-indicator">
                <span id="currentStep">1</span> de 4
            </div>
        </div>

        <!-- Etapa 1: Boas-vindas -->
        <div class="onboarding-step active" data-step="1">
            <div class="onboarding-icon">
                <i class="fas fa-heart"></i>
            </div>
            <h2 class="onboarding-title">Bem-vindo ao Mentes Brilhantes!</h2>
            <p class="onboarding-description">
                Sua plataforma completa para acompanhar o desenvolvimento do seu familiar neurodivergente e conectar com especialistas qualificados.
            </p>
            <div class="onboarding-illustration">
                <i class="fas fa-users" style="font-size: 4rem; color: var(--primary-blue); opacity: 0.3;"></i>
            </div>
        </div>

        <!-- Etapa 2: Funcionalidades Principais -->
        <div class="onboarding-step" data-step="2">
            <div class="onboarding-icon">
                <i class="fas fa-rocket"></i>
            </div>
            <h2 class="onboarding-title">Tudo em Um Só Lugar</h2>
            <p class="onboarding-description">
                Gerencie o acompanhamento completo com ferramentas profissionais e intuitivas.
            </p>
            <ul class="onboarding-features">
                <li><i class="fas fa-check-circle"></i> Dashboard com visão geral completa</li>
                <li><i class="fas fa-check-circle"></i> Perfil sensorial detalhado</li>
                <li><i class="fas fa-check-circle"></i> Marcos de desenvolvimento</li>
                <li><i class="fas fa-check-circle"></i> Assistente virtual Luna</li>
                <li><i class="fas fa-check-circle"></i> Relatórios personalizados</li>
            </ul>
        </div>

        <!-- Etapa 3: Equipe e Recursos -->
        <div class="onboarding-step" data-step="3">
            <div class="onboarding-icon">
                <i class="fas fa-user-md"></i>
            </div>
            <h2 class="onboarding-title">Especialistas e Conteúdos</h2>
            <p class="onboarding-description">
                Conecte-se com profissionais especializados e acesse materiais educativos de qualidade.
            </p>
            <div class="onboarding-features-grid">
                <div class="feature-box">
                    <i class="fas fa-calendar-check"></i>
                    <h4>Agendamento Fácil</h4>
                    <p>Marque consultas presenciais ou online</p>
                </div>
                <div class="feature-box">
                    <i class="fas fa-book-open"></i>
                    <h4>Biblioteca Educativa</h4>
                    <p>Acesse cartilhas, vídeos e guias</p>
                </div>
            </div>
        </div>

        <!-- Etapa 4: Termos de Uso -->
        <div class="onboarding-step" data-step="4">
            <div class="onboarding-icon">
                <i class="fas fa-shield-alt"></i>
            </div>
            <h2 class="onboarding-title">Termos de Uso e Privacidade</h2>
            <p class="onboarding-description">
                Para começar, precisamos que você aceite nossos termos de serviço e política de privacidade.
            </p>
            
            <div class="onboarding-terms-box">
                <div class="terms-info">
                    <i class="fas fa-info-circle"></i>
                    <div>
                        <h4>Plataforma Gratuita</h4>
                        <p>
                            O Mentes Brilhantes é uma plataforma <strong>100% gratuita</strong> dedicada a apoiar 
                            famílias e profissionais no cuidado com pessoas neurodivergentes.
                        </p>
                    </div>
                </div>
            </div>

            <div class="onboarding-terms">
                <label class="onboarding-checkbox-container">
                    <input type="checkbox" id="acceptTerms" required>
                    <span class="checkmark"></span>
                    <span class="checkbox-label">
                        Li e concordo com os <a href="../html/termos-de-uso.html" target="_blank">Termos de Uso</a> 
                        e <a href="../html/politica-privacidade.html" target="_blank">Política de Privacidade</a>
                    </span>
                </label>
            </div>

            <p class="onboarding-terms-note">
                <i class="fas fa-lock"></i> Seus dados estão protegidos e seguimos a LGPD rigorosamente
            </p>
        </div>

        <!-- Navegação -->
        <div class="onboarding-navigation">
            <button class="btn-onboarding btn-onboarding-secondary" id="prevStep" style="visibility: hidden;">
            Anterior
            </button>
            <button class="btn-onboarding btn-onboarding-primary" id="nextStep">
                Próximo
            </button>
            <button class="btn-onboarding btn-onboarding-primary" id="finishOnboarding" style="display: none;">
                Começar a Usar
            </button>
        </div>
    </div>
</div>

    

    <!-- Header -->
    <header class="header">
        <div class="header-container">
            <div class="logo-section">
                <img src="../img/LogoMB.png" alt="Logo" style="width: 32px; height: 32px;">
                <span>Mentes Brilhantes</span>
            </div>
            <nav class="header-nav">
            <div class="user-profile">
    <?php if (!empty($foto_perfil) && file_exists($foto_perfil)): ?>
        <div class="user-avatar" id="header-avatar" style="background-image: url('<?php echo htmlspecialchars($foto_perfil); ?>'); background-size: cover; background-position: center;"></div>
    <?php else: ?>
        <div class="user-avatar" id="header-avatar"><?php echo $iniciais; ?></div>
    <?php endif; ?>
    <div class="user-info">
        <h4 id="header-nome"><?php echo htmlspecialchars($primeiro_nome); ?></h4>
        <p>Responsável</p>
    </div>
</div>

                <button onclick="fazerLogout()" class="btn btn-outline" style="margin-left: 1rem;">
                    <i class="fas fa-sign-out-alt"></i>
                </button>
            </nav>
        </div>
    </header>

    <!-- Layout Principal -->
    <div class="main-layout">
        <!-- Sidebar -->
        <aside class="sidebar">
            <nav>
                <div class="nav-section">
                    <h3 class="nav-title">Menu Principal</h3>
                    <a href="#" class="nav-item active" data-section="dashboard">
                        <i class="fas fa-home"></i>
                        <span>Dashboard</span>
                    </a>
                    <a href="#" class="nav-item" data-section="perfil">
                        <i class="fas fa-user"></i>
                        <span>Meu Perfil</span>
                    </a>
                    <a href="#" class="nav-item" data-section="luna">
                        <i class="fas fa-robot"></i>
                        <span>Assistente Luna</span>
                    </a>
                    
                </div>

                <div class="nav-section">
                    <h3 class="nav-title">Área do Neurodivergente</h3>
                    <a href="#" class="nav-item" data-section="perfil-autista1">
                        <i class="fas fa-child"></i>
                        <span>Perfil do Autista</span>
                    </a>
                    <a href="#" class="nav-item" data-section="perfil-autista">
                        <i class="fas fa-brain"></i>
                        <span>Perfil Sensorial</span>
                    </a>
                    
                </div>

                <div class="nav-section">
                    <h3 class="nav-title">Acompanhamento</h3>
                    <a href="#" class="nav-item" data-section="progresso">
                        <i class="fas fa-chart-line"></i>
                        <span>Progresso</span>
                    </a>
                    <a href="#" class="nav-item" data-section="especialistas">
                        <i class="fas fa-user-md"></i>
                        <span>Especialistas</span>
                    </a>
                    <a href="#" class="nav-item" data-section="relatorios">
                        <i class="fas fa-file-alt"></i>
                        <span>Relatórios</span>
                    </a>
                </div>

                <div class="nav-section">
                    <h3 class="nav-title">Suporte</h3>
                    <a href="#" class="nav-item" data-section="recursos">
                        <i class="fas fa-book"></i>
                        <span>Recursos & Suporte</span>
                    </a>
                    <a href="#" class="nav-item" data-section="configuracoes">
                        <i class="fas fa-cog"></i>
                        <span>Configurações</span>
                    </a>
                </div>
            </nav>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <!-- Dashboard Section -->
            <section id="dashboard" class="section active">
                <div class="page-header">
                <h1 class="page-title" id="dashboard-boas-vindas"><?php echo $saudacao . ', ' . $primeiro_nome; ?></h1>
                    <p class="page-subtitle">Acompanhe o desenvolvimento e progresso</p>
                </div>

                <!-- Stats Grid -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">Sessões Realizadas</span>
                            <div class="stat-icon primary">
                                <i class="fas fa-calendar-check"></i>
                            </div>
                        </div>
                        <div class="stat-number">42</div>
                        <div class="stat-change positive">
                            <i class="fas fa-arrow-up"></i>
                            <span>8% este mês</span>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">Marcos Alcançados</span>
                            <div class="stat-icon success">
                                <i class="fas fa-trophy"></i>
                            </div>
                        </div>
                        <div class="stat-number">15</div>
                        <div class="stat-change positive">
                            <i class="fas fa-arrow-up"></i>
                            <span>3 novos</span>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">Próximas Consultas</span>
                            <div class="stat-icon warning">
                                <i class="fas fa-clock"></i>
                            </div>
                        </div>
                        <div class="stat-number">3</div>
                        <div class="stat-change">
                            <i class="fas fa-calendar"></i>
                            <span>Próxima em 2 dias</span>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">Dias Ativos</span>
                            <div class="stat-icon secondary">
                                <i class="fas fa-star"></i>
                            </div>
                        </div>
                        <div class="stat-number">89</div>
                        <div class="stat-change positive">
                            <i class="fas fa-arrow-up"></i>
                            <span>Sequência atual</span>
                        </div>
                    </div>
                </div>

                <!-- Content Grid -->
                <div class="content-grid">
                    <!-- Main Panel -->
                    <div class="main-panel">
                        <!-- Atividade Recente -->
                        <div class="card">
                            <div class="card-header">
                                <h2 class="card-title">Atividade Recente</h2>
                            </div>
                            <div class="card-content">
                                <div class="table-container">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Data</th>
                                                <th>Atividade</th>
                                                <th>Status</th>
                                                <th>Especialista</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>Hoje</td>
                                                <td>Sessão de Fonoaudiologia</td>
                                                <td><span class="badge success">Concluída</span></td>
                                                <td>Dra. Ana Costa</td>
                                            </tr>
                                            <tr>
                                                <td>Ontem</td>
                                                <td>Atividade Cognitiva</td>
                                                <td><span class="badge success">Concluída</span></td>
                                                <td>Sistema</td>
                                            </tr>
                                            <tr>
                                                <td>15/11</td>
                                                <td>Consulta Psicológica</td>
                                                <td><span class="badge warning">Agendada</span></td>
                                                <td>Dr. Carlos Lima</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

            </section>



<!-- ========================================= -->
<!-- SEÇÃO: PERFIL DO AUTISTA - CORRIGIDA -->
<!-- ========================================= -->
<section id="perfil-autista1" class="section">
    <div class="page-header">
        <div>
            <h1 class="page-title">Perfil do Autista</h1>
            <p class="page-subtitle">Informações e configurações do perfil</p>
        </div>
        <a href="../html/cadastroneuro.html" class="btn btn-primary">
            <i class="fas fa-user-plus"></i> Adicionar Neurodivergente
        </a>
    </div>

    <!-- Loading Spinner -->
    <div id="loading-neurodivergentes" style="display: none; text-align: center; padding: 40px;">
        <i class="fas fa-spinner fa-spin" style="font-size: 48px; color: #667eea;"></i>
        <p style="margin-top: 20px; color: #666;">Carregando informações...</p>
    </div>

    <!-- Container de Conteúdo -->
    <div id="container-neurodivergentes">
        <!-- Seletor de Neurodivergentes (aparece quando há mais de um) -->
        <div id="seletor-neurodivergentes" style="display: none; margin-bottom: 20px;">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title"><i class="fas fa-users"></i> Selecionar Perfil</h2>
                </div>
                <div class="card-content">
                    <select id="select-neurodivergente" class="form-input" style="padding: 12px; font-size: 16px; cursor: pointer;">
                        <!-- Opções serão preenchidas via JavaScript -->
                    </select>
                </div>
            </div>
        </div>

        <!-- Layout Grid: Main Content + Sidebar -->
        <div class="content-grid">
            <!-- Painel Principal (Esquerda) -->
            <div class="main-panel">
                <!-- Informações Pessoais -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Informações Pessoais</h2>
                    </div>
                    <div class="card-content" id="info-pessoais-content">
                        <!-- Estado Vazio -->
                        <div style="text-align: center; padding: 60px 20px; color: #666;">
                            <i class="fas fa-user-plus" style="font-size: 64px; margin-bottom: 20px; color: #667eea; opacity: 0.3;"></i>
                            <p style="font-size: 18px; margin-bottom: 10px; font-weight: 500;">Nenhum neurodivergente cadastrado</p>
                            <p style="font-size: 14px; color: #999; margin-bottom: 20px;">Clique em "Adicionar Neurodivergente" para cadastrar</p>
                            <a href="../html/cadastroneuro.html" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Cadastrar Agora
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Endereço (Movido para baixo) -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Endereço</h2>
                    </div>
                    <div class="card-content" id="endereco-content">
                        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem;">
                            <div class="form-group" style="grid-column: 1 / -1;">
                                <label class="form-label">Rua</label>
                                <input type="text" class="form-input" id="neuro-rua" disabled>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Número</label>
                                <input type="text" class="form-input" id="neuro-numero" disabled>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Complemento</label>
                                <input type="text" class="form-input" id="neuro-complemento" disabled>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Bairro</label>
                                <input type="text" class="form-input" id="neuro-bairro" disabled>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Cidade</label>
                                <input type="text" class="form-input" id="neuro-cidade" disabled>
                            </div>
                            <div class="form-group">
                                <label class="form-label">CEP</label>
                                <input type="text" class="form-input" id="neuro-cep" disabled>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Painel Lateral (Direita) -->
            <div class="side-panel">
                <!-- Foto do Perfil (Movida para o topo da sidebar) -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Foto do Perfil</h2>
                    </div>
                    <div class="card-content">
                        <div style="text-align: center;">
                            <div id="neuro-avatar-display" style="width: 120px; height: 120px; margin: 0 auto 1rem; background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-blue-dark) 100%); color: white; border-radius: var(--radius-xl); display: flex; align-items: center; justify-content: center; font-size: 3rem; font-weight: var(--weight-semibold);">
                                <span id="neuro-iniciais">?</span>
                            </div>
                            <p style="font-size: var(--text-sm); color: var(--text-medium); font-weight: var(--weight-medium);" id="neuro-nome-display">Selecione um perfil</p>
                        </div>
                    </div>
                </div>

                <!-- Equipe Médica -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Equipe Médica</h2>
                    </div>
                    <div class="card-content">
                        <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                            <div style="padding: 0.75rem; background: var(--bg-elevated); border-radius: var(--radius-md); display: flex; align-items: center; gap: 0.75rem;">
                                <div style="width: 40px; height: 40px; background: rgba(74, 144, 226, 0.1); color: var(--primary-blue); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0;">
                                    <i class="fas fa-user-md"></i>
                                </div>
                                <div style="flex: 1;">
                                    <div style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">Dr. Pedro Oliveira</div>
                                    <div style="font-size: var(--text-xs); color: var(--text-medium);">Neuropediatra</div>
                                </div>
                            </div>
                            <div style="padding: 0.75rem; background: var(--bg-elevated); border-radius: var(--radius-md); display: flex; align-items: center; gap: 0.75rem;">
                                <div style="width: 40px; height: 40px; background: rgba(84, 199, 164, 0.1); color: var(--primary-green); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0;">
                                    <i class="fas fa-user-md"></i>
                                </div>
                                <div style="flex: 1;">
                                    <div style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">Dra. Ana Costa</div>
                                    <div style="font-size: var(--text-xs); color: var(--text-medium);">Fonoaudióloga</div>
                                </div>
                            </div>
                            <div style="padding: 0.75rem; background: var(--bg-elevated); border-radius: var(--radius-md); display: flex; align-items: center; gap: 0.75rem;">
                                <div style="width: 40px; height: 40px; background: rgba(255, 107, 53, 0.1); color: var(--primary-orange); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0;">
                                    <i class="fas fa-user-md"></i>
                                </div>
                                <div style="flex: 1;">
                                    <div style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">Dr. Carlos Lima</div>
                                    <div style="font-size: var(--text-xs); color: var(--text-medium);">Psicólogo</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Contatos de Emergência -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Contatos de Emergência</h2>
                    </div>
                    <div class="card-content">
                        <div style="display: flex; flex-direction: column; gap: 1rem;">
                            <div>
                                <div style="font-size: var(--text-xs); font-weight: var(--weight-semibold); text-transform: uppercase; color: var(--text-medium); margin-bottom: 0.5rem;">Responsável Principal</div>
                                <div style="padding: 0.75rem; background: var(--bg-elevated); border-radius: var(--radius-md);">
                                    <div style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark); margin-bottom: 0.25rem;" id="nome-responsavel">Carregando...</div>
                                    <div style="font-size: var(--text-sm); color: var(--text-medium); display: flex; align-items: center; gap: 0.5rem;">
                                        <i class="fas fa-phone" style="color: var(--primary-blue);"></i>
                                        <span id="celular-responsavel">Carregando...</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- ========================================= -->
<!-- SEÇÃO: MEU PERFIL - CORRIGIDA COM SIDEBAR -->
<!-- ========================================= -->
<section id="perfil" class="section">
    <div class="page-header">
        <h1 class="page-title">Meu Perfil</h1>
        <p class="page-subtitle">Gerencie suas informações pessoais e preferências</p>
    </div>

    <!-- Layout Grid: Main Content + Sidebar -->
    <div class="content-grid">
        <!-- Painel Principal (Esquerda) -->
        <div class="main-panel">
            <!-- Informações Pessoais -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Informações Pessoais</h2>
                </div>
                <div class="card-content">
                    <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem;">
                        <div class="form-group">
                            <label class="form-label">Nome Completo</label>
                            <input type="text" class="form-input" id="perfil-nome" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-input" id="perfil-email" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Celular</label>
                            <input type="tel" class="form-input" id="perfil-celular" placeholder="(11) 99999-9999" maxlength="15">
                        </div>
                        <div class="form-group">
                            <label class="form-label">CPF</label>
                            <input type="text" class="form-input" id="perfil-cpf" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Data de Nascimento</label>
                            <input type="text" class="form-input" id="perfil-data-nascimento" disabled title="Este campo não pode ser alterado">
                        </div>
                    </div>
                </div>
                <div class="card-footer">
                    <button class="btn btn-secondary" onclick="cancelarEdicao()">Cancelar</button>
                    <button class="btn btn-primary" onclick="salvarAlteracoes()">Salvar Alterações</button>
                </div>
            </div>

            <!-- Endereço -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Endereço</h2>
                </div>
                <div class="card-content">
                    <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem;">
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Rua</label>
                            <input type="text" class="form-input" id="perfil-rua" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Número</label>
                            <input type="text" class="form-input" id="perfil-numero" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Complemento</label>
                            <input type="text" class="form-input" id="perfil-complemento" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Bairro</label>
                            <input type="text" class="form-input" id="perfil-bairro" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Cidade</label>
                            <input type="text" class="form-input" id="perfil-cidade" disabled title="Este campo não pode ser alterado">
                        </div>
                        <div class="form-group">
                            <label class="form-label">CEP</label>
                            <input type="text" class="form-input" id="perfil-cep" disabled title="Este campo não pode ser alterado">
                        </div>
                    </div>
                </div>
            </div>

            <!-- Segurança -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Segurança</h2>
                </div>
                <div class="card-content">
                    <div style="display: flex; flex-direction: column; gap: 1rem; padding: 1rem; background: var(--bg-elevated); border-radius: var(--radius-md);">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <div style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">Autenticação de Dois Fatores</div>
                                <div style="font-size: var(--text-xs); color: var(--text-medium); margin-top: 0.25rem;">Adicione uma camada extra de segurança</div>
                            </div>
                            <label class="switch">
                                <input type="checkbox">
                                <span class="slider"></span>
                            </label>
                        </div>
                        <button class="btn btn-outline" style="width: fit-content;">
                            <i class="fas fa-key"></i> Alterar Senha
                        </button>
                    </div>

                    <h3 style="margin-top: 1.5rem; margin-bottom: 0.75rem; font-size: var(--text-base); font-weight: var(--weight-semibold);">Sessões Ativas</h3>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Dispositivo</th>
                                    <th>Local</th>
                                    <th>Data</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><i class="fas fa-laptop"></i> Chrome - Windows</td>
                                    <td>São Paulo, BR</td>
                                    <td>Agora</td>
                                    <td><span class="badge primary">Atual</span></td>
                                </tr>
                                <tr>
                                    <td><i class="fas fa-mobile-alt"></i> Safari - iOS</td>
                                    <td>São Paulo, BR</td>
                                    <td>2 horas atrás</td>
                                    <td><button class="btn btn-sm btn-outline">Encerrar</button></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Painel Lateral (Direita) - ADICIONADO -->
        <div class="side-panel">
            <!-- Foto do Perfil -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Foto do Perfil</h2>
                </div>
                <div class="card-content">
                    <div style="text-align: center;">
                        <div id="perfil-avatar-display" style="width: 120px; height: 120px; margin: 0 auto 1rem; background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-blue-dark) 100%); color: white; border-radius: var(--radius-xl); display: flex; align-items: center; justify-content: center; font-size: 3rem; font-weight: var(--weight-semibold); position: relative; overflow: hidden;">
                            <span id="perfil-iniciais">M</span>
                            <img id="perfil-foto-img" style="width: 100%; height: 100%; object-fit: cover; position: absolute; top: 0; left: 0; display: none;">
                        </div>
                        <input type="file" id="input-foto-perfil" accept="image/*" style="display: none;">
                        <button class="btn btn-outline btn-sm" onclick="document.getElementById('input-foto-perfil').click()">
                            <i class="fas fa-camera"></i> Alterar Foto
                        </button>
                        <p style="margin-top: 0.5rem; font-size: var(--text-xs); color: var(--text-light);">Tamanho máximo: 5MB</p>
                    </div>
                </div>
            </div>

            <!-- Estatísticas da Conta -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Estatísticas da Conta</h2>
                </div>
                <div class="card-content">
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.5rem 0; border-bottom: 1px solid var(--border-light);">
                            <span style="font-size: var(--text-sm); color: var(--text-medium);">Membro desde</span>
                            <span style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">Janeiro 2024</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.5rem 0; border-bottom: 1px solid var(--border-light);">
                            <span style="font-size: var(--text-sm); color: var(--text-medium);">Sessões totais</span>
                            <span style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">42</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.5rem 0;">
                            <span style="font-size: var(--text-sm); color: var(--text-medium);">Último acesso</span>
                            <span style="font-size: var(--text-sm); font-weight: var(--weight-semibold); color: var(--text-dark);">Hoje</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Preferências -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Preferências</h2>
                </div>
                <div class="card-content">
                    <div style="display: flex; flex-direction: column; gap: 1rem;">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <div style="font-size: var(--text-sm); font-weight: var(--weight-medium); color: var(--text-dark);">Notificações por Email</div>
                                <div style="font-size: var(--text-xs); color: var(--text-medium); margin-top: 0.25rem;">Receba atualizações por email</div>
                            </div>
                            <label class="switch">
                                <input type="checkbox">
                                <span class="slider"></span>
                            </label>
                        </div>
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <div style="font-size: var(--text-sm); font-weight: var(--weight-medium); color: var(--text-dark);">Notificações Push</div>
                                <div style="font-size: var(--text-xs); color: var(--text-medium); margin-top: 0.25rem;">Receba alertas no navegador</div>
                            </div>
                            <label class="switch">
                                <input type="checkbox" checked>
                                <span class="slider"></span>
                            </label>
                        </div>
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <div style="font-size: var(--text-sm); font-weight: var(--weight-medium); color: var(--text-dark);">Lembrete de Consultas</div>
                                <div style="font-size: var(--text-xs); color: var(--text-medium); margin-top: 0.25rem;">Alertas antes das consultas</div>
                            </div>
                            <label class="switch">
                                <input type="checkbox" checked>
                                <span class="slider"></span>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

            <!-- Luna Section -->
            <section id="luna" class="section">
                <div class="page-header">
                    <h1 class="page-title">Assistente Luna</h1>
                    <p class="page-subtitle">IA especializada em Transtorno do Espectro Autista</p>
                </div>

                <div class="card" style="max-width: 900px; margin: 0 auto;">
                    <div class="card-content" style="min-height: 500px; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 3rem;">
                        <div style="width: 120px; height: 120px; background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-blue-dark) 100%); color: white; border-radius: var(--radius-2xl); display: flex; align-items: center; justify-content: center; font-size: 4rem; margin-bottom: 2rem; box-shadow: var(--shadow-xl);">
                            <i class="fas fa-robot"></i>
                        </div>
                        <h3 style="font-size: var(--text-2xl); font-weight: var(--weight-semibold); color: var(--text-dark); margin-bottom: 1rem;">Olá! Sou a Luna</h3>
                        <p style="font-size: var(--text-base); color: var(--text-medium); max-width: 500px; margin-bottom: 2rem;">Estou aqui para ajudar você com informações sobre o desenvolvimento, tirar dúvidas sobre TEA e oferecer orientações personalizadas.</p>
                        <button class="btn btn-primary btn-lg" onclick="window.location.href='../html/luna.php'">
                            <i class="fas fa-comments"></i>
                            Conversar com Luna
                        </button>
                    </div>
                </div>
            </section>

            <!-- Perfil Sensorial Section -->
            <section class="section" id="perfil-autista">
                <div class="page-header-simple">
                    <h1 class="page-title-simple">Perfil Sensorial</h1>
                    <button class="btn btn-outline-primary" onclick="shareProfile()">
                        <i class="fas fa-share-alt"></i> Compartilhar Perfil
                    </button>
                </div>

                <!-- INTERESSES E PREFERÊNCIAS -->
                <div class="card-modern">
                    <h2 class="section-title">Interesses e Preferências</h2>
                    
                    <div class="two-column-grid">
                        <!-- Coluna 1: Interesses Especiais -->
                        <div class="column-content">
                            <h3 class="subsection-title">Interesses Especiais</h3>
                            <div class="tags-container" id="interesses-container">
                                <span class="tag tag-blue">Dinossauros</span>
                                <span class="tag tag-blue">Trens</span>
                                <span class="tag tag-blue">Blocos de Montar</span>
                                <span class="tag tag-blue">Música</span>
                            </div>
                            <button class="btn-add" onclick="addItem('interesses')">
                                <i class="fas fa-plus"></i> Adicionar
                            </button>
                        </div>

                        <!-- Coluna 2: Alimentos Preferidos -->
                        <div class="column-content">
                            <h3 class="subsection-title">Alimentos Preferidos</h3>
                            <div class="checklist-container" id="alimentos-container">
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Macarrão com molho de tomate</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('alimentos', 1)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('alimentos', 1)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Frango grelhado</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('alimentos', 2)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('alimentos', 2)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Banana</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('alimentos', 3)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('alimentos', 3)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Iogurte natural</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('alimentos', 4)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('alimentos', 4)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <button class="btn-add" onclick="addItem('alimentos')">
                                <i class="fas fa-plus"></i> Adicionar
                            </button>
                        </div>
                    </div>
                </div>

                <!-- PERFIL SENSORIAL -->
                <div class="card-modern">
                    <h2 class="section-title">Perfil Sensorial</h2>
                    
                    <div class="two-column-grid">
                        <!-- Coluna 1: Sensibilidades -->
                        <div class="column-content">
                            <h3 class="subsection-title">Sensibilidades</h3>
                            <div class="tags-container" id="sensibilidades-container">
                                <span class="tag tag-orange">Sons Altos</span>
                                <span class="tag tag-orange">Luzes Fortes</span>
                                <span class="tag tag-orange">Multidões</span>
                            </div>
                            <button class="btn-add" onclick="addItem('sensibilidades')">
                                <i class="fas fa-plus"></i> Adicionar
                            </button>
                        </div>

                        <!-- Coluna 2: Estratégias de Regulação -->
                        <div class="column-content">
                            <h3 class="subsection-title">Estratégias de Regulação</h3>
                            <div class="checklist-container" id="estrategias-container">
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Uso de fones com cancelamento de ruído</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('estrategias', 1)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('estrategias', 1)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Pausas em ambiente calmo</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('estrategias', 2)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('estrategias', 2)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Objeto de conforto (dinossauro de pelúcia)</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('estrategias', 3)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('estrategias', 3)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="checklist-item">
                                    <i class="fas fa-check check-icon"></i>
                                    <span>Respiração profunda</span>
                                    <div class="item-actions-inline">
                                        <button class="btn-icon-inline edit" onclick="editItemInline('estrategias', 4)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon-inline delete" onclick="deleteItemInline('estrategias', 4)">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <button class="btn-add" onclick="addItem('estrategias')">
                                <i class="fas fa-plus"></i> Adicionar
                            </button>
                        </div>
                    </div>
                </div>

                <!-- MARCOS DE DESENVOLVIMENTO -->
                <div class="card-modern">
                    <h2 class="section-title">Marcos de Desenvolvimento</h2>
                    
                    <div class="development-grid">
                        <div class="development-card">
                            <div class="development-header">
                                <div class="development-icon communication-icon">
                                    <i class="fas fa-comments"></i>
                                </div>
                                <h3 class="development-title">COMUNICAÇÃO</h3>
                            </div>
                            <p class="development-text">Usa frases de 3-4 palavras</p>
                            <div class="development-actions">
                                <button class="btn-icon-inline edit" onclick="editDevelopment('comunicacao')">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>

                        <div class="development-card">
                            <div class="development-header">
                                <div class="development-icon social-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h3 class="development-title">SOCIAL</h3>
                            </div>
                            <p class="development-text">Iniciando contato visual</p>
                            <div class="development-actions">
                                <button class="btn-icon-inline edit" onclick="editDevelopment('social')">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>

                        <div class="development-card">
                            <div class="development-header">
                                <div class="development-icon selfcare-icon">
                                    <i class="fas fa-hand-sparkles"></i>
                                </div>
                                <h3 class="development-title">AUTOCUIDADO</h3>
                            </div>
                            <p class="development-text">Rotina de higiene básica</p>
                            <div class="development-actions">
                                <button class="btn-icon-inline edit" onclick="editDevelopment('autocuidado')">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>

                        <div class="development-card">
                            <div class="development-header">
                                <div class="development-icon motor-icon">
                                    <i class="fas fa-running"></i>
                                </div>
                                <h3 class="development-title">MOTOR</h3>
                            </div>
                            <p class="development-text">Coordenação motora fina</p>
                            <div class="development-actions">
                                <button class="btn-icon-inline edit" onclick="editDevelopment('motor')">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Progresso Section -->
            <section id="progresso" class="section">
                <div class="page-header">
                    <h1 class="page-title">Progresso</h1>
                    <p class="page-subtitle">Acompanhamento detalhado do desenvolvimento</p>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Relatórios de Progresso</h2>
                    </div>
                    <div class="card-content" style="min-height: 400px; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center;">
                        <div style="width: 80px; height: 80px; background: rgba(74, 144, 226, 0.1); color: var(--primary-blue); border-radius: var(--radius-xl); display: flex; align-items: center; justify-content: center; font-size: 2.5rem; margin-bottom: 1.5rem;">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <h3 style="font-size: var(--text-xl); font-weight: var(--weight-semibold); color: var(--text-dark); margin-bottom: 0.75rem;">Esta seção estará disponível em breve.</h3>
                        <p style="font-size: var(--text-base); color: var(--text-medium); max-width: 500px;">Aqui você poderá acompanhar gráficos detalhados, estatísticas e análises do desenvolvimento.</p>
                    </div>
                </div>
            </section>

            <!-- Especialistas Section -->
            <section id="especialistas" class="section">
                <div class="page-header">
                    <h1 class="page-title">Especialistas</h1>
                    <p class="page-subtitle">Gerencie sua equipe multidisciplinar</p>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Equipe Médica</h2>
                        <button class="btn btn-primary btn-sm" onclick="window.location.href='../html/especialista.html'">
                            <i class="fas fa-plus"></i>
                            Adicionar Especialista
                        </button>
                    </div>
                    <div class="card-content">
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Especialista</th>
                                        <th>Especialidade</th>
                                        <th>Contato</th>
                                        <th>Próxima Consulta</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Dr. Pedro Oliveira</td>
                                        <td>Neuropediatra</td>
                                        <td>(11) 91234-5678</td>
                                        <td>20/11/2024</td>
                                        <td><span class="badge success">Ativo</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline">Editar</button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Dra. Ana Costa</td>
                                        <td>Fonoaudióloga</td>
                                        <td>(11) 92345-6789</td>
                                        <td>15/11/2024</td>
                                        <td><span class="badge success">Ativo</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline">Editar</button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Dr. Carlos Lima</td>
                                        <td>Psicólogo</td>
                                        <td>(11) 93456-7890</td>
                                        <td>18/11/2024</td>
                                        <td><span class="badge success">Ativo</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline">Editar</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="card" style="margin-top: 1.5rem;">
                    <div class="card-header">
                        <h2 class="card-title">Calendário de Consultas</h2>
                    </div>
                    <div class="card-content">
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Data</th>
                                        <th>Horário</th>
                                        <th>Especialista</th>
                                        <th>Tipo</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>15/11/2024</td>
                                        <td>14:00</td>
                                        <td>Dra. Ana Costa</td>
                                        <td>Fonoaudiologia</td>
                                        <td><span class="badge warning">Agendada</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline">Detalhes</button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>18/11/2024</td>
                                        <td>10:00</td>
                                        <td>Dr. Carlos Lima</td>
                                        <td>Psicologia</td>
                                        <td><span class="badge warning">Agendada</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline">Detalhes</button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>20/11/2024</td>
                                        <td>16:00</td>
                                        <td>Dra. Fernanda Santos</td>
                                        <td>Terapia Ocupacional</td>
                                        <td><span class="badge warning">Agendada</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline">Detalhes</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Relatórios Section -->
            <section id="relatorios" class="section">
                <div class="page-header">
                    <h1 class="page-title">Relatórios</h1>
                    <p class="page-subtitle">Relatórios detalhados de acompanhamento</p>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Sistema de Relatórios</h2>
                    </div>
                    <div class="card-content" style="min-height: 400px; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center;">
                        <div style="width: 80px; height: 80px; background: rgba(74, 144, 226, 0.1); color: var(--primary-blue); border-radius: var(--radius-xl); display: flex; align-items: center; justify-content: center; font-size: 2.5rem; margin-bottom: 1.5rem;">
                            <i class="fas fa-file-alt"></i>
                        </div>
                        <h3 style="font-size: var(--text-xl); font-weight: var(--weight-semibold); color: var(--text-dark); margin-bottom: 0.75rem;">Esta funcionalidade estará disponível em breve.</h3>
                        <p style="font-size: var(--text-base); color: var(--text-medium); max-width: 500px;">Aqui você poderá gerar relatórios personalizados sobre o desenvolvimento, atividades e marcos alcançados.</p>
                    </div>
                </div>
            </section>

         <!-- ======================================= -->
<!-- SEÇÃO: RECURSOS & SUPORTE -->
<!-- ======================================= -->
<section id="recursos" class="section">
    <div class="section-header">
        <div class="section-title">
            <i class="fas fa-book-open"></i>
            <div>
                <h2>Recursos & Suporte</h2>
                <p>Materiais educativos, vídeos informativos e canais de comunicação</p>
            </div>
        </div>
    </div>

    <div class="section-content">
        <!-- Tabs de Navegação -->
        <div class="recursos-tabs">
            <button class="tab-btn active" data-tab="videos">
                <i class="fas fa-video"></i> Vídeos Educativos
            </button>
            <button class="tab-btn" data-tab="materiais">
                <i class="fas fa-file-pdf"></i> Materiais PDF
            </button>
            <button class="tab-btn" data-tab="faq">
                <i class="fas fa-question-circle"></i> Perguntas Frequentes
            </button>
            <button class="tab-btn" data-tab="suporte">
                <i class="fas fa-headset"></i> Suporte
            </button>
        </div>

        <!-- Conteúdo: Vídeos Educativos -->
        <div class="tab-content active" id="videos">
            <div class="videos-grid">
                <div class="video-categoria">
                    <h3><i class="fas fa-play-circle"></i> Vídeos sobre TEA</h3>
                    <div class="videos-lista">
                        <!-- Vídeo 1 -->
                        <div class="video-item">
                            <div class="video-thumbnail">
                                <img src="https://img.youtube.com/vi/HrDFz5W7pZ4/maxresdefault.jpg" 
                                     alt="TEA - Autismo explicado para crianças"
                                     onerror="this.src='https://img.youtube.com/vi/HrDFz5W7pZ4/hqdefault.jpg'">
                                <div class="play-overlay">
                                    <i class="fas fa-play-circle"></i>
                                </div>
                                <span class="video-badge">Infantil</span>
                            </div>
                            <div class="video-info">
                                <h4>TEA - Autismo explicado para crianças</h4>
                                <p>Animação "O Mundinho da Rosa" conta a história de Duda, uma criança autista</p>
                                <a href="https://www.youtube.com/watch?v=HrDFz5W7pZ4" 
                                   target="_blank" 
                                   class="btn-watch">
                                    <i class="fas fa-external-link-alt"></i> Assistir no YouTube
                                </a>
                            </div>
                        </div>
                        
                        <!-- Vídeo 2 -->
                        <div class="video-item">
                            <div class="video-thumbnail">
                                <img src="https://img.youtube.com/vi/bcPAINb2xSQ/maxresdefault.jpg" 
                                     alt="AUTISMO - Transtorno do Espectro Autista"
                                     onerror="this.src='https://img.youtube.com/vi/bcPAINb2xSQ/hqdefault.jpg'">
                                <div class="play-overlay">
                                    <i class="fas fa-play-circle"></i>
                                </div>
                                <span class="video-badge">Educacional</span>
                            </div>
                            <div class="video-info">
                                <h4>AUTISMO - Transtorno do Espectro Autista (Aula Completa)</h4>
                                <p>Explicação detalhada sobre TEA, sintomas e níveis de comprometimento</p>
                                <a href="https://www.youtube.com/watch?v=bcPAINb2xSQ" 
                                   target="_blank" 
                                   class="btn-watch">
                                    <i class="fas fa-external-link-alt"></i> Assistir no YouTube
                                </a>
                            </div>
                        </div>

                        <!-- Vídeo 3 -->
                        <div class="video-item">
                            <div class="video-thumbnail">
                                <img src="https://img.youtube.com/vi/tWXON3pW9bE/maxresdefault.jpg" 
                                     alt="Entenda o BÁSICO sobre Autismo"
                                     onerror="this.src='https://img.youtube.com/vi/tWXON3pW9bE/hqdefault.jpg'">
                                <div class="play-overlay">
                                    <i class="fas fa-play-circle"></i>
                                </div>
                                <span class="video-badge">Introdução</span>
                            </div>
                            <div class="video-info">
                                <h4>Entenda o BÁSICO sobre Autismo - Mayra Gaiato</h4>
                                <p>O que é autismo e como funciona o neurodesenvolvimento no TEA</p>
                                <a href="https://www.youtube.com/watch?v=tWXON3pW9bE" 
                                   target="_blank" 
                                   class="btn-watch">
                                    <i class="fas fa-external-link-alt"></i> Assistir no YouTube
                                </a>
                            </div>
                        </div>

                        <!-- Vídeo 4 -->
                        <div class="video-item">
                            <div class="video-thumbnail">
                                <img src="https://img.youtube.com/vi/YlrI9vdQBsI/maxresdefault.jpg" 
                                     alt="Aprendendo sobre o autismo com o André"
                                     onerror="this.src='https://img.youtube.com/vi/YlrI9vdQBsI/hqdefault.jpg'">
                                <div class="play-overlay">
                                    <i class="fas fa-play-circle"></i>
                                </div>
                                <span class="video-badge">Turma da Mônica</span>
                            </div>
                            <div class="video-info">
                                <h4>Aprendendo sobre o autismo com o André!</h4>
                                <p>Turma da Mônica explica o TEA de forma lúdica para crianças</p>
                                <a href="https://www.youtube.com/watch?v=YlrI9vdQBsI" 
                                   target="_blank" 
                                   class="btn-watch">
                                    <i class="fas fa-external-link-alt"></i> Assistir no YouTube
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Conteúdo: Materiais PDF -->
        <div class="tab-content" id="materiais">
            <div class="recursos-grid-new">
                <div class="recurso-categoria-new">
                    <div class="categoria-header-new">
                        <i class="fas fa-book"></i>
                        <h3>Materiais para Download</h3>
                    </div>
                    <div class="recurso-lista-new">
                        <!-- PDF 1 -->
                        <div class="recurso-item-new">
                            <div class="pdf-cover">
                                <div class="pdf-icon-wrapper">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="pdf-pages">104 páginas</div>
                            </div>
                            <div class="recurso-info-new">
                                <span class="pdf-tag tag-blue">AMA</span>
                                <h4>Guia Prático sobre Autismo</h4>
                                <p>Publicação completa da Associação de Amigos do Autista com informações essenciais sobre TEA</p>
                                <a href="https://ama.org.br/site/downloads/" 
                                   target="_blank" 
                                   class="btn-download-new">
                                    <i class="fas fa-download"></i> Baixar PDF
                                </a>
                            </div>
                        </div>

                        <!-- PDF 2 -->
                        <div class="recurso-item-new">
                            <div class="pdf-cover pdf-cover-orange">
                                <div class="pdf-icon-wrapper">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="pdf-pages">Material Oficial</div>
                            </div>
                            <div class="recurso-info-new">
                                <span class="pdf-tag tag-orange">Direitos</span>
                                <h4>Cartilha dos Direitos da Pessoa com Autismo</h4>
                                <p>Manual completo sobre direitos garantidos por lei no Brasil para pessoas com TEA</p>
                                <a href="https://biblioteca.cofen.gov.br/cartilha-autista-atualizada/" 
                                   target="_blank" 
                                   class="btn-download-new">
                                    <i class="fas fa-download"></i> Baixar PDF
                                </a>
                            </div>
                        </div>

                        <!-- PDF 3 -->
                        <div class="recurso-item-new">
                            <div class="pdf-cover pdf-cover-green">
                                <div class="pdf-icon-wrapper">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="pdf-pages">Cartilha Oficial</div>
                            </div>
                            <div class="recurso-info-new">
                                <span class="pdf-tag tag-green">Sensorial</span>
                                <h4>Orientações Sensoriais para Crianças com TEA</h4>
                                <p>Estratégias sensoriais do Governo Federal para apoiar crianças autistas no dia a dia</p>
                                <a href="https://bvsms.saude.gov.br/bvs/publicacoes/orientacoes_estrategias_sensoriais_criancas_tea.pdf" 
                                   target="_blank" 
                                   class="btn-download-new">
                                    <i class="fas fa-download"></i> Baixar PDF
                                </a>
                            </div>
                        </div>

                        <!-- PDF 4 -->
                        <div class="recurso-item-new">
                            <div class="pdf-cover pdf-cover-blue">
                                <div class="pdf-icon-wrapper">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="pdf-pages">BVS Saúde</div>
                            </div>
                            <div class="recurso-info-new">
                                <span class="pdf-tag tag-blue">Orientação</span>
                                <h4>Autismo: Orientação para os Pais</h4>
                                <p>Cartilha elaborada pela Casa do Autista com informações básicas e orientações práticas</p>
                                <a href="https://bvsms.saude.gov.br/bvs/publicacoes/autismo_orientacao_pais.pdf" 
                                   target="_blank" 
                                   class="btn-download-new">
                                    <i class="fas fa-download"></i> Baixar PDF
                                </a>
                            </div>
                        </div>

                        <!-- PDF 5 -->
                        <div class="recurso-item-new">
                            <div class="pdf-cover pdf-cover-purple">
                                <div class="pdf-icon-wrapper">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="pdf-pages">Múltiplos Materiais</div>
                            </div>
                            <div class="recurso-info-new">
                                <span class="pdf-tag tag-purple">Cotidiano</span>
                                <h4>Cartilhas - Autismo e Realidade</h4>
                                <p>Manuais elaborados para ajudar pessoas com TEA no cotidiano e inclusão social</p>
                                <a href="https://autismoerealidade.org.br/o-que-e-o-autismo/cartilhas/" 
                                   target="_blank" 
                                   class="btn-download-new">
                                    <i class="fas fa-download"></i> Acessar Cartilhas
                                </a>
                            </div>
                        </div>

                        <!-- PDF 6 -->
                        <div class="recurso-item-new">
                            <div class="pdf-cover pdf-cover-orange">
                                <div class="pdf-icon-wrapper">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="pdf-pages">Câmara SP</div>
                            </div>
                            <div class="recurso-info-new">
                                <span class="pdf-tag tag-orange">Legal</span>
                                <h4>Manual dos Direitos da Pessoa com Autismo - SP</h4>
                                <p>Guia completo sobre direitos, benefícios e orientações legais para pessoas com TEA</p>
                                <a href="https://www.saopaulo.sp.leg.br/apartes-anteriores/revista-apartes/manual-dos-direitos-da-pessoa-com-autismo/" 
                                   target="_blank" 
                                   class="btn-download-new">
                                    <i class="fas fa-download"></i> Baixar PDF
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Conteúdo: Perguntas Frequentes (FAQ) -->
        <div class="tab-content" id="faq">
            <div class="faq-container-new">
                <div class="faq-header-new">
                    <div class="faq-icon-header">
                        <i class="fas fa-lightbulb"></i>
                    </div>
                    <h3>Perguntas Frequentes sobre TEA</h3>
                    <p>Tire suas dúvidas sobre o Transtorno do Espectro Autista</p>
                </div>

                <div class="faq-categories">
                    <!-- Categoria: Diagnóstico -->
                    <div class="faq-category">
                        <h4 class="faq-category-title">
                            <i class="fas fa-stethoscope"></i> Diagnóstico e Identificação
                        </h4>
                        <div class="faq-list-new">
                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>O que é o Transtorno do Espectro Autista (TEA)?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>O TEA é um transtorno do neurodesenvolvimento caracterizado por dificuldades persistentes na comunicação e interação social, além de padrões restritos e repetitivos de comportamento, interesses ou atividades. Essas características se manifestam no início da infância e impactam o funcionamento diário. O termo "espectro" reflete a ampla variação na intensidade dos sintomas e nas necessidades de suporte de cada pessoa.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Quais são os primeiros sinais de autismo em bebês e crianças pequenas?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Os sinais precoces incluem: ausência de contato visual, não responder ao nome aos 12 meses, ausência de gestos como apontar ou acenar, atraso na fala ou perda de habilidades já adquiridas, movimentos repetitivos (como balançar o corpo), fixação intensa em objetos específicos, dificuldade em expressar emoções e hipersensibilidade a sons, luzes ou texturas. É importante ressaltar que cada criança é única e pode apresentar combinações diferentes desses sinais.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Como é feito o diagnóstico do autismo?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>O diagnóstico é essencialmente clínico, realizado por profissionais especializados como neuropediatra, psiquiatra infantil ou neurologista. O processo envolve: entrevistas detalhadas com os pais/responsáveis, observação direta do comportamento da criança, aplicação de instrumentos padronizados (como M-CHAT, CARS, ADOS), avaliação do histórico de desenvolvimento e, quando necessário, exames complementares para descartar outras condições. Não existe um exame de sangue ou imagem que diagnostique o TEA.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Existem diferentes níveis de autismo?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Sim. O DSM-5 classifica o TEA em 3 níveis de suporte: Nível 1 (requer suporte) - a pessoa consegue funcionar com apoio mínimo; Nível 2 (requer suporte substancial) - necessita de apoio diário evidente; Nível 3 (requer suporte muito substancial) - necessita de apoio intensivo e constante. Essa classificação ajuda profissionais e familiares a compreender as necessidades específicas de cada pessoa e planejar intervenções adequadas.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Categoria: Causas e Prevenção -->
                    <div class="faq-category">
                        <h4 class="faq-category-title">
                            <i class="fas fa-dna"></i> Causas e Fatores de Risco
                        </h4>
                        <div class="faq-list-new">
                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>O que causa o autismo?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>As causas exatas do autismo ainda não são totalmente conhecidas, mas pesquisas apontam para uma combinação de fatores genéticos e ambientais. Estudos indicam que predisposição genética responde por cerca de 50% do risco. Fatores como idade avançada dos pais, complicações durante a gravidez ou parto, e exposição a certos fatores ambientais podem aumentar o risco. É fundamental esclarecer: vacinas NÃO causam autismo - esse mito foi completamente desmentido por inúmeros estudos científicos.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>O autismo é hereditário?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Sim, existe um componente genético significativo. Se uma família já tem uma criança com TEA, o risco de ter outro filho com o transtorno é de 10-20%, muito maior que a população geral (cerca de 1%). Além disso, irmãos gêmeos idênticos têm alta concordância para autismo. No entanto, não é um padrão de herança simples - múltiplos genes estão envolvidos, tornando a previsão complexa.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Categoria: Tratamento e Intervenções -->
                    <div class="faq-category">
                        <h4 class="faq-category-title">
                            <i class="fas fa-heartbeat"></i> Tratamento e Intervenções
                        </h4>
                        <div class="faq-list-new">
                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Meu filho foi diagnosticado com autismo. O que devo fazer?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Após o diagnóstico, os passos incluem: 1) Buscar uma equipe multidisciplinar qualificada (terapeutas ocupacionais, fonoaudiólogos, psicólogos, psicopedagogos); 2) Iniciar intervenções precoces - quanto antes, melhores os resultados; 3) Conhecer os direitos legais da criança (Lei 12.764/2012); 4) Participar de grupos de apoio com outras famílias; 5) Adaptar a rotina doméstica às necessidades da criança; 6) Cuidar da saúde mental da família. Lembre-se: o diagnóstico abre portas para suporte adequado.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Quais terapias são recomendadas para o autismo?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>As intervenções mais eficazes baseadas em evidências incluem: Análise do Comportamento Aplicada (ABA), Terapia Ocupacional (para habilidades sensoriais e motoras), Fonoaudiologia (comunicação e linguagem), Psicologia (regulação emocional e habilidades sociais), Musicoterapia, Terapia Cognitivo-Comportamental (para crianças mais velhas) e atividades físicas adaptadas. O plano de tratamento deve ser individualizado e reavaliado periodicamente pela equipe multidisciplinar.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>O autismo tem cura?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>O autismo não tem "cura" porque não é uma doença, mas sim uma condição neurológica permanente. No entanto, intervenções precoces e adequadas podem promover avanços significativos no desenvolvimento, independência e qualidade de vida. Muitas pessoas com TEA alcançam autonomia completa, frequentam escola regular, trabalham e têm relacionamentos. O objetivo do tratamento é desenvolver potencialidades, reduzir desafios e promover inclusão.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Categoria: Vida Diária e Inclusão -->
                    <div class="faq-category">
                        <h4 class="faq-category-title">
                            <i class="fas fa-home"></i> Vida Diária e Inclusão
                        </h4>
                        <div class="faq-list-new">
                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Como posso ajudar no desenvolvimento do meu filho com autismo?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Estratégias eficazes incluem: estabelecer rotinas previsíveis, usar comunicação visual (pictogramas, calendários), celebrar pequenas conquistas, respeitar hipersensibilidades sensoriais, estimular interesses especiais de forma construtiva, promover autonomia gradual em autocuidado, incentivar socialização respeitando os limites da criança e praticar a paciência. É fundamental dividir responsabilidades com outros familiares para evitar sobrecarga do cuidador principal.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Pessoas com autismo podem ter uma vida independente e produtiva?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Absolutamente! Com suporte adequado, muitas pessoas com TEA alcançam plena independência. Elas podem concluir ensino superior, ter carreiras de sucesso (especialmente em áreas que valorizam suas habilidades especiais, como tecnologia, ciências e artes), estabelecer relacionamentos, constituir família e viver de forma autônoma. O importante é respeitar as particularidades de cada indivíduo e oferecer os apoios necessários no ritmo adequado.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Como lidar com crises e comportamentos desafiadores?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>Durante crises: mantenha a calma, remova a criança de ambientes superestimulantes, ofereça um espaço seguro, evite contato físico excessivo (a menos que seja do agrado da criança), use tom de voz baixo e aguarde a desregulação passar. Prevenção: identifique gatilhos (fome, cansaço, mudanças bruscas, sobrecarga sensorial), antecipe situações difíceis, ensine estratégias de autorregulação e trabalhe com terapeutas para desenvolver habilidades de comunicação de necessidades.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Categoria: Direitos e Legislação -->
                    <div class="faq-category">
                        <h4 class="faq-category-title">
                            <i class="fas fa-balance-scale"></i> Direitos e Legislação
                        </h4>
                        <div class="faq-list-new">
                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>Quais são os direitos legais da pessoa com autismo no Brasil?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>A Lei 12.764/2012 (Lei Berenice Piana) garante: direito a diagnóstico e tratamento pelo SUS, educação inclusiva em escola regular com acompanhante especializado (se necessário), prioridade em atendimentos públicos, proteção contra discriminação, acesso ao BPC/LOAS (Benefício de Prestação Continuada) se aplicável, isenção de impostos na compra de veículo adaptado, prioridade em programas habitacionais e vagas reservadas em concursos públicos. Além disso, a Lei 13.146/2015 (Estatuto da Pessoa com Deficiência) amplia direitos de acessibilidade e inclusão.</p>
                                </div>
                            </div>

                            <div class="faq-item-new">
                                <button class="faq-question-new">
                                    <span>A escola pode recusar matrícula de criança com autismo?</span>
                                    <i class="fas fa-chevron-down"></i>
                                </button>
                                <div class="faq-answer-new">
                                    <p>NÃO! A recusa de matrícula é ilegal e constitui crime de discriminação (Lei 13.146/2015, Art. 8º). A escola tem obrigação legal de aceitar a matrícula, oferecer educação inclusiva e, se necessário, disponibilizar acompanhante especializado sem custo adicional à família. Caso ocorra recusa, os responsáveis podem: registrar queixa no Ministério Público, acionar a Secretaria de Educação local e buscar orientação jurídica através da Defensoria Pública.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Conteúdo: Suporte (MANTIDO INTACTO) -->
        <div class="tab-content" id="suporte">
            <div class="suporte-grid">
                <div class="suporte-section">
                    <h3><i class="fas fa-phone-alt"></i> Entre em Contato</h3>
                    <p class="suporte-intro">Nossa equipe está disponível para ajudar você. Entre em contato pelos canais abaixo:</p>
                    
                    <!-- E-mail -->
                    <div class="canal-item-simples">
                        <div class="canal-icon"><i class="fas fa-envelope"></i></div>
                        <div class="canal-info">
                            <h4>E-mail</h4>
                            <p>Envie sua mensagem para:</p>
                            <a href="mailto:mentesbrilhantestcc@gmail.com" class="contato-link">
                                mentesbrilhantestcc@gmail.com
                            </a>
                        </div>
                    </div>

                    <!-- Telefone -->
                    <div class="canal-item-simples">
                        <div class="canal-icon"><i class="fas fa-phone"></i></div>
                        <div class="canal-info">
                            <h4>Telefone</h4>
                            <p>Atendimento: Segunda a Sexta, 8h às 18h</p>
                            <a href="tel:+5511973402001" class="contato-link">
                                (11) 97340-2001
                            </a>
                        </div>
                    </div>

                    <!-- WhatsApp -->
                    <div class="canal-item-simples">
                        <div class="canal-icon"><i class="fas fa-phone-volume"></i></div>
                        <div class="canal-info">
                            <h4>WhatsApp</h4>
                            <p>Atendimento rápido via mensagem</p>
                            <a href="https://wa.me/5511973402001" target="_blank" class="contato-link">
                                (11) 97340-2001
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>





            <!-- Configurações Section -->
            <section id="configuracoes" class="section">
    <div class="page-header">
        <h1 class="page-title">Configurações</h1>
        <p class="page-subtitle">Personalize sua experiência na plataforma Mentes Brilhantes</p>
    </div>

    <!-- Tabs de Navegação -->
    <div class="config-tabs">
        <button class="config-tab-btn active" data-tab="notificacoes">
            <i class="fas fa-bell"></i>
            <span>Notificações</span>
        </button>
        <button class="config-tab-btn" data-tab="privacidade">
            <i class="fas fa-shield-alt"></i>
            <span>Privacidade</span>
        </button>
        <button class="config-tab-btn" data-tab="aparencia">
            <i class="fas fa-palette"></i>
            <span>Aparência</span>
        </button>
        <button class="config-tab-btn" data-tab="acessibilidade">
            <i class="fas fa-universal-access"></i>
            <span>Acessibilidade</span>
        </button>
        <button class="config-tab-btn" data-tab="conta">
            <i class="fas fa-user-cog"></i>
            <span>Conta</span>
        </button>
    </div>

    <!-- Formulário de Configurações -->
    <form id="formConfiguracoes" method="POST">
        <input type="hidden" name="acao" value="salvar_configuracoes">
        
        <!-- Tab: Notificações -->
        <div class="config-tab-content active" data-tab-content="notificacoes">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Preferências de Notificações</h2>
                    <p style="font-size: var(--text-sm); color: var(--text-medium); margin-top: 4px;">
                        Escolha como e quando você deseja receber notificações
                    </p>
                </div>
                <div class="card-content">
                    <!-- Notificações por E-mail -->
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-envelope"></i>
                            Notificações por E-mail
                        </h3>
                        
                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Atividades dos Neurodivergentes</label>
                                <p class="config-description">Receba atualizações sobre as atividades e progresso</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="notif_email_atividades" name="notif_email_atividades">
                                <span class="slider"></span>
                            </div>
                        </div>

                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Lembretes de Consultas</label>
                                <p class="config-description">Alertas sobre consultas e compromissos agendados</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="notif_email_lembretes" name="notif_email_lembretes">
                                <span class="slider"></span>
                            </div>
                        </div>

                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Novidades da Plataforma</label>
                                <p class="config-description">Informações sobre novos recursos e atualizações</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="notif_email_novidades" name="notif_email_novidades">
                                <span class="slider"></span>
                            </div>
                        </div>
                    </div>

                    <!-- Notificações Push -->
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-mobile-alt"></i>
                            Notificações Push
                        </h3>
                        
                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Mensagens de Especialistas</label>
                                <p class="config-description">Notificações instantâneas de novas mensagens</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="notif_push_mensagens" name="notif_push_mensagens">
                                <span class="slider"></span>
                            </div>
                        </div>

                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Alertas Importantes</label>
                                <p class="config-description">Alertas críticos que requerem atenção imediata</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="notif_push_alertas" name="notif_push_alertas">
                                <span class="slider"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab: Privacidade -->
        <div class="config-tab-content" data-tab-content="privacidade">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Privacidade e Segurança</h2>
                    <p style="font-size: var(--text-sm); color: var(--text-medium); margin-top: 4px;">
                        Controle quem pode ver suas informações
                    </p>
                </div>
                <div class="card-content">
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-eye"></i>
                            Visibilidade do Perfil
                        </h3>
                        
                        <div class="form-group">
                            <label class="form-label">Quem pode ver meu perfil?</label>
                            <select name="privacidade_perfil" id="privacidade_perfil" class="form-select">
                                <option value="publico">Público - Qualquer pessoa pode ver</option>
                                <option value="privado" selected>Privado - Apenas eu</option>
                                <option value="amigos">Especialistas Vinculados - Apenas profissionais que me atendem</option>
                            </select>
                        </div>

                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Mostrar E-mail no Perfil</label>
                                <p class="config-description">Permitir que outros usuários vejam seu e-mail</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="mostrar_email" name="mostrar_email">
                                <span class="slider"></span>
                            </div>
                        </div>

                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Mostrar Telefone no Perfil</label>
                                <p class="config-description">Permitir que outros usuários vejam seu telefone</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="mostrar_telefone" name="mostrar_telefone">
                                <span class="slider"></span>
                            </div>
                        </div>
                    </div>

                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-database"></i>
                            Seus Dados
                        </h3>
                        
                        <div class="config-item-full">
                            <p class="config-description" style="margin-bottom: var(--space-4);">
                                Você tem controle total sobre seus dados pessoais armazenados na plataforma.
                            </p>
                            <div class="btn-group">
                                <button type="button" class="btn btn-outline" id="btnExportarDados">
                                    <i class="fas fa-download"></i>
                                    Exportar Meus Dados
                                </button>
                                <button type="button" class="btn btn-secondary" id="btnExcluirConta">
                                    <i class="fas fa-trash-alt"></i>
                                    Solicitar Exclusão de Conta
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab: Aparência -->
        <div class="config-tab-content" data-tab-content="aparencia">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Personalização de Aparência</h2>
                    <p style="font-size: var(--text-sm); color: var(--text-medium); margin-top: 4px;">
                        Ajuste a interface de acordo com sua preferência
                    </p>
                </div>
                <div class="card-content">
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-moon"></i>
                            Tema da Interface
                        </h3>
                        
                        <div class="theme-options">
                            <label class="theme-option">
                                <input type="radio" name="tema" value="claro" checked>
                                <div class="theme-card">
                                    <div class="theme-preview theme-light">
                                        <div class="theme-bar"></div>
                                        <div class="theme-content">
                                            <div class="theme-line"></div>
                                            <div class="theme-line"></div>
                                        </div>
                                    </div>
                                    <span class="theme-name">Claro</span>
                                </div>
                            </label>

                            <label class="theme-option">
                                <input type="radio" name="tema" value="escuro">
                                <div class="theme-card">
                                    <div class="theme-preview theme-dark">
                                        <div class="theme-bar"></div>
                                        <div class="theme-content">
                                            <div class="theme-line"></div>
                                            <div class="theme-line"></div>
                                        </div>
                                    </div>
                                    <span class="theme-name">Escuro</span>
                                </div>
                            </label>

                            <label class="theme-option">
                                <input type="radio" name="tema" value="auto">
                                <div class="theme-card">
                                    <div class="theme-preview theme-auto">
                                        <div class="theme-bar"></div>
                                        <div class="theme-content">
                                            <div class="theme-line"></div>
                                            <div class="theme-line"></div>
                                        </div>
                                    </div>
                                    <span class="theme-name">Automático</span>
                                </div>
                            </label>
                        </div>
                    </div>

                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-text-height"></i>
                            Tamanho do Texto
                        </h3>
                        
                        <div class="form-group">
                            <label class="form-label">Escolha o tamanho da fonte</label>
                            <select name="tamanho_fonte" id="tamanho_fonte" class="form-select">
                                <option value="pequeno">Pequeno</option>
                                <option value="medio" selected>Médio (Padrão)</option>
                                <option value="grande">Grande</option>
                            </select>
                            <p class="config-description" style="margin-top: var(--space-2);">
                                Ajuste o tamanho da fonte para melhor legibilidade
                            </p>
                        </div>
                    </div>

                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-globe"></i>
                            Idioma e Região
                        </h3>
                        
                        <div class="form-group">
                            <label class="form-label">Idioma da Plataforma</label>
                            <select name="idioma" id="idioma" class="form-select">
                                <option value="pt-BR" selected>Português (Brasil)</option>
                                <option value="en-US">English (US)</option>
                                <option value="es-ES">Español</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Fuso Horário</label>
                            <select name="fuso_horario" id="fuso_horario" class="form-select">
                                <option value="America/Sao_Paulo" selected>Brasília (GMT-3)</option>
                                <option value="America/Manaus">Manaus (GMT-4)</option>
                                <option value="America/Rio_Branco">Rio Branco (GMT-5)</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab: Acessibilidade -->
        <div class="config-tab-content" data-tab-content="acessibilidade">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Recursos de Acessibilidade</h2>
                    <p style="font-size: var(--text-sm); color: var(--text-medium); margin-top: 4px;">
                        Ajustes para melhorar a experiência de uso
                    </p>
                </div>
                <div class="card-content">
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-adjust"></i>
                            Recursos Visuais
                        </h3>
                        
                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Modo Alto Contraste</label>
                                <p class="config-description">Aumenta o contraste entre texto e fundo para melhor legibilidade</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="modo_alto_contraste" name="modo_alto_contraste">
                                <span class="slider"></span>
                            </div>
                        </div>

                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Reduzir Animações</label>
                                <p class="config-description">Minimiza movimentos e transições na interface</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="animacoes_reduzidas" name="animacoes_reduzidas">
                                <span class="slider"></span>
                            </div>
                        </div>
                    </div>

                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-keyboard"></i>
                            Navegação
                        </h3>
                        
                        <div class="config-item-full">
                            <p class="config-description">
                                <strong>Atalhos de Teclado Disponíveis:</strong>
                            </p>
                            <ul style="list-style: none; padding: 0; margin-top: var(--space-3);">
                                <li style="padding: var(--space-2) 0; color: var(--text-medium); font-size: var(--text-sm);">
                                    <kbd style="background: var(--bg-elevated); padding: 4px 8px; border-radius: 4px; font-family: monospace;">Alt + D</kbd> - Ir para Dashboard
                                </li>
                                <li style="padding: var(--space-2) 0; color: var(--text-medium); font-size: var(--text-sm);">
                                    <kbd style="background: var(--bg-elevated); padding: 4px 8px; border-radius: 4px; font-family: monospace;">Alt + P</kbd> - Ir para Perfil
                                </li>
                                <li style="padding: var(--space-2) 0; color: var(--text-medium); font-size: var(--text-sm);">
                                    <kbd style="background: var(--bg-elevated); padding: 4px 8px; border-radius: 4px; font-family: monospace;">Alt + C</kbd> - Ir para Configurações
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab: Conta -->
        <div class="config-tab-content" data-tab-content="conta">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Configurações da Conta</h2>
                    <p style="font-size: var(--text-sm); color: var(--text-medium); margin-top: 4px;">
                        Gerencie sua conta e segurança
                    </p>
                </div>
                <div class="card-content">
                    <!-- Alterar Senha -->
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-lock"></i>
                            Alterar Senha
                        </h3>
                        
                        <div class="form-group">
                            <label class="form-label">Senha Atual</label>
                            <input type="password" id="senha_atual" class="form-input" placeholder="Digite sua senha atual">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Nova Senha</label>
                            <input type="password" id="senha_nova" class="form-input" placeholder="Digite a nova senha">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Confirmar Nova Senha</label>
                            <input type="password" id="senha_confirmar" class="form-input" placeholder="Confirme a nova senha">
                        </div>

                        <button type="button" class="btn btn-primary" id="btnAlterarSenha">
                            <i class="fas fa-key"></i>
                            Alterar Senha
                        </button>
                    </div>

                    <!-- Segurança -->
                    <div class="config-section">
                        <h3 class="config-section-title">
                            <i class="fas fa-shield-alt"></i>
                            Segurança Avançada
                        </h3>
                        
                        <div class="config-item">
                            <div class="config-item-info">
                                <label class="config-label">Autenticação em Dois Fatores</label>
                                <p class="config-description">Adicione uma camada extra de segurança à sua conta</p>
                            </div>
                            <div class="switch">
                                <input type="checkbox" id="autenticacao_dois_fatores" name="autenticacao_dois_fatores">
                                <span class="slider"></span>
                            </div>
                        </div>

                        <div class="config-item-full" style="margin-top: var(--space-4);">
                            <button type="button" class="btn btn-outline">
                                <i class="fas fa-history"></i>
                                Ver Histórico de Acesso
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Botões de Ação -->
        <div class="config-actions">
            <button type="button" class="btn btn-secondary" id="btnCancelar">
                Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
                <i class="fas fa-save"></i>
                Salvar Configurações
            </button>
        </div>
    </form>
</section>


        </main>
    </div>

    <!-- Modal de Confirmação de Logout -->
<div id="modal-logout" class="modal-overlay" style="display: none;">
    <div class="modal-logout-content">
        <div class="modal-logout-icon">
            <i class="fas fa-sign-out-alt"></i>
        </div>
        <h2 class="modal-logout-title">Deseja sair?</h2>
        <p class="modal-logout-message">Tem certeza que deseja encerrar sua sessão?</p>
        <div class="modal-logout-buttons">
            <button class="btn btn-secondary" onclick="fecharModalLogout()">
            Cancelar
            </button>
            <button class="btn btn-primary" onclick="confirmarLogout()">
            Confirmar Saída
            </button>
        </div>
    </div>
</div>


<script>

        // ==================================
        // VARIÁVEIS GLOBAIS
        // ==================================
        let responsavelData = null;
        let neurodivergentesCache = [];
        let neurodivergenteSelecionadoIndex = 0;

        // ==================================
        // ONBOARDING SYSTEM
        // ==================================
        // ========== ONBOARDING SYSTEM ==========
function initOnboarding() {
    const modal = document.getElementById('onboardingModal');
    const steps = document.querySelectorAll('.onboarding-step');
    const progressBar = document.getElementById('onboardingProgress');
    const currentStepText = document.getElementById('currentStep');
    const nextBtn = document.getElementById('nextStep');
    const prevBtn = document.getElementById('prevStep');
    const finishBtn = document.getElementById('finishOnboarding');
    const closeBtn = document.getElementById('closeOnboarding');
    const acceptTerms = document.getElementById('acceptTerms');
    
    let currentStep = 1;
    const totalSteps = 4;
// Controlar estado visual do botão baseado no checkbox
acceptTerms.addEventListener('change', function() {
    if (this.checked) {
        finishBtn.classList.remove('btn-disabled');
        finishBtn.disabled = false;
    } else {
        finishBtn.classList.add('btn-disabled');
        finishBtn.disabled = true;
    }
});

// Inicializar botão como desabilitado
finishBtn.classList.add('btn-disabled');
finishBtn.disabled = true;

    // Verificar se já viu o onboarding
    const hasSeenOnboarding = localStorage.getItem('hasSeenOnboardingResponsavel');
    
    if (!hasSeenOnboarding) {
        setTimeout(() => {
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
        }, 500);
    }

    function updateProgress() {
        const progress = (currentStep / totalSteps) * 100;
        progressBar.style.width = progress + '%';
        currentStepText.textContent = currentStep;
    }

    function showStep(stepNumber) {
        steps.forEach(step => step.classList.remove('active'));
        const activeStep = document.querySelector(`[data-step="${stepNumber}"]`);
        if (activeStep) {
            activeStep.classList.add('active');
        }

        prevBtn.style.visibility = stepNumber === 1 ? 'hidden' : 'visible';

        if (stepNumber === totalSteps) {
            nextBtn.style.display = 'none';
            finishBtn.style.display = 'inline-flex';
            // Verificar estado do checkbox ao mostrar o botão
// Verificar estado do checkbox ao mostrar o botão
if (!acceptTerms.checked) {
    finishBtn.classList.add('btn-disabled');
    finishBtn.disabled = true;
} else {
    finishBtn.classList.remove('btn-disabled');
    finishBtn.disabled = false;
}


        } else {
            nextBtn.style.display = 'inline-flex';
            finishBtn.style.display = 'none';
        }

        updateProgress();
    }

    // Controlar estado visual do botão baseado no checkbox
acceptTerms.addEventListener('change', function() {
    if (this.checked) {
        finishBtn.classList.remove('btn-disabled');
        finishBtn.disabled = false;
    } else {
        finishBtn.classList.add('btn-disabled');
        finishBtn.disabled = true;
    }
});

// Inicializar botão como desabilitado
finishBtn.classList.add('btn-disabled');
finishBtn.disabled = true;


    nextBtn.addEventListener('click', function() {
        if (currentStep < totalSteps) {
            currentStep++;
            showStep(currentStep);
        }
    });

    prevBtn.addEventListener('click', function() {
        if (currentStep > 1) {
            currentStep--;
            showStep(currentStep);
        }
    });

    finishBtn.addEventListener('click', function() {
        if (!acceptTerms.checked) {
            alert('Por favor, aceite os Termos de Uso e Política de Privacidade para continuar.');
            return;
        }

        localStorage.setItem('hasSeenOnboardingResponsavel', 'true');
        closeOnboardingModal();
        alert('Bem-vindo(a) ao Mentes Brilhantes!');
    });

    closeBtn.addEventListener('click', function() {
        if (confirm('Deseja fechar o tutorial?')) {
            closeOnboardingModal();
        }
    });

    function closeOnboardingModal() {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }

    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            closeBtn.click();
        }
    });

    // Inicializar
    showStep(currentStep);
}

// Função global para reiniciar onboarding (útil para testes)
window.restartOnboarding = function() {
    localStorage.removeItem('hasSeenOnboardingResponsavel');
    location.reload();
};


        // ==================================
        // VERIFICAR SESSÃO E CARREGAR DADOS DO HEADER
        // ==================================
        function verificarSessao() {
            // ... seu código de verificarSessao aqui ...
        }

        // ==================================
        // CARREGAR DADOS DOS NEURODIVERGENTES
        // ==================================
        function carregarNeurodivergentes() {
            // ... seu código de carregarNeurodivergentes aqui ...
        }

        // ... todas as outras funções (mostrarSeletorNeurodivergentes, exibirDadosNeurodivergente, etc.) ...

        // ==================================
        // INICIALIZAÇÃO PRINCIPAL
        // ==================================
        document.addEventListener('DOMContentLoaded', function() {
            // Inicializar o onboarding
            initOnboarding();
            
            
            // Inicializações
            verificarSessao();
            carregarDadosPerfil();
            
            // Aplicar máscara ao celular
            $('#perfil-celular').mask('(00) 00000-0000');
        });
 
// ============================================
// VERIFICAR SESSÃO E CARREGAR DADOS DO HEADER
// ============================================
function verificarSessao() {
    fetch('../php/verificar_sessao_responsavel.php')
        .then(response => response.json())
        .then(data => {
            console.log('Verificação de sessão:', data);
            
            if (!data.logado) {
                alert('Sessão expirada. Faça login novamente.');
                window.location.href = '../html/login.html';
                return;
            }
            
            if (data.success) {
                const dados = data.dados;
                
                // Atualizar nome no header
                const nomeHeader = document.getElementById('header-nome');
                if (nomeHeader) {
                    nomeHeader.textContent = dados.primeiro_nome;
                }
                
                // Atualizar iniciais/foto do avatar no header
                const avatarHeader = document.getElementById('header-avatar');
                if (avatarHeader) {
                    if (dados.perfil && dados.perfil.trim() !== '') {
                        avatarHeader.style.backgroundImage = `url('../${dados.perfil}')`;
                        avatarHeader.style.backgroundSize = 'cover';
                        avatarHeader.style.backgroundPosition = 'center';
                        avatarHeader.textContent = '';
                    } else {
                        avatarHeader.textContent = dados.iniciais;
                        avatarHeader.style.backgroundImage = 'none';
                    }
                }
                
                
            }
        })
        .catch(error => {
            console.error('Erro ao verificar sessão:', error);
            alert('Erro ao verificar sessão. Redirecionando para login...');
            setTimeout(() => {
                window.location.href = '../html/login.html';
            }, 2000);
        });
}

// ============================================
// FUNÇÃO DE LOGOUT
// ============================================
function fazerLogout() {
    // Mostrar modal ao invés de confirm
    document.getElementById('modal-logout').style.display = 'flex';
}

function fecharModalLogout() {
    document.getElementById('modal-logout').style.display = 'none';
}

function confirmarLogout() {
    window.location.href = "../php/logout_responsavel.php";
}


// ============================================
// CARREGAR DADOS DO PERFIL DO RESPONSÁVEL
// ============================================
function carregarDadosPerfil() {
    fetch('../php/buscar_dados_responsavel.php')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const dados = data.dados;
                
                // Preencher campos pessoais
                document.getElementById('perfil-nome').value = dados.nome || '';
                document.getElementById('perfil-email').value = dados.email || '';
                document.getElementById('perfil-celular').value = dados.celular_formatado || '';
                document.getElementById('perfil-cpf').value = dados.cpf_formatado || '';
                document.getElementById('perfil-data-nascimento').value = dados.data_nascimento_formatada || '';
                
                // Preencher endereço
                document.getElementById('perfil-rua').value = dados.rua || '';
                document.getElementById('perfil-numero').value = dados.numero || '';
                document.getElementById('perfil-complemento').value = dados.complemento || '';
                document.getElementById('perfil-bairro').value = dados.bairro || '';
                document.getElementById('perfil-cidade').value = dados.cidade || '';
                document.getElementById('perfil-cep').value = dados.cep_formatado || '';
                
                // Atualizar foto/avatar
                const iniciais = dados.iniciais || 'M';
                const perfilIniciais = document.getElementById('perfil-iniciais');
                const perfilFotoImg = document.getElementById('perfil-foto-img');
                
                if (perfilIniciais) {
                    perfilIniciais.textContent = iniciais;
                }
                
                if (dados.perfil && dados.perfil.trim() !== '' && perfilFotoImg) {
                    perfilFotoImg.src = '../' + dados.perfil + '?t=' + new Date().getTime();
                    perfilFotoImg.style.display = 'block';
                    if (perfilIniciais) {
                        perfilIniciais.style.display = 'none';
                    }
                } else if (perfilFotoImg) {
                    perfilFotoImg.style.display = 'none';
                    if (perfilIniciais) {
                        perfilIniciais.style.display = 'flex';
                    }
                }
            } else {
                console.error('Erro ao carregar dados:', data.message);
                alert('Erro ao carregar dados do perfil: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar dados do perfil. Verifique sua conexão.');
        });
}

// ============================================
// SALVAR ALTERAÇÕES (CELULAR)
// ============================================
function salvarAlteracoes() {
    const celular = document.getElementById('perfil-celular').value;
    
    if (!celular) {
        alert('Por favor, preencha o celular');
        return;
    }
    
    const celularLimpo = celular.replace(/\D/g, '');
    if (celularLimpo.length != 11) {
        alert('Celular inválido! Digite no formato (11) 99999-9999');
        return;
    }
    
    const formData = new FormData();
    formData.append('celular', celular);
    
    const btnSalvar = event.target;
    const textoOriginal = btnSalvar.innerHTML;
    btnSalvar.disabled = true;
    btnSalvar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Salvando...';
    
    fetch('../php/atualizar_perfil_responsavel.php', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            carregarDadosPerfil();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao salvar alterações. Tente novamente.');
    })
    .finally(() => {
        btnSalvar.disabled = false;
        btnSalvar.innerHTML = textoOriginal;
    });
}

// ============================================
// CANCELAR EDIÇÃO
// ============================================
function cancelarEdicao() {
    if (confirm('Deseja descartar as alterações?')) {
        carregarDadosPerfil();
    }
}

// UPLOAD DE FOTO DO RESPONSÁVEL
const inputFoto = document.getElementById('input-foto-perfil');
if (inputFoto) {
    inputFoto.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (!file) {
            return;
        }
        
        // Validar tamanho (5MB)
        if (file.size > 5242880) {
            alert('Arquivo muito grande! Tamanho máximo: 5MB');
            inputFoto.value = '';
            return;
        }
        
        // Validar tipo
        const tiposPermitidos = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
        if (!tiposPermitidos.includes(file.type)) {
            alert('Tipo de arquivo inválido! Use apenas JPG, PNG ou GIF');
            inputFoto.value = '';
            return;
        }
        
        // Criar FormData
        const formData = new FormData();
        formData.append('perfil', file);
        
        // Mostrar loading no avatar
        const avatarDisplay = document.getElementById('perfil-avatar-display');
        const perfilFotoImg = document.getElementById('perfil-foto-img');
        const perfilIniciais = document.getElementById('perfil-iniciais');
        
        const originalContent = avatarDisplay ? avatarDisplay.innerHTML : '';
        
        if (avatarDisplay) {
            avatarDisplay.innerHTML = '<i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: white;"></i>';
        }
        
        // Fazer upload
        fetch('../php/atualizar_perfil_responsavel.php', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            console.log('Resposta do servidor:', data); // DEBUG
            
            if (data.success) {
                alert(data.message);
                
                // Recarregar dados do perfil
                if (typeof carregarDadosPerfil === 'function') {
                    carregarDadosPerfil();
                }
                
                // Recarregar dados do header
                if (typeof verificarSessao === 'function') {
                    verificarSessao();
                }
                
                // Atualizar imagem imediatamente
                if (data.foto_url && perfilFotoImg) {
                    perfilFotoImg.src = '../' + data.foto_url + '?t=' + new Date().getTime();
                    perfilFotoImg.style.display = 'block';
                    
                    if (perfilIniciais) {
                        perfilIniciais.style.display = 'none';
                    }
                }
            } else {
                alert('Erro: ' + data.message);
                if (avatarDisplay) {
                    avatarDisplay.innerHTML = originalContent;
                }
            }
            
            // Limpar input
            inputFoto.value = '';
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao fazer upload da foto. Tente novamente.');
            if (avatarDisplay) {
                avatarDisplay.innerHTML = originalContent;
            }
            inputFoto.value = '';
        });
    });
}

// ============================================
// CARREGAR DADOS DOS NEURODIVERGENTES
// ============================================
function carregarNeurodivergentes() {
    const loadingElement = document.getElementById('loading-neurodivergentes');
    const containerElement = document.getElementById('container-neurodivergentes');
    const infoPessoaisContent = document.getElementById('info-pessoais-content');

    // Mostrar loading
    if (loadingElement) loadingElement.style.display = 'block';
    if (containerElement) containerElement.style.display = 'none';

    console.log('Iniciando carregamento de neurodivergentes...'); // DEBUG

    fetch('../php/buscar_dados_neurodivergente.php')
        .then(response => {
            console.log('Status da resposta:', response.status); // DEBUG
            return response.json();
        })
        .then(data => {
            console.log('Dados recebidos:', data); // DEBUG

            // Esconder loading
            if (loadingElement) loadingElement.style.display = 'none';
            if (containerElement) containerElement.style.display = 'block';

            if (data.success && data.dados && data.dados.length > 0) {
                neurodivergentesCache = data.dados;

                // Se há mais de um neurodivergente, mostrar seletor
                if (data.dados.length > 1) {
                    mostrarSeletorNeurodivergentes(data.dados);
                }

                // Exibir o primeiro ou único neurodivergente
                exibirDadosNeurodivergente(0);
            } else {
                // Nenhum neurodivergente cadastrado
                console.log('Nenhum neurodivergente encontrado'); // DEBUG
                if (infoPessoaisContent) {
                    infoPessoaisContent.innerHTML = `
                        <div style="text-align: center; padding: 40px 20px; color: #666;">
                            <i class="fas fa-user-plus" style="font-size: 48px; margin-bottom: 20px; color: #667eea;"></i>
                            <p style="font-size: 18px; margin-bottom: 10px;">Nenhum neurodivergente cadastrado</p>
                            <p style="font-size: 14px; color: #999; margin-bottom: 20px;">Clique em "Adicionar Neurodivergente" para cadastrar</p>
                            <a href="../html/cadastroneuro.html" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Cadastrar Agora
                            </a>
                        </div>
                    `;

                    // Esconder botões de ação
                    const botoesAcao = document.getElementById('botoes-acao-neurodivergente');
                    if (botoesAcao) botoesAcao.style.display = 'none';
                }
            }
        })
        .catch(error => {
            console.error('Erro ao carregar neurodivergentes:', error); // DEBUG
            
            if (loadingElement) loadingElement.style.display = 'none';
            if (containerElement) containerElement.style.display = 'block';
            
            if (infoPessoaisContent) {
                infoPessoaisContent.innerHTML = `
                    <div style="text-align: center; padding: 40px 20px; color: #dc3545;">
                        <i class="fas fa-exclamation-triangle" style="font-size: 48px; margin-bottom: 20px;"></i>
                        <p style="font-size: 18px; margin-bottom: 10px;">Erro ao carregar dados</p>
                        <p style="font-size: 14px; color: #999;">Erro: ${error.message}</p>
                        <p style="font-size: 14px; color: #999; margin-top: 10px;">Tente novamente mais tarde</p>
                    </div>
                `;
            }
        });
}

// EXIBIR DADOS DO NEURODIVERGENTE
function exibirDadosNeurodivergente(index) {
    if (!neurodivergentesCache || neurodivergentesCache.length === 0) return;

    const neuro = neurodivergentesCache[index];
    console.log('Exibindo neurodivergente:', neuro); // DEBUG

    const infoPessoaisContent = document.getElementById('info-pessoais-content');
    const botoesAcao = document.getElementById('botoes-acao-neurodivergente');

    // Mostrar botões de ação
    if (botoesAcao) {
        botoesAcao.style.display = 'flex';
        botoesAcao.style.gap = '0.5rem';
    }

    // Preencher informações pessoais
    if (infoPessoaisContent) {
        infoPessoaisContent.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem;">
                <div class="form-group">
                    <label class="form-label">Nome Completo</label>
                    <input type="text" class="form-input" value="${neuro.nome}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Email</label>
                    <input type="email" class="form-input" value="${neuro.email}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Data de Nascimento</label>
                    <input type="text" class="form-input" value="${neuro.data_nascimento_formatada}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Idade</label>
                    <input type="text" class="form-input" value="${neuro.idade > 0 ? neuro.idade + ' anos' : 'Não informado'}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">RG</label>
                    <input type="text" class="form-input" value="${neuro.rg_formatado}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">CPF</label>
                    <input type="text" class="form-input" value="${neuro.cpf_formatado}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Sexo</label>
                    <input type="text" class="form-input" value="${neuro.sexo}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Celular</label>
                    <input type="text" class="form-input" value="${neuro.celular_formatado}" disabled>
                </div>
            </div>
        `;
    }

    // Preencher endereco
    const neuroRua = document.getElementById('neuro-rua');
    const neuroNumero = document.getElementById('neuro-numero');
    const neuroComplemento = document.getElementById('neuro-complemento');
    const neuroBairro = document.getElementById('neuro-bairro');
    const neuroCidade = document.getElementById('neuro-cidade');
    const neuroCep = document.getElementById('neuro-cep');

    if (neuroRua) neuroRua.value = neuro.rua;
    if (neuroNumero) neuroNumero.value = neuro.numero;
    if (neuroComplemento) neuroComplemento.value = neuro.complemento || '';
    if (neuroBairro) neuroBairro.value = neuro.bairro;
    if (neuroCidade) neuroCidade.value = neuro.cidade;
    if (neuroCep) neuroCep.value = neuro.cep_formatado;

    // Atualizar avatar e nome
    const neuroIniciais = document.getElementById('neuro-iniciais');
    const neuroNomeDisplay = document.getElementById('neuro-nome-display');
    const neuroAvatarDisplay = document.getElementById('neuro-avatar-display');

    console.log('Caminho da foto:', neuro.perfil); // DEBUG

    // Atualizar nome
    if (neuroNomeDisplay) {
        neuroNomeDisplay.textContent = neuro.nome || 'Nome não disponível';
    }

    // Atualizar avatar com foto ou iniciais
    if (neuroAvatarDisplay) {
        // Limpar avatar anterior
        const existingImg = neuroAvatarDisplay.querySelector('img.profile-photo');
        if (existingImg) existingImg.remove();

        // Verificar se existe foto
        if (neuro.perfil && neuro.perfil.trim() !== '') {
            console.log('Foto encontrada! Carregando:', neuro.perfil); // DEBUG

            // Criar elemento de imagem
            const imgElement = document.createElement('img');
            imgElement.className = 'profile-photo';
            imgElement.style.cssText = 'width: 100%; height: 100%; object-fit: cover; border-radius: 50%; position: absolute; top: 0; left: 0; z-index: 2;';
            
            // Definir src da imagem
            imgElement.src = '../' + neuro.perfil + '?t=' + new Date().getTime();
            imgElement.alt = 'Foto de ' + neuro.nome;

            // Adicionar ao container
            neuroAvatarDisplay.appendChild(imgElement);

            // Esconder iniciais quando foto carregar
            imgElement.onload = function() {
                console.log('Foto carregada com sucesso!'); // DEBUG
                if (neuroIniciais) {
                    neuroIniciais.style.display = 'none';
                    neuroIniciais.style.opacity = '0';
                }
            };

            // Mostrar iniciais se erro ao carregar
            imgElement.onerror = function() {
                console.error('Erro ao carregar foto:', neuro.perfil); // DEBUG
                imgElement.remove();
                if (neuroIniciais) {
                    neuroIniciais.style.display = 'flex';
                    neuroIniciais.style.opacity = '1';
                    neuroIniciais.textContent = neuro.iniciais || '?';
                }
            };
        } else {
            console.log('Sem foto cadastrada, mostrando iniciais'); // DEBUG
            // Mostrar iniciais se não houver foto
            if (neuroIniciais) {
                neuroIniciais.style.display = 'flex';
                neuroIniciais.style.opacity = '1';
                neuroIniciais.textContent = neuro.iniciais || '?';
            }
        }
    }

    // Preencher contatos de emergência
    const nomeResponsavel = document.getElementById('nome-responsavel');
    const celularResponsavel = document.getElementById('celular-responsavel');

    if (nomeResponsavel) nomeResponsavel.textContent = neuro.nome_responsavel || 'Não informado';
    if (celularResponsavel) celularResponsavel.textContent = neuro.celular_responsavel_formatado || 'Não informado';
}


// ============================================
// EDITAR NEURODIVERGENTE SELECIONADO
// ============================================
function editarNeurodivergenteSelecionado() {
    if (!neurodivergentesCache || neurodivergentesCache.length === 0) {
        alert('Nenhum neurodivergente selecionado');
        return;
    }
    
    const neuro = neurodivergentesCache[neurodivergenteSelecionadoIndex];
    
    // Preencher o formulário do modal
    document.getElementById('edit_id_neuro').value = neuro.id;
    document.getElementById('edit_nome').value = neuro.nome || '';
    document.getElementById('edit_email').value = neuro.email || '';
    document.getElementById('edit_data_nascimento').value = neuro.data_nascimento_formatada || '';
    document.getElementById('edit_rg').value = neuro.rg || '';
    document.getElementById('edit_cpf').value = neuro.cpf || '';
    document.getElementById('edit_sexo').value = neuro.sexo || '';
    document.getElementById('edit_celular').value = neuro.celular_formatado || '';
    document.getElementById('edit_cep').value = neuro.cep_formatado || '';
    document.getElementById('edit_rua').value = neuro.rua || '';
    document.getElementById('edit_numero').value = neuro.numero || '';
    document.getElementById('edit_complemento').value = neuro.complemento || '';
    document.getElementById('edit_bairro').value = neuro.bairro || '';
    document.getElementById('edit_cidade').value = neuro.cidade || '';
    
    // Aplicar máscaras
    aplicarMascarasModal();
    
    // Abrir modal
    $('#modalEditarNeuro').modal('show');
}

// ============================================
// APLICAR MÁSCARAS NO MODAL
// ============================================
function aplicarMascarasModal() {
    $('#edit_data_nascimento').mask('00/00/0000');
    $('#edit_rg').mask('00.000.000-0');
    $('#edit_cpf').mask('000.000.000-00');
    $('#edit_celular').mask('(00) 00000-0000');
    $('#edit_cep').mask('00000-000');
}

// ============================================
// SALVAR EDIÇÃO DO NEURODIVERGENTE
// ============================================
function salvarEdicaoNeurodivergente(event) {
    event.preventDefault();
    
    const form = document.getElementById('formEditarNeuro');
    const formData = new FormData(form);
    
    const btnSubmit = form.querySelector('button[type="submit"]');
    const textoOriginal = btnSubmit.innerHTML;
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Salvando...';
    
    fetch('../php/atualizar_perfil_neurodivergente.php', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            $('#modalEditarNeuro').modal('hide');
            carregarNeurodivergentes();
        } else {
            alert('Erro: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao salvar alterações. Tente novamente.');
    })
    .finally(() => {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = textoOriginal;
    });
}

// ============================================
// EXCLUIR NEURODIVERGENTE SELECIONADO
// ============================================
function excluirNeurodivergenteSelecionado() {
    if (!neurodivergentesCache || neurodivergentesCache.length === 0) {
        alert('Nenhum neurodivergente selecionado');
        return;
    }
    
    const neuro = neurodivergentesCache[neurodivergenteSelecionadoIndex];
    
    if (!confirm(`Tem certeza que deseja excluir o perfil de ${neuro.nome}?\n\nEsta ação não pode ser desfeita!`)) {
        return;
    }
    
    const formData = new FormData();
    formData.append('id_neuro', neuro.id);
    
    fetch('../php/excluir_neurodivergente.php', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            carregarNeurodivergentes();
        } else {
            alert('Erro: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao excluir perfil. Tente novamente.');
    });
}

// ============================================
// FUNÇÕES DO PERFIL SENSORIAL (PLACEHOLDER)
// ============================================
function shareProfile() {
    alert('Função de compartilhamento em desenvolvimento');
}

function addItem(tipo) {
    const texto = prompt(`Adicionar novo item em ${tipo}:`);
    if (texto && texto.trim() !== '') {
        alert(`Item "${texto}" adicionado! (Função em desenvolvimento)`);
    }
}

function editItemInline(tipo, id) {
    alert(`Editar item ${id} de ${tipo} (Função em desenvolvimento)`);
}

function deleteItemInline(tipo, id) {
    if (confirm('Deseja excluir este item?')) {
        alert(`Item ${id} de ${tipo} excluído! (Função em desenvolvimento)`);
    }
}

function editDevelopment(tipo) {
    alert(`Editar marco de desenvolvimento: ${tipo} (Função em desenvolvimento)`);
}

// Atualizar a contagem de etapas
const totalSteps = 4; // Mudou de 5 para 4

// Atualizar a função de progresso
function updateProgress() {
    const progress = (currentStep / totalSteps) * 100;
    document.getElementById('onboardingProgress').style.width = progress + '%';
    document.getElementById('currentStep').textContent = currentStep;
}

// Na última etapa, verificar apenas os termos
document.getElementById('nextStep').addEventListener('click', function() {
    if (currentStep === 4) {
        const acceptTerms = document.getElementById('acceptTerms');
        
        if (!acceptTerms.checked) {
            alert('Por favor, aceite os Termos de Uso e Política de Privacidade para continuar.');
            return;
        }
    }
    
    // Continuar com a navegação normal
    if (currentStep < totalSteps) {
        currentStep++;
        showStep(currentStep);
    }
});
</script>

<script>
   // ========================================
// SISTEMA DE NAVEGAÇÃO - VERSÃO FINAL
// ========================================

document.addEventListener('DOMContentLoaded', function () {
    console.log('Sistema carregado com sucesso');
    
    // Inicializar o onboarding apenas se for primeira vez
    const hasSeenOnboarding = localStorage.getItem('hasSeenOnboardingResponsavel');
    if (!hasSeenOnboarding) {
        initOnboarding();
    }
    
    const navItems = document.querySelectorAll('.nav-item');
    const sections = document.querySelectorAll('.section');

    // Função centralizada para trocar de seção
    function mudarSeccao(sectionId) {
        console.log('Navegando para seção:', sectionId);
        
        // Remover 'active' de TODOS os itens de navegação e seções
        navItems.forEach(nav => nav.classList.remove('active'));
        sections.forEach(sec => sec.classList.remove('active'));

        // Ativar item de menu correspondente
        const navItem = document.querySelector(`[data-section="${sectionId}"]`);
        if (navItem) {
            navItem.classList.add('active');
        }

        // Exibir a seção correspondente
        const targetSection = document.getElementById(sectionId);
        if (targetSection) {
            targetSection.classList.add('active');

            // Carregar dados específicos conforme a seção
            if (sectionId === 'perfil-autista1') {
                if (typeof carregarNeurodivergentes === 'function') {
                    try {
                        carregarNeurodivergentes();
                    } catch (error) {
                        console.error('Erro ao carregar neurodivergentes:', error);
                    }
                }
            }

            if (sectionId === 'perfil') {
                if (typeof carregarDadosPerfil === 'function') {
                    try {
                        carregarDadosPerfil();
                    } catch (error) {
                        console.error('Erro ao carregar perfil:', error);
                    }
                }
            }
        }
    }

    // Adicionar listeners aos itens de navegação
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const sectionId = this.getAttribute('data-section');
            if (sectionId) {
                mudarSeccao(sectionId);
            }
        });
    });

    // Detectar parâmetro de seção na URL
    const urlParams = new URLSearchParams(window.location.search);
    const section = urlParams.get('section');

    if (section) {
        mudarSeccao(section);
        // Limpar URL discretamente
        window.history.replaceState({}, document.title, window.location.pathname);
    } else {
        // Se não houver parâmetro, mostrar dashboard por padrão
        mudarSeccao('dashboard');
    }

    // Inicializações gerais
    verificarSessao();
    carregarDadosPerfil();
    
    // Aplicar máscara ao celular
    const celularInput = document.getElementById('perfil-celular');
    if (celularInput && typeof $.mask === 'function') {
        $(celularInput).mask('(00) 00000-0000');
    }
});

// Função global para navegar programaticamente
function navegarPara(sectionId) {
    const navItem = document.querySelector(`[data-section="${sectionId}"]`);
    if (navItem) {
        navItem.click();
    }
}

// ============================================
// FUNÇÕES RELACIONADAS A NEURODIVERGENTES
// ============================================

function mostrarSeletorNeurodivergentes(neurodivergentes) {
    const seletor = document.getElementById('seletor-neurodivergentes');
    const selectElement = document.getElementById('select-neurodivergente');
    
    if (!seletor || !selectElement) {
        console.error('Elementos do seletor não encontrados');
        return;
    }
    
    // Limpar opções antigas
    selectElement.innerHTML = '';
    
    // Adicionar opções
    neurodivergentes.forEach((neuro, index) => {
        const option = document.createElement('option');
        option.value = index;
        option.textContent = neuro.nome;
        if (index === 0) option.selected = true;
        selectElement.appendChild(option);
    });
    
    // Mostrar seletor
    seletor.style.display = 'block';
    
    // Remover listener antigo (se existir) e adicionar novo
    selectElement.removeEventListener('change', handleNeurodivergentChange);
    selectElement.addEventListener('change', handleNeurodivergentChange);
}

function handleNeurodivergentChange(event) {
    const index = parseInt(event.target.value);
    neurodivergenteSelecionadoIndex = index;
    console.log('Neurodivergente selecionado (índice):', index);
    exibirDadosNeurodivergente(index);
}

function exibirDadosNeurodivergente(index) {
    if (!neurodivergentesCache || neurodivergentesCache.length === 0) return;
    
    const neuro = neurodivergentesCache[index];
    console.log('Exibindo neurodivergente:', neuro); // DEBUG
    
    const infoPessoaisContent = document.getElementById('info-pessoais-content');
    const botoesAcao = document.getElementById('botoes-acao-neurodivergente');
    
    // Mostrar botões de ação
    if (botoesAcao) {
        botoesAcao.style.display = 'flex';
        botoesAcao.style.gap = '0.5rem';
    }
    
    // Preencher informações pessoais
    if (infoPessoaisContent) {
        infoPessoaisContent.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem;">
                <div class="form-group">
                    <label class="form-label">Nome Completo</label>
                    <input type="text" class="form-input" value="${neuro.nome || ''}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Email</label>
                    <input type="email" class="form-input" value="${neuro.email || ''}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Data de Nascimento</label>
                    <input type="text" class="form-input" value="${neuro.data_nascimento_formatada || ''}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Idade</label>
                    <input type="text" class="form-input" value="${neuro.idade > 0 ? neuro.idade + ' anos' : 'Não informado'}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">RG</label>
                    <input type="text" class="form-input" value="${neuro.rg_formatado || ''}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">CPF</label>
                    <input type="text" class="form-input" value="${neuro.cpf_formatado || ''}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Sexo</label>
                    <input type="text" class="form-input" value="${neuro.sexo || ''}" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Celular</label>
                    <input type="text" class="form-input" value="${neuro.celular_formatado || ''}" disabled>
                </div>
            </div>
        `;
    }
    
    // Preencher endereço
    const neuroRua = document.getElementById('neuro-rua');
    const neuroNumero = document.getElementById('neuro-numero');
    const neuroComplemento = document.getElementById('neuro-complemento');
    const neuroBairro = document.getElementById('neuro-bairro');
    const neuroCidade = document.getElementById('neuro-cidade');
    const neuroCep = document.getElementById('neuro-cep');
    
    if (neuroRua) neuroRua.value = neuro.rua || '';
    if (neuroNumero) neuroNumero.value = neuro.numero || '';
    if (neuroComplemento) neuroComplemento.value = neuro.complemento || '';
    if (neuroBairro) neuroBairro.value = neuro.bairro || '';
    if (neuroCidade) neuroCidade.value = neuro.cidade || '';
    if (neuroCep) neuroCep.value = neuro.cep_formatado || '';
    
    // Atualizar avatar e nome
    const neuroIniciais = document.getElementById('neuro-iniciais');
    const neuroNomeDisplay = document.getElementById('neuro-nome-display');
    const neuroAvatarDisplay = document.getElementById('neuro-avatar-display');
    
    console.log('Caminho da foto:', neuro.perfil); // DEBUG
    
    // Atualizar nome
    if (neuroNomeDisplay) {
        neuroNomeDisplay.textContent = neuro.nome || 'Nome não disponível';
    }
    
    // Atualizar avatar com foto ou iniciais
    if (neuroAvatarDisplay) {
        // Limpar avatar anterior
        const existingImg = neuroAvatarDisplay.querySelector('img.profile-photo');
        if (existingImg) existingImg.remove();
        
        // Verificar se existe foto
        if (neuro.perfil && neuro.perfil.trim() !== '') {
            console.log('Foto encontrada! Carregando:', neuro.perfil); // DEBUG
            
            // Criar elemento de imagem
            const imgElement = document.createElement('img');
            imgElement.className = 'profile-photo';
            imgElement.style.cssText = 'width: 100%; height: 100%; object-fit: cover; border-radius: 50%; position: absolute; top: 0; left: 0; z-index: 2;';
            
            // Definir src da imagem
            imgElement.src = `../${neuro.perfil}?t=${new Date().getTime()}`;
            imgElement.alt = `Foto de ${neuro.nome}`;
            
            // Adicionar ao container
            neuroAvatarDisplay.appendChild(imgElement);
            
            // Esconder iniciais quando foto carregar
            imgElement.onload = function() {
                console.log('Foto carregada com sucesso!'); // DEBUG
                if (neuroIniciais) {
                    neuroIniciais.style.display = 'none';
                    neuroIniciais.style.opacity = '0';
                }
            };
            
            // Mostrar iniciais se erro ao carregar
            imgElement.onerror = function() {
                console.error('Erro ao carregar foto:', neuro.perfil); // DEBUG
                imgElement.remove();
                if (neuroIniciais) {
                    neuroIniciais.style.display = 'flex';
                    neuroIniciais.style.opacity = '1';
                    neuroIniciais.textContent = neuro.iniciais || '?';
                }
            };
        } else {
            console.log('Sem foto cadastrada, mostrando iniciais'); // DEBUG
            // Mostrar iniciais se não houver foto
            if (neuroIniciais) {
                neuroIniciais.style.display = 'flex';
                neuroIniciais.style.opacity = '1';
                neuroIniciais.textContent = neuro.iniciais || '?';
            }
        }
    }
    
    // Preencher contatos de emergência
    const nomeResponsavel = document.getElementById('nome-responsavel');
    const celularResponsavel = document.getElementById('celular-responsavel');
    
    if (nomeResponsavel) nomeResponsavel.textContent = neuro.nome_responsavel || 'Não informado';
    if (celularResponsavel) celularResponsavel.textContent = neuro.celular_responsavel_formatado || 'Não informado';
}

function editarNeurodivergenteSelecionado() {
    if (!neurodivergentesCache || neurodivergentesCache.length === 0) {
        alert('Nenhum neurodivergente selecionado');
        return;
    }
    
    const neuro = neurodivergentesCache[neurodivergenteSelecionadoIndex];
    
    // Preencher o formulário do modal
    document.getElementById('edit_id_neuro').value = neuro.id;
    document.getElementById('edit_nome').value = neuro.nome || '';
    document.getElementById('edit_email').value = neuro.email || '';
    document.getElementById('edit_data_nascimento').value = neuro.data_nascimento_formatada || '';
    document.getElementById('edit_rg').value = neuro.rg || '';
    document.getElementById('edit_cpf').value = neuro.cpf || '';
    document.getElementById('edit_sexo').value = neuro.sexo || '';
    document.getElementById('edit_celular').value = neuro.celular_formatado || '';
    document.getElementById('edit_cep').value = neuro.cep_formatado || '';
    document.getElementById('edit_rua').value = neuro.rua || '';
    document.getElementById('edit_numero').value = neuro.numero || '';
    document.getElementById('edit_complemento').value = neuro.complemento || '';
    document.getElementById('edit_bairro').value = neuro.bairro || '';
    document.getElementById('edit_cidade').value = neuro.cidade || '';
    
    // Aplicar máscaras
    aplicarMascarasModal();
    
    // Abrir modal
    $('#modalEditarNeuro').modal('show');
}

function excluirNeurodivergenteSelecionado() {
    if (!neurodivergentesCache || neurodivergentesCache.length === 0) {
        alert('Nenhum neurodivergente selecionado');
        return;
    }
    
    const neuro = neurodivergentesCache[neurodivergenteSelecionadoIndex];
    
    if (!confirm(`Tem certeza que deseja excluir o perfil de ${neuro.nome}?\n\nEsta ação não pode ser desfeita!`)) {
        return;
    }
    
    const formData = new FormData();
    formData.append('id_neuro', neuro.id);
    
    fetch('../php/excluir_neurodivergente.php', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            carregarNeurodivergentes();
        } else {
            alert('Erro: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao excluir perfil. Tente novamente.');
    });
}

function carregarNeurodivergentes() {
    const loadingElement = document.getElementById('loading-neurodivergentes');
    const containerElement = document.getElementById('container-neurodivergentes');
    const seletorElement = document.getElementById('seletor-neurodivergentes');
    
    if (loadingElement) {
        loadingElement.style.display = 'flex';
    }
    
    fetch('../php/listar_neurodivergentes.php')
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro HTTP: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('Resposta do servidor:', data);
            
            if (data.success) {
                neurodivergentesCache = data.neurodivergentes;
                console.log('Neurodivergentes em cache:', neurodivergentesCache);
                
                if (neurodivergentesCache.length === 0) {
                    if (seletorElement) {
                        seletorElement.style.display = 'none';
                    }
                    if (containerElement) {
                        containerElement.innerHTML = `
                            <div style="text-align: center; padding: 2rem; color: #999;">
                                <p>Nenhum neurodivergente cadastrado</p>
                                <p style="font-size: 0.9rem; margin-top: 0.5rem;">
                                    Clique em "Adicionar Neurodivergente" para começar
                                </p>
                            </div>
                        `;
                    }
                } else {
                    if (seletorElement) {
                        seletorElement.style.display = 'block';
                    }
                    mostrarSeletorNeurodivergentes(neurodivergentesCache);
                    exibirDadosNeurodivergente(0);
                }
            } else {
                alert('Erro: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Erro ao carregar neurodivergentes:', error);
            alert('Erro ao carregar neurodivergentes. Verifique se o arquivo listar_neurodivergentes.php existe.');
        })
        .finally(() => {
            if (loadingElement) {
                loadingElement.style.display = 'none';
            }
        });
}

// ========================================
// RECURSOS & SUPORTE - FUNCIONALIDADES
// ========================================

// Navegação entre tabs
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        const targetTab = this.dataset.tab;
        
        // Remove active de todos os botões e conteúdos
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        
        // Adiciona active ao botão clicado e conteúdo correspondente
        this.classList.add('active');
        document.getElementById(targetTab).classList.add('active');
    });
});

// ========================================
// RECURSOS & SUPORTE - FUNCIONALIDADES
// ========================================

// Navegação entre tabs
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        const targetTab = this.dataset.tab;
        
        // Remove active de todos os botões e conteúdos
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        
        // Adiciona active ao botão clicado e conteúdo correspondente
        this.classList.add('active');
        document.getElementById(targetTab).classList.add('active');
    });
});

// FAQ - Accordion
document.querySelectorAll('.faq-question').forEach(question => {
    question.addEventListener('click', function() {
        const faqItem = this.closest('.faq-item');
        const isActive = faqItem.classList.contains('active');
        
        // Fecha todos os outros FAQs
        document.querySelectorAll('.faq-item').forEach(item => {
            item.classList.remove('active');
        });
        
        // Se não estava ativo, abre
        if (!isActive) {
            faqItem.classList.add('active');
        }
    });
});

// Animação ao scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Observa todos os cards
document.querySelectorAll('.recurso-item, .video-item, .atividade-card, .canal-item').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(20px)';
    el.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    observer.observe(el);
});
// ========================================
// RECURSOS & SUPORTE - FUNCIONALIDADES
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    
    // Tabs de Navegação
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetTab = button.getAttribute('data-tab');

            // Remove active de todos os botões e conteúdos
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Adiciona active ao botão e conteúdo selecionado
            button.classList.add('active');
            document.getElementById(targetTab).classList.add('active');
        });
    });

    // FAQ - Accordion
    const faqQuestions = document.querySelectorAll('.faq-question-new');

    faqQuestions.forEach(question => {
        question.addEventListener('click', () => {
            const faqItem = question.parentElement;
            const isActive = faqItem.classList.contains('active');

            // Fecha todos os itens
            document.querySelectorAll('.faq-item-new').forEach(item => {
                item.classList.remove('active');
            });

            // Abre o item clicado se não estava ativo
            if (!isActive) {
                faqItem.classList.add('active');
            }
        });
    });
});


    </script>
<script>
// ========================================
// CONFIGURAÇÕES - FUNCIONALIDADE COMPLETA
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    
    // Navegação entre tabs
    const tabButtons = document.querySelectorAll('.config-tab-btn');
    const tabContents = document.querySelectorAll('.config-tab-content');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetTab = this.getAttribute('data-tab');
            
            // Remover active de todos
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            
            // Adicionar active ao selecionado
            this.classList.add('active');
            document.querySelector(`[data-tab-content="${targetTab}"]`).classList.add('active');
        });
    });
    
    // Carregar configurações atuais
    fetch('processar_configuracoes.php', {
        method: 'GET'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.config) {
            const config = data.config;
            
            // Preencher campos com valores salvos
            document.getElementById('notif_email_atividades').checked = config.notif_email_atividades == 1;
            document.getElementById('notif_email_lembretes').checked = config.notif_email_lembretes == 1;
            document.getElementById('notif_email_novidades').checked = config.notif_email_novidades == 1;
            document.getElementById('notif_push_mensagens').checked = config.notif_push_mensagens == 1;
            document.getElementById('notif_push_alertas').checked = config.notif_push_alertas == 1;
            
            document.getElementById('privacidade_perfil').value = config.privacidade_perfil || 'privado';
            document.getElementById('mostrar_email').checked = config.mostrar_email == 1;
            document.getElementById('mostrar_telefone').checked = config.mostrar_telefone == 1;
            
            document.querySelector(`input[name="tema"][value="${config.tema || 'claro'}"]`).checked = true;
            document.getElementById('tamanho_fonte').value = config.tamanho_fonte || 'medio';
            document.getElementById('modo_alto_contraste').checked = config.modo_alto_contraste == 1;
            document.getElementById('animacoes_reduzidas').checked = config.animacoes_reduzidas == 1;
            
            document.getElementById('idioma').value = config.idioma || 'pt-BR';
            document.getElementById('fuso_horario').value = config.fuso_horario || 'America/Sao_Paulo';
            
            document.getElementById('autenticacao_dois_fatores').checked = config.autenticacao_dois_fatores == 1;
        }
    })
    .catch(error => console.error('Erro ao carregar configurações:', error));
    
    // Salvar configurações
    document.getElementById('formConfiguracoes').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        
        fetch('processar_configuracoes.php', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                mostrarNotificacao('Configurações salvas com sucesso!', 'success');
            } else {
                mostrarNotificacao(data.message || 'Erro ao salvar configurações', 'error');
            }
        })
        .catch(error => {
            mostrarNotificacao('Erro ao processar solicitação', 'error');
            console.error('Erro:', error);
        });
    });
    
    // Alterar senha
    document.getElementById('btnAlterarSenha').addEventListener('click', function() {
        const senhaAtual = document.getElementById('senha_atual').value;
        const senhaNova = document.getElementById('senha_nova').value;
        const senhaConfirmar = document.getElementById('senha_confirmar').value;
        
        if (!senhaAtual || !senhaNova || !senhaConfirmar) {
            mostrarNotificacao('Preencha todos os campos de senha', 'warning');
            return;
        }
        
        if (senhaNova !== senhaConfirmar) {
            mostrarNotificacao('As senhas não coincidem', 'error');
            return;
        }
        
        if (senhaNova.length < 6) {
            mostrarNotificacao('A senha deve ter no mínimo 6 caracteres', 'warning');
            return;
        }
        
        const formData = new FormData();
        formData.append('acao', 'alterar_senha');
        formData.append('senha_atual', senhaAtual);
        formData.append('senha_nova', senhaNova);
        formData.append('senha_confirmar', senhaConfirmar);
        
        fetch('processar_configuracoes.php', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                mostrarNotificacao('Senha alterada com sucesso!', 'success');
                document.getElementById('senha_atual').value = '';
                document.getElementById('senha_nova').value = '';
                document.getElementById('senha_confirmar').value = '';
            } else {
                mostrarNotificacao(data.message || 'Erro ao alterar senha', 'error');
            }
        })
        .catch(error => {
            mostrarNotificacao('Erro ao processar solicitação', 'error');
            console.error('Erro:', error);
        });
    });
    
    // Exportar dados
    document.getElementById('btnExportarDados').addEventListener('click', function() {
        const formData = new FormData();
        formData.append('acao', 'exportar_dados');
        
        fetch('processar_configuracoes.php', {
            method: 'POST',
            body: formData
        })
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'meus_dados_mentes_brilhantes.json';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
            mostrarNotificacao('Dados exportados com sucesso!', 'success');
        })
        .catch(error => {
            mostrarNotificacao('Erro ao exportar dados', 'error');
            console.error('Erro:', error);
        });
    });
    
    // Excluir conta (simulado)
    document.getElementById('btnExcluirConta').addEventListener('click', function() {
        if (confirm('Tem certeza que deseja solicitar a exclusão da sua conta? Esta ação não pode ser desfeita.')) {
            mostrarNotificacao('Solicitação de exclusão enviada. Nossa equipe entrará em contato em breve.', 'info');
        }
    });
    
    // Cancelar alterações
    document.getElementById('btnCancelar').addEventListener('click', function() {
        if (confirm('Deseja descartar as alterações não salvas?')) {
            location.reload();
        }
    });
    
    // Função para mostrar notificações
    function mostrarNotificacao(mensagem, tipo = 'info') {
        // Criar elemento de notificação
        const notificacao = document.createElement('div');
        notificacao.className = `notificacao-config notificacao-${tipo}`;
        notificacao.innerHTML = `
            <div style="display: flex; align-items: center; gap: 12px;">
                <i class="fas fa-${tipo === 'success' ? 'check-circle' : tipo === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
                <span>${mensagem}</span>
            </div>
        `;
        
        // Adicionar estilos inline
        notificacao.style.cssText = `
            position: fixed;
            top: 80px;
            right: 24px;
            padding: 16px 24px;
            background: ${tipo === 'success' ? '#10b981' : tipo === 'error' ? '#ef4444' : tipo === 'warning' ? '#f59e0b' : '#3b82f6'};
            color: white;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            z-index: 10000;
            animation: slideInRight 0.3s ease;
            font-weight: 500;
            font-size: 14px;
        `;
        
        document.body.appendChild(notificacao);
        
        // Remover após 4 segundos
        setTimeout(() => {
            notificacao.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => notificacao.remove(), 300);
        }, 4000);
    }
    
    // Adicionar animações ao head
    if (!document.getElementById('config-animations')) {
        const style = document.createElement('style');
        style.id = 'config-animations';
        style.textContent = `
            @keyframes slideInRight {
                from {
                    transform: translateX(400px);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(400px);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
});
</script>
