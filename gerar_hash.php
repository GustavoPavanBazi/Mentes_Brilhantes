<?php
// Execute este arquivo uma vez para obter o hash da senha
$senha = "Mbtcc2025";
$hash = password_hash($senha, PASSWORD_DEFAULT);

echo "<h3>Hash gerado para a senha 'Mbtcc2025':</h3>";
echo "<p><strong>$hash</strong></p>";
echo "<p>Copie este hash e use no INSERT da tabela cad_administrador</p>";
?>
