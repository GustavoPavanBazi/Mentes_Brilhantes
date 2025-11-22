<?php
// Inicia a sessão apenas UMA VEZ no topo do arquivo
session_start();
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Mentes Brilhantes</title>
    <link rel="icon" type="image/x-icon" href="../img/LogoMB.png">
    <link rel="stylesheet" href="../css/login.css">
</head>
<body>
    <div class="container">
        <div class="left-side">
            <div class="logo">
                <img src="../img/LogoMB.png" alt="Logo">
            </div>
        </div>

        <div class="right-side">
            <div class="login-form">
                <h2 class="form-title">Faça o seu Login</h2>
                
                <!-- Exibe erro SOMENTE se existir na sessão -->
                <?php
                if (isset($_SESSION['erro_login'])) {
                    echo '<div class="alert-error">' . htmlspecialchars($_SESSION['erro_login']) . '</div>';
                    unset($_SESSION['erro_login']); // Remove após exibir
                }
                ?>

                <form action="../php/loginsistema.php" method="POST">
                    <div class="form-group">
                        <label for="email" class="form-label">E-mail ou CPF:</label>
                        <input type="text" id="email" name="email" class="form-input" 
                               placeholder="Digite seu e-mail ou CPF" required>
                    </div>

                    <div class="form-group">
                        <label for="senha" class="form-label">Senha:</label>
                        <input type="password" id="senha" name="senha" class="form-input" 
                               placeholder="Digite sua senha" required>
                    </div>

                    <button type="submit" class="login-btn">Entrar</button>
                </form>
                
                <div class="form-links">
                    <a href="../index.html" class="form-link">Continuar sem fazer login</a>
                </div>
                
               
                
                 <div class="register-link">
                    <p>Não tem uma conta? <a href="../html/cadastro.html">Criar conta</a></p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>

    <script>
        // Verifica se há mensagem de erro na sessão
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            const erro = urlParams.get('erro');
            
            if (erro) {
  document.getElementById('errorMessage').style.display = 'block';
  document.getElementById('errorText').textContent = decodeURIComponent(erro);
  // Adiciona estilo nos inputs
  document.getElementById('email').classList.add('border-danger');
  document.getElementById('senha').classList.add('border-danger');
}

        };

        function showForgotPassword() {
            alert('Funcionalidade de recuperação de senha será implementada em breve.');
        }

        function loginWithApple() {
            alert('Login com Apple será implementado em breve.');
        }

        function loginWithGoogle() {
            alert('Login com Google será implementado em breve.');
        }
    </script>
</body>
</html>