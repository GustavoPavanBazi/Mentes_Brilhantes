<?php
session_start();

// Caminho do arquivo de conexão
$conexao_path = __DIR__ . '/../php/conexao.php';

if (file_exists($conexao_path)) {
    require_once($conexao_path);
} else {
    die('Erro: Arquivo conexao.php não encontrado');
}

// Verificar se a conexão foi estabelecida
if (!isset($sql) || $sql->connect_error) {
    die('Erro de conexão com banco de dados');
}

// Verificar se o usuário está logado e é responsável
if (!isset($_SESSION['usuario_logado']) || $_SESSION['tipo_usuario'] !== 'responsavel') {
    header('Location: ../html/login.html');
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'];

// Buscar dados do responsável
$stmt = $sql->prepare("SELECT nome, perfil FROM cad_responsavel WHERE id_responsa = ?");
$stmt->bind_param("i", $id_responsavel);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $dados = $result->fetch_assoc();
    $nome_completo = $dados['nome'];
    $foto_perfil_db = $dados['perfil'];
    
    // Construir caminhos corretos
    if (!empty($foto_perfil_db)) {
        $foto_perfil_path = __DIR__ . '/../' . $foto_perfil_db;
        $foto_perfil = '../' . $foto_perfil_db;
        $tem_foto = file_exists($foto_perfil_path);
    } else {
        $tem_foto = false;
        $foto_perfil = '';
    }
    
    // Extrair primeiro nome e iniciais
    $nomes = explode(' ', trim($nome_completo));
    $primeiro_nome = $nomes[0];
    
    if (count($nomes) >= 2) {
        $iniciais = strtoupper(substr($nomes[0], 0, 1) . substr($nomes[1], 0, 1));
    } else {
        $iniciais = strtoupper(substr($nomes[0], 0, 2));
    }
} else {
    $nome_completo = 'Usuário';
    $primeiro_nome = 'Usuário';
    $iniciais = 'US';
    $tem_foto = false;
    $foto_perfil = '';
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Luna AI - Assistente Especializada em TEA</title>
    <link rel="stylesheet" href="../css/luna.css">
    <link rel="icon" type="image/x-icon" href="../img/LogoMB.png">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <!-- Sidebar -->
    <aside class="sidebar" id="sidebar">
        <!-- Sidebar Header com Logo -->
        <div class="sidebar-header">
            <div class="sidebar-logo">
                <img src="../img/LogoMB.png" alt="Mentes Brilhantes">
            </div>
            <div class="sidebar-title">
                <h2>Luna AI</h2>
                <p>Assistente Especializada</p>
            </div>
        </div>

        <!-- New Chat Button -->
        <button class="new-chat-btn" id="newChatBtn">
            <i class="fas fa-plus"></i>
            Nova Conversa
        </button>

        <!-- Chat History -->
        <div class="chat-history" id="chatHistory">
            <div class="loading-skeleton">
                <div class="skeleton-item"></div>
                <div class="skeleton-item"></div>
                <div class="skeleton-item"></div>
            </div>
        </div>

        <!-- Sidebar Footer -->
        <div class="sidebar-footer">
            <div class="user-info">
                <?php if ($tem_foto): ?>
                    <img src="<?php echo htmlspecialchars($foto_perfil); ?>" alt="Foto de perfil" class="user-avatar-img">
                <?php else: ?>
                    <div class="user-avatar-placeholder"><?php echo $iniciais; ?></div>
                <?php endif; ?>
                <div class="user-details">
                    <span class="user-name"><?php echo htmlspecialchars($primeiro_nome); ?></span>
                    <span class="user-status">
                        <i class="fas fa-circle"></i> Online
                    </span>
                </div>
            </div>
            <a href="adm_responsavel.php" class="logout-btn" title="Sair">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
    </aside>

    <!-- Overlay para mobile -->
    <div class="sidebar-overlay" id="sidebarOverlay"></div>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Chat Header -->
        <header class="chat-header">
            <button class="menu-toggle" id="menuToggle">
                <i class="fas fa-bars"></i>
            </button>
            
            <div class="header-info">
                <div class="luna-avatar-header">
                    <img src="../img/luna.png" alt="Luna AI">
                </div>
                <div class="header-text">
                    <h1>Luna AI</h1>
                    <div class="status-indicator">
                        <i class="fas fa-circle"></i>
                        Pronta para ajudar
                    </div>
                </div>
            </div>

            <button class="header-action-btn" id="clearChatBtn" title="Limpar conversa">
                <i class="fas fa-trash-alt"></i>
            </button>
        </header>

        <!-- Chat Messages Area -->
        <div class="chat-messages" id="chatMessages">
            <!-- Welcome Message -->
            <div class="welcome-screen">
                <div class="welcome-container">
                    <div class="welcome-header">
                        <div class="welcome-logo">
                            <img src="../img/LogoMB.png" alt="Mentes Brilhantes">
                        </div>
                        <div class="welcome-intro">
                            <h1>Bem-vindo à Luna AI</h1>
                            <p>Sua assistente inteligente especializada em Transtorno do Espectro Autista. Estou aqui para fornecer suporte especializado, orientações baseadas em evidências científicas e estratégias personalizadas para o desenvolvimento e bem-estar.</p>
                        </div>
                    </div>

                    <div class="capabilities-grid">
                        <div class="capability-card">
                            <div class="capability-icon">
                                <i class="fas fa-brain"></i>
                            </div>
                            <h3>Conhecimento Especializado</h3>
                            <p>Informações baseadas em pesquisas e práticas comprovadas</p>
                        </div>
                        <div class="capability-card">
                            <div class="capability-icon">
                                <i class="fas fa-comments"></i>
                            </div>
                            <h3>Apoio Contínuo</h3>
                            <p>Suporte acolhedor e não-julgador disponível quando precisar</p>
                        </div>
                        <div class="capability-card">
                            <div class="capability-icon">
                                <i class="fas fa-lightbulb"></i>
                            </div>
                            <h3>Soluções Práticas</h3>
                            <p>Estratégias personalizadas e aplicáveis ao dia a dia</p>
                        </div>
                    </div>

                    <div class="quick-suggestions">
                        <p class="suggestions-title">Como posso ajudar você hoje?</p>
                        <div class="suggestions-grid">
                            <button class="suggestion-btn" onclick="enviarMensagemRapida('Como criar rotinas visuais estruturadas?')">
                                Rotinas Visuais
                            </button>
                            <button class="suggestion-btn" onclick="enviarMensagemRapida('Estratégias de comunicação para autismo')">
                                Comunicação
                            </button>
                            <button class="suggestion-btn" onclick="enviarMensagemRapida('Técnicas de regulação sensorial')">
                                Regulação Sensorial
                            </button>
                            <button class="suggestion-btn" onclick="enviarMensagemRapida('Atividades para desenvolvimento cognitivo')">
                                Desenvolvimento Cognitivo
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Chat Input Area -->
        <div class="chat-input-container">
            <div class="chat-input-wrapper">
                <textarea 
                    id="messageInput" 
                    placeholder="Digite sua mensagem..."
                    rows="1"
                    maxlength="2000"
                ></textarea>
                <button class="send-btn" id="sendButton">
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
            <div class="input-footer">
                <span class="char-counter" id="charCounter">0 / 2000</span>
                <span class="input-hint">
                    Pressione Enter para enviar ou Shift+Enter para nova linha
                </span>
            </div>
        </div>
    </main>

    <!-- VARIÁVEIS PHP PARA JAVASCRIPT -->
    <script>
        const USER_TEM_FOTO = <?php echo $tem_foto ? 'true' : 'false'; ?>;
        const USER_FOTO_PERFIL = '<?php echo addslashes($foto_perfil); ?>';
        const USER_INICIAIS = '<?php echo $iniciais; ?>';
    </script>

    <!-- JAVASCRIPT PRINCIPAL -->
    <script>
        // ========================================
        // CONFIGURAÇÕES GLOBAIS
        // ========================================
        const CONFIG = {
            maxMessageLength: 2000,
            maxHistoryMessages: 10,
            typingIndicatorDelay: 500,
            scrollBehavior: 'smooth'
        };

        let conversaAtualId = null;
        let isProcessing = false;
        let conversasCache = [];

        // ========================================
        // INICIALIZAÇÃO
        // ========================================
        document.addEventListener('DOMContentLoaded', function() {
            console.log('%c🤖 Luna AI System Iniciado', 'color: #2563eb; font-size: 16px; font-weight: bold');
            
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('sendButton');
            const newChatBtn = document.getElementById('newChatBtn');
            const clearChatBtn = document.getElementById('clearChatBtn');
            const charCounter = document.getElementById('charCounter');
            const menuToggle = document.getElementById('menuToggle');
            const sidebar = document.getElementById('sidebar');
            const sidebarOverlay = document.getElementById('sidebarOverlay');
            
            carregarConversas();
            
            if (sendButton) {
                sendButton.addEventListener('click', (e) => {
                    e.preventDefault();
                    enviarMensagem();
                });
            }
            
            if (messageInput) {
                messageInput.addEventListener('keydown', function(e) {
                    if (e.key === 'Enter' && !e.shiftKey) {
                        e.preventDefault();
                        enviarMensagem();
                    }
                });
                
                messageInput.addEventListener('input', function() {
                    this.style.height = 'auto';
                    this.style.height = Math.min(this.scrollHeight, 120) + 'px';
                    
                    if (charCounter) {
                        const count = this.value.length;
                        charCounter.textContent = `${count} / ${CONFIG.maxMessageLength}`;
                        charCounter.style.color = count > CONFIG.maxMessageLength * 0.9 ? '#ef4444' : '#6b7280';
                    }
                });
                
                messageInput.focus();
            }
            
            if (newChatBtn) {
                newChatBtn.addEventListener('click', novaConversa);
            }
            
            if (clearChatBtn) {
                clearChatBtn.addEventListener('click', function() {
                    if (conversaAtualId) {
                        confirmarDeletarConversa(conversaAtualId);
                    } else {
                        mostrarNotificacao('Nenhuma conversa ativa para limpar', 'info');
                    }
                });
            }
            
            if (menuToggle && sidebar && sidebarOverlay) {
                menuToggle.addEventListener('click', function() {
                    sidebar.classList.toggle('open');
                    sidebarOverlay.classList.toggle('show');
                });
                
                sidebarOverlay.addEventListener('click', function() {
                    sidebar.classList.remove('open');
                    sidebarOverlay.classList.remove('show');
                });
            }
        });

        // ========================================
        // FUNÇÕES PRINCIPAIS
        // ========================================
        function carregarConversas() {
            const chatHistory = document.getElementById('chatHistory');
            
            fetch('../php/carregar_conversas.php', {
                method: 'GET',
                credentials: 'same-origin'
            })
            .then(response => response.json())
            .then(data => {
                if (data.sucesso) {
                    conversasCache = data.conversas;
                    exibirConversas(data.conversas);
                } else {
                    if (chatHistory) {
                        chatHistory.innerHTML = '<div class="error-state"><i class="fas fa-exclamation-circle"></i><p>Erro ao carregar conversas</p></div>';
                    }
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                if (chatHistory) {
                    chatHistory.innerHTML = '<div class="error-state"><i class="fas fa-wifi-slash"></i><p>Erro de conexão</p></div>';
                }
            });
        }

        function exibirConversas(conversas) {
            const chatHistory = document.getElementById('chatHistory');
            if (!chatHistory) return;
            
            if (conversas.length === 0) {
                chatHistory.innerHTML = '<div class="empty-state"><i class="fas fa-comments"></i><p>Nenhuma conversa ainda</p><span>Clique em "Nova Conversa" para começar</span></div>';
                return;
            }
            
            chatHistory.innerHTML = '';
            
            conversas.forEach(conversa => {
                const conversaDiv = document.createElement('div');
                conversaDiv.className = 'chat-tab';
                conversaDiv.dataset.conversaId = conversa.id_conversa;
                
                const dataFormatada = formatarDataRelativa(conversa.data_ultima_mensagem);
                
                conversaDiv.innerHTML = `
                    <i class="fas fa-message chat-tab-icon"></i>
                    <div class="chat-tab-content">
                        <div class="chat-tab-title">${escapeHtml(conversa.titulo)}</div>
                        <div class="chat-tab-meta">
                            <span>${dataFormatada}</span>
                        </div>
                    </div>
                    <button class="chat-tab-delete" onclick="confirmarDeletarConversa(${conversa.id_conversa}); event.stopPropagation();" title="Deletar">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                `;
                
                conversaDiv.addEventListener('click', function() {
                    carregarConversa(conversa.id_conversa);
                    document.querySelectorAll('.chat-tab').forEach(tab => tab.classList.remove('active'));
                    conversaDiv.classList.add('active');
                    
                    if (window.innerWidth <= 768) {
                        document.getElementById('sidebar').classList.remove('open');
                        document.getElementById('sidebarOverlay').classList.remove('show');
                    }
                });
                
                chatHistory.appendChild(conversaDiv);
            });
        }

        function carregarConversa(idConversa) {
            conversaAtualId = idConversa;
            const chatMessages = document.getElementById('chatMessages');
            
            if (chatMessages) {
                chatMessages.innerHTML = '<div class="loading-state"><div class="loading-spinner"></div><p>Carregando conversa...</p></div>';
            }
            
            fetch(`../php/carregar_mensagens.php?id_conversa=${idConversa}`, {
                method: 'GET',
                credentials: 'same-origin'
            })
            .then(response => response.json())
            .then(data => {
                if (data.sucesso) {
                    exibirMensagens(data.mensagens);
                } else {
                    mostrarErro('Erro ao carregar mensagens');
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                mostrarErro('Erro ao carregar conversa');
            });
        }

        function exibirMensagens(mensagens) {
            const chatMessages = document.getElementById('chatMessages');
            if (!chatMessages) return;
            
            chatMessages.innerHTML = '';
            
            if (mensagens.length === 0) {
                mostrarMensagemBoasVindas();
                return;
            }
            
            mensagens.forEach(msg => {
                adicionarMensagemNaTela(msg.conteudo, msg.remetente, msg.data_envio);
            });
            
            scrollToBottom();
        }

        function adicionarMensagemNaTela(conteudo, remetente, dataEnvio = null) {
            const chatMessages = document.getElementById('chatMessages');
            if (!chatMessages) return;
            
            const welcomeMsg = document.querySelector('.welcome-screen');
            if (welcomeMsg) {
                welcomeMsg.remove();
            }
            
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${remetente} fade-in`;
            
            const timestamp = dataEnvio ? formatarDataHora(dataEnvio) : formatarDataHora(new Date());
            
            if (remetente === 'usuario') {
                let avatarHTML = '';
                if (USER_TEM_FOTO) {
                    avatarHTML = `<img src="${USER_FOTO_PERFIL}" alt="Foto de perfil">`;
                } else {
                    avatarHTML = `<span class="user-avatar-text">${USER_INICIAIS}</span>`;
                }
                
                messageDiv.innerHTML = `
                    <div class="message-wrapper">
                        <div class="message-content">${formatarMensagem(conteudo)}</div>
                        <span class="message-timestamp">${timestamp}</span>
                    </div>
                    <div class="user-avatar">${avatarHTML}</div>`;
            } else {
                messageDiv.innerHTML = `
                    <div class="ai-avatar">
                        <img src="../img/luna.png" alt="Luna AI">
                    </div>
                    <div class="message-wrapper">
                        <div class="message-content">${formatarMensagem(conteudo)}</div>
                        <span class="message-timestamp">${timestamp}</span>
                    </div>`;
            }
            
            chatMessages.appendChild(messageDiv);
            scrollToBottom();
        }

        function enviarMensagem() {
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('sendButton');
            
            if (!messageInput || !sendButton) return;
            
            const mensagem = messageInput.value.trim();
            
            if (!mensagem) {
                messageInput.focus();
                return;
            }
            
            if (mensagem.length > CONFIG.maxMessageLength) {
                mostrarNotificacao('Mensagem muito longa. Máximo de ' + CONFIG.maxMessageLength + ' caracteres.', 'error');
                return;
            }
            
            if (isProcessing) return;
            
            isProcessing = true;
            messageInput.disabled = true;
            sendButton.disabled = true;
            sendButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            
            adicionarMensagemNaTela(mensagem, 'usuario');
            
            messageInput.value = '';
            messageInput.style.height = 'auto';
            document.getElementById('charCounter').textContent = '0 / 2000';
            
            setTimeout(() => adicionarIndicadorDigitando(), CONFIG.typingIndicatorDelay);
            
            const dados = {
                mensagem: mensagem,
                id_conversa: conversaAtualId
            };
            
            fetch('../php/salvar_mensagem.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(dados)
            })
            .then(response => response.json())
            .then(data => {
                removerIndicadorDigitando();
                
                if (data.sucesso) {
                    if (data.id_conversa && !conversaAtualId) {
                        conversaAtualId = data.id_conversa;
                        carregarConversas();
                        
                        setTimeout(() => {
                            const novaTab = document.querySelector(`[data-conversa-id="${data.id_conversa}"]`);
                            if (novaTab) {
                                document.querySelectorAll('.chat-tab').forEach(tab => tab.classList.remove('active'));
                                novaTab.classList.add('active');
                            }
                        }, 800);
                    }
                    
                    adicionarMensagemNaTela(data.resposta, 'ia', data.timestamp);
                    
                    messageInput.disabled = false;
                    sendButton.disabled = false;
                    sendButton.innerHTML = '<i class="fas fa-paper-plane"></i>';
                    messageInput.focus();
                    isProcessing = false;
                } else {
                    throw new Error(data.erro || 'Erro desconhecido');
                }
            })
            .catch(error => {
                removerIndicadorDigitando();
                console.error('Erro:', error);
                mostrarNotificacao('Erro ao enviar mensagem: ' + error.message, 'error');
                
                messageInput.disabled = false;
                sendButton.disabled = false;
                sendButton.innerHTML = '<i class="fas fa-paper-plane"></i>';
                messageInput.value = mensagem;
                isProcessing = false;
            });
        }

        function enviarMensagemRapida(texto) {
            const messageInput = document.getElementById('messageInput');
            if (messageInput) {
                messageInput.value = texto;
                messageInput.focus();
                enviarMensagem();
            }
        }

        function novaConversa() {
            const messageInput = document.getElementById('messageInput');
            if (messageInput && messageInput.value.trim()) {
                if (!confirm('Há uma mensagem sendo digitada. Deseja descartá-la?')) {
                    return;
                }
            }
            
            conversaAtualId = null;
            
            if (messageInput) {
                messageInput.value = '';
                messageInput.style.height = 'auto';
                document.getElementById('charCounter').textContent = '0 / 2000';
            }
            
            limparChat();
            document.querySelectorAll('.chat-tab').forEach(tab => tab.classList.remove('active'));
            
            if (messageInput) {
                messageInput.focus();
            }
            
            if (window.innerWidth <= 768) {
                document.getElementById('sidebar').classList.remove('open');
                document.getElementById('sidebarOverlay').classList.remove('show');
            }
            
            mostrarNotificacao('Nova conversa iniciada', 'success');
        }

        function limparChat() {
            const chatMessages = document.getElementById('chatMessages');
            if (chatMessages) {
                chatMessages.innerHTML = '';
                mostrarMensagemBoasVindas();
            }
        }

        function mostrarMensagemBoasVindas() {
            const chatMessages = document.getElementById('chatMessages');
            if (!chatMessages) return;
            
            chatMessages.innerHTML = `
                <div class="welcome-screen">
                    <div class="welcome-container">
                        <div class="welcome-header">
                            <div class="welcome-logo">
                                <img src="../img/LogoMB.png" alt="Mentes Brilhantes">
                            </div>
                            <div class="welcome-intro">
                                <h1>Bem-vindo à Luna AI</h1>
                                <p>Sua assistente inteligente especializada em Transtorno do Espectro Autista. Estou aqui para fornecer suporte especializado, orientações baseadas em evidências científicas e estratégias personalizadas.</p>
                            </div>
                        </div>
                        <div class="capabilities-grid">
                            <div class="capability-card">
                                <div class="capability-icon"><i class="fas fa-brain"></i></div>
                                <h3>Conhecimento Especializado</h3>
                                <p>Informações baseadas em pesquisas comprovadas</p>
                            </div>
                            <div class="capability-card">
                                <div class="capability-icon"><i class="fas fa-comments"></i></div>
                                <h3>Apoio Contínuo</h3>
                                <p>Suporte acolhedor disponível quando precisar</p>
                            </div>
                            <div class="capability-card">
                                <div class="capability-icon"><i class="fas fa-lightbulb"></i></div>
                                <h3>Soluções Práticas</h3>
                                <p>Estratégias personalizadas aplicáveis</p>
                            </div>
                        </div>
                        <div class="quick-suggestions">
                            <p class="suggestions-title">Como posso ajudar você hoje?</p>
                            <div class="suggestions-grid">
                                <button class="suggestion-btn" onclick="enviarMensagemRapida('Como criar rotinas visuais estruturadas?')">Rotinas Visuais</button>
                                <button class="suggestion-btn" onclick="enviarMensagemRapida('Estratégias de comunicação para autismo')">Comunicação</button>
                                <button class="suggestion-btn" onclick="enviarMensagemRapida('Técnicas de regulação sensorial')">Regulação Sensorial</button>
                                <button class="suggestion-btn" onclick="enviarMensagemRapida('Atividades para desenvolvimento cognitivo')">Desenvolvimento Cognitivo</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }

        function confirmarDeletarConversa(idConversa) {
            if (!confirm('Tem certeza que deseja deletar esta conversa? Esta ação não pode ser desfeita.')) return;
            
            fetch('../php/deletar_conversa.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({ id_conversa: idConversa })
            })
            .then(response => response.json())
            .then(data => {
                if (data.sucesso) {
                    if (conversaAtualId === idConversa) {
                        conversaAtualId = null;
                        limparChat();
                    }
                    carregarConversas();
                    mostrarNotificacao('Conversa deletada com sucesso', 'success');
                } else {
                    mostrarNotificacao('Erro ao deletar conversa', 'error');
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                mostrarNotificacao('Erro ao deletar conversa', 'error');
            });
        }

        function adicionarIndicadorDigitando() {
            const chatMessages = document.getElementById('chatMessages');
            if (!chatMessages) return;
            
            removerIndicadorDigitando();
            
            const loadingDiv = document.createElement('div');
            loadingDiv.className = 'message ia typing-indicator fade-in';
            loadingDiv.id = 'typingIndicator';
            loadingDiv.innerHTML = `
                <div class="ai-avatar">
                    <img src="../img/luna.png" alt="Luna AI">
                </div>
                <div class="typing-dots">
                    <span></span><span></span><span></span>
                </div>
            `;
            
            chatMessages.appendChild(loadingDiv);
            scrollToBottom();
        }

        function removerIndicadorDigitando() {
            const indicator = document.getElementById('typingIndicator');
            if (indicator) {
                indicator.classList.add('fade-out');
                setTimeout(() => indicator.remove(), 300);
            }
        }

        // ========================================
        // FUNÇÕES UTILITÁRIAS
        // ========================================
        function scrollToBottom() {
            const chatMessages = document.getElementById('chatMessages');
            if (chatMessages) {
                chatMessages.scrollTo({
                    top: chatMessages.scrollHeight,
                    behavior: CONFIG.scrollBehavior
                });
            }
        }

        function formatarMensagem(texto) {
            return escapeHtml(texto)
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                .replace(/\*(.*?)\*/g, '<em>$1</em>')
                .replace(/\n/g, '<br>');
        }

        function escapeHtml(text) {
            const map = {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'};
            return text.replace(/[&<>"']/g, m => map[m]);
        }

        function formatarDataHora(data) {
            const d = new Date(data);
            return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
        }

        function formatarDataRelativa(dataStr) {
            const agora = new Date();
            const data = new Date(dataStr);
            const diffMs = agora - data;
            const diffMins = Math.floor(diffMs / 60000);
            const diffHoras = Math.floor(diffMs / 3600000);
            const diffDias = Math.floor(diffMs / 86400000);
            
            if (diffMins < 1) return 'Agora';
            if (diffMins < 60) return `${diffMins}min atrás`;
            if (diffHoras < 24) return `${diffHoras}h atrás`;
            if (diffDias === 1) return 'Ontem';
            if (diffDias < 7) return `${diffDias} dias atrás`;
            
            return data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
        }

        function mostrarNotificacao(mensagem, tipo = 'info') {
            const toast = document.createElement('div');
            toast.className = `notification-toast ${tipo}`;
            
            const icones = {
                success: 'fa-check-circle',
                error: 'fa-exclamation-circle',
                warning: 'fa-exclamation-triangle',
                info: 'fa-info-circle'
            };
            
            toast.innerHTML = `<i class="fas ${icones[tipo]}"></i><span>${mensagem}</span>`;
            document.body.appendChild(toast);
            setTimeout(() => toast.classList.add('show'), 100);
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => toast.remove(), 300);
            }, 3000);
        }

        function mostrarErro(mensagem) {
            const chatMessages = document.getElementById('chatMessages');
            if (chatMessages) {
                chatMessages.innerHTML = `
                    <div class="error-state">
                        <i class="fas fa-exclamation-triangle"></i>
                        <p>${mensagem}</p>
                        <button class="retry-btn" onclick="location.reload()">
                            <i class="fas fa-redo"></i> Tentar Novamente
                        </button>
                    </div>
                `;
            }
        }
    </script>
</body>
</html>
