# Redes - T1

Laboratório de Redes - Trabalho 1

# FUNCIONALIDADES

## registrar usuário

- registrar usuário:"/REG username <username>"
  exemplo: "/REG username johndoe"
- "/REG password <password>"
  exemplo: "/REG username abracadabra"

  persistência: 
  manter lista de usuários registrados "<username> <password>"

## fazer login

    envia: "/LOGIN <username>". Exemplo: "/LOGIN johndoe"
    server solicita password assim: "password:"
    informa senha e dá enter
    se o server encontrou, dá mensagem ("Usuário " + userName + " está online.")
    se não encontrou ou deu erro, informa: "Usuário ou senha incorretos."

## listar usuarios online

    comando: "/ONLINE"

Comando devolve lista de usuarios online.
Exemplo de retorno:

Usuários online:
kieroff
adao
pedro
miguel

## conversar (enviar e receber mensagens)

comando: "/MSG <username> <message>"
Exemplo:"/MSG johndoe como vai?"
obs: usuário precisa estar online. Se estiver offline, retorna mensagem: "Usuário <username> não está online."

## enviar arquivo (enviar e receber arquivos)

comando: "/FTP <username> <linkdoarquivo.extensao>"
Exemplo:"/FTP johndoe src/foto.jpg"
obs: usuário precisa estar online. Se estiver offline, retorna mensagem: "Usuário <username> não está online."
quando envia arquivo, cria um diretorio com o nome do destinatário no diretorio "/persistence" e o arquivo enviado vai ser copiado/transferido para o diretorio do destinatário do arquivo. Exemplo:
comando: "/FTP johndoe src/foto.jpg"

- cria diretório: "/persistence/johndoe/"
- copia arquivo para o diretório recém criado
- exibe mensagem para o usuário: "<username> <link>" = "kieroff /persistence/johndoe/foto.jpg" (clicável)

## seguir usuário

comando para seguir: "/FOLLOW <username> true"
comando para deixar de seguir: "/FOLLOW <username> false"
comando para exibir lista de quem eu sigo: "/FOLLOW who"
devolve a lista de usuários que eu marquei para seguir.
Exemplo:

Seguindo:
kieroff
adao
pedro
miguel

quando um usuário da minha lista de follow se conectar (online) enviar mensagem "Usuário <username> está online."

## newsletter

criar canal newsletter: "/NEWS create"
remover canal newsletter: "/NEWS delete"
assinar newsletter: "/NEWS <username> true"
enviar newsletter: "/NEWS MSG <message>"
listar minha lista newsletter: "/NEWS who"
desinscrever canal: "/NEWS <username> false"
visualizar mensagens (últimas x mensagens): "/NEWS list"

- quando usuário cria canal de newsletter, após o seu username será exibido um asterisco: "johndoe\*", indicando que ele possui um canal ativo.
- quando envia mensagem da newsletter, alimenta a lista de mensagens: "news <username> : <message>". Exemplo:

news johndoe : hoje vai ter sol
news kieroff : sabadou!
news miguel : arriba, arriba!

- ao remover o canal, remove o asterisco \* após o nome do usuário
- criar lógica para tratar e exibir a lista quando assinar newsletter
- ao enviar mensagem na newsletter, registra a mensagem na fila da newsletter 
- quando cria o canal de newsletter vincula o usuário na lista de usuários ativos da "newsletter"

para persistir:
- lista de usuários com newsletter ativos "newsletter_users"
- marca asterisco no final do nome do usuário (tem que validar esse caractere quando registrar usuário e informar erro se alguém usar, porque é reservado da aplicação)
- lista de mensagens da newsletter, no formato "<username> : <message>" até 100 caracteres de mensagem permitida
- lista de inscrição da newsletter para o usuário, contendo os usernames dos usuários que ele assinou
para exibir as mensagens, verificar primeiro se o usuário ainda está ativo na newsletter, filtrar mensagens com os usernames e exibir elas. 

## Ajuda 

comando: "/HELP"
- exibe a lista completa de comandos disponíveis, com breve explicação sobre os mesmos, no formato de uma tabela, mais ou menos assim eu pensei:
      ┌───────────────┬──────────────┬─────────────────────────────────────────────┐
      │ comand        │ example      │ description                                 │
      │ /REG          │ /reg johndoe │ Comando para registrar novo usuario         │
      │ /NEWS create  │ /NEWS create │ Comando para criar newsletter               │
      └───────────────┴──────────────┴─────────────────────────────────────────────┘

