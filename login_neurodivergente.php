<?php
session_start();

// Verificar se o responsável está logado
if (!isset($_SESSION['id_responsavel']) && !isset($_SESSION['id_responsa'])) {
    header('Location: login_responsavel.php');
    exit;
}

$id_responsavel = $_SESSION['id_responsavel'] ?? $_SESSION['id_responsa'];
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vincular Neurodivergente - Mentes Brilhantes</title>
    <link rel="stylesheet" href="cadastroneuro.css">
    <style>
        .vincular-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            padding: 20px;
        }
        
        .vincular-box {
            width: 100%;
            max-width: 500px;
            background: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.15);
        }
        
        .vincular-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .vincular-title {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }
        
        .vincular-subtitle {
            font-size: 14px;
            color: #666;
            line-height: 1.6;
        }
        
        .info-box {
            background: #e8f4ff;
            border-left: 4px solid #4A90E2;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 25px;
        }
        
        .info-box-title {
            font-size: 14px;
            font-weight: 600;
            color: #4A90E2;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .info-box-text {
            font-size: 13px;
            color: #555;
            line-height: 1.5;
        }
        
        .alert {
            padding: 12px 16px;
            border-radius: 12px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        
        .alert-error {
            background: #fee;
            color: #c33;
            border: 1px solid #fcc;
        }
        
        .alert-success {
            background: #efe;
            color: #3c3;
            border: 1px solid #cfc;
        }
        
        .form-actions {
            display: flex;
            gap: 12px;
            margin-top: 25px;
        }
        
        .btn {
            flex: 1;
            padding: 14px;
            border: none;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn-primary {
            background: #4A90E2;
            color: white;
        }
        
        .btn-primary:hover {
            background: #357ABD;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
        }
        
        .btn-secondary {
            background: #f0f0f0;
            color: #666;
        }
        
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        
        .input-group {
            position: relative;
        }
        
        .toggle-password {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #999;
            cursor: pointer;
            font-size: 18px;
            padding: 5px;
        }
        
        .toggle-password:hover {
            color: #4A90E2;
        }
    </style>
</head>
<body>
    <div class="vincular-container">
        <div class="vincular-box">
            <div class="vincular-header">
                <h1 class="vincular-title">🔗 Vincular Neurodivergente</h1>
                <p class="vincular-subtitle">
                    Adicione um neurodivergente já cadastrado à sua conta
                </p>
            </div>
            
            <div class="info-box">
                <div class="info-box-title">
                    ℹ️ Como funciona?
                </div>
                <div class="info-box-text">
                    Digite o <strong>CPF ou E-mail</strong> e a <strong>senha</strong> do neurodivergente que você deseja vincular. 
                    O neurodivergente continuará vinculado ao responsável original e também ficará disponível para você gerenciar.
                </div>
            </div>
            
            <?php
            if (isset($_SESSION['erro_vinculo'])) {
                echo '<div class="alert alert-error">' . htmlspecialchars($_SESSION['erro_vinculo']) . '</div>';
                unset($_SESSION['erro_vinculo']);
            }
            
            if (isset($_SESSION['sucesso_vinculo'])) {
                echo '<div class="alert alert-success">' . htmlspecialchars($_SESSION['sucesso_vinculo']) . '</div>';
                unset($_SESSION['sucesso_vinculo']);
            }
            ?>
            
            <form action="../php/processar_login_neurodivergente.php" method="POST" id="vincularForm">
                <div class="form-group">
                    <label class="form-label" for="identificador">
                        CPF ou E-mail do Neurodivergente *
                    </label>
                    <input 
                        type="text" 
                        class="form-input" 
                        id="identificador" 
                        name="identificador" 
                        placeholder="Digite o CPF ou e-mail"
                        required
                    >
                </div>
                
                <div class="form-group">
                    <label class="form-label" for="senha">
                        Senha do Neurodivergente *
                    </label>
                    <div class="input-group">
                        <input 
                            type="password" 
                            class="form-input" 
                            id="senha" 
                            name="senha" 
                            placeholder="Digite a senha"
                            required
                        >
                        <button type="button" class="toggle-password" onclick="togglePassword()">
                            👁️
                        </button>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="window.location.href='adm_responsavel.php'">
                        Cancelar
                    </button>
                    <button type="submit" class="btn btn-primary">
                        Vincular Agora
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function togglePassword() {
            const senhaInput = document.getElementById('senha');
            const toggleBtn = document.querySelector('.toggle-password');
            
            if (senhaInput.type === 'password') {
                senhaInput.type = 'text';
                toggleBtn.textContent = '🙈';
            } else {
                senhaInput.type = 'password';
                toggleBtn.textContent = '👁️';
            }
        }
        
        // Remover formatação do CPF antes de enviar
        document.getElementById('vincularForm').addEventListener('submit', function(e) {
            const identificadorInput = document.getElementById('identificador');
            identificadorInput.value = identificadorInput.value.replace(/[^\w@.-]/g, '');
        });
    </script>
</body>
</html>
