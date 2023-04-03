# Fall-into-oblivion# Projeto - FALL-INTO-OBLIVION
Trabalho realizado para a unidade Curricular, Segurança Informática.

## ENTREGA: 23:55 do dia 26/05/2023

Os ficheiros devem ter, **contudo**, ter uma nomenclatura semelhante à que se segue (considere que T3 significa proposta de Trabalho 3, que veio a ser desenvolvida pelos alunos Ricardo, Rita e Raúl):

- código python () - .py → T3-Ricardo-Rita-Raul-code.py
- diagrama de ataque () - .png → T3-Ricardo-Rita-Raul-attack-diagram.png
- diagrama de sistema () - .jpg → T3-Ricardo-Rita-Raul-system-diagram.jpg
- executavel () - .exe → T3-Ricardo-Rita-Raul.exe
- código e outros artefactos () - .zip → T3-Ricardo-Rita-Raul.zip

## Resumo do Trabalho

A aplicação a desenvolver deve estar constantemente a monitorizar uma pasta chamada FALL-INTO-OBLIVION, e cifrar automaticamente todos os ficheiros que aí forem colocados. Deve também calcular um valor resumo, um Message Authentication
Code (MAC) ou uma assinatura digital do ficheiro. A chave usada para cifrar o ficheiro e calcular o MAC deve ser gerada automaticamente para cada ficheiro, mas derivada de um Personal Identification Number (PIN) de 3 ou 4 dígitos. Se um utilizador desejar reaver o seu ficheiro mais tarde, tem de adivinhar o código com que foi cifrado e autenticado. Tem 3 hipóteses para conseguir decifrar o ficheiro. Depois dessas 3 tentativas, o ficheiro deve ser eliminado do sistema operativo.

## Funcionalidades Minimas

- [ ]  Permitir cifrar ficheiros, guardando o resultado numa pasta chamada
FALL-INTO-OBLIVION;
- [ ]  calcular o valor de hash do ficheiro, guardando também o resultado junto com o criptograma (em ficheiros separados)
- [ ]  gerar automaticamente um PIN, e usá-lo como chave para cifrar cada ficheiro;
- [ ]  calcular o MAC dos criptogramas;
- [ ]  permitir decifrar o ficheiro por via da adivinhação do PIN. Só devem ser permitidas até 3 tentativas;
- [ ]  verificar a integridade do ficheiro no caso do PIN ter sido adivinhado.

Pode correr em modo modo Client Line Interface (CLI) ou em modo gráfico (fica ao
critério dos executantes).

Devem usar cifras e mecanismos de autenticação de mensagens de qualidade (e.g., Advanced Encryption Standard em modo Cipher Block Chainign (AES-CBC) e Hash MAC Secure Hash Algorithm 256 (HMAC-SHA256)).

## Funcionalidades adicionais

- [ ]  substituir os MACs por assinaturas digitais (o programa deve então também permitir
gerar as chaves pública e privadas);
- [ ]  permitir que o utilizador escolha a cifra a utilizar e o comprimento da chave de cifra;
- [ ]  permitir que o utilizador escolha a função de hash a usar;
- [ ]  ter um help completo e intuitivo.

Pensem numa forma de atacar o sistema (uma falha
da sua implementação) e dediquem-lhe um pequeno intervalo de tempo na apresentação
