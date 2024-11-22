# URL Shortener com AWS Lambda

Este projeto é um exemplo de como criar um encurtador de URLs utilizando **AWS Lambda** e **Java**. A aplicação recebe uma URL original e gera um código único que pode ser usado como a URL encurtada.

## 📋 Funcionalidades

- Receber requisições JSON com uma URL original e um tempo de expiração.
- Gerar um código único de 8 caracteres para a URL encurtada.
- Retornar o código gerado no formato de resposta JSON.

## 🚀 Tecnologias Utilizadas

- **Java**: Linguagem de programação principal.
- **AWS Lambda**: Para execução de funções serverless.
- **Jackson ObjectMapper**: Para manipulação de JSON.
- **UUID**: Para geração de códigos únicos.
- **AWS SDK**: Dependência da AWS para uso do `RequestHandler`.
- **Amazon API Gateway**: Serviço que vai interceptar as funções e disponibilizá-las em uma única chamada. 

### 🖥️ O que é AWS Lambda?

O **AWS Lambda** é um serviço da Amazon Web Services (AWS) que permite executar código sem a necessidade de gerenciar servidores. Com o Lambda, você só precisa fornecer a função que deseja executar, e a AWS cuida de toda a infraestrutura necessária para rodá-la.

- **Como funciona?**  
  Você escreve uma função (em linguagens como Java, Python, Node.js, entre outras) e configura um "gatilho", que é o evento que dispara a execução da função. Por exemplo, um gatilho pode ser o envio de um arquivo para o Amazon S3, uma requisição HTTP via API Gateway, ou até um evento em uma fila SQS.

- **Vantagens:**
   - **Serverless:** Sem preocupações com servidores ou escalabilidade.
   - **Pagamento por uso:** Você só paga pelo tempo de execução e pela quantidade de requisições.
   - **Integração com outros serviços AWS:** Pode ser usado com S3, DynamoDB, API Gateway, e muito mais.

- **Exemplo simples:**  
  Imagine que você quer redimensionar imagens enviadas para um bucket S3. Você pode criar uma função Lambda que será acionada sempre que uma imagem for enviada ao bucket. Essa função processará a imagem automaticamente, sem necessidade de configurar servidores.

---

### 📦 O que é Amazon S3?

O **Amazon S3 (Simple Storage Service)** é um serviço de armazenamento de objetos da AWS. Ele permite armazenar e recuperar qualquer quantidade de dados, em qualquer momento, de forma confiável e segura.

- **Como funciona?**  
  Os dados são armazenados em "buckets" (como pastas na nuvem). Cada bucket pode conter arquivos (ou objetos) e possui uma estrutura plana, onde os objetos são identificados por um nome exclusivo chamado de "chave" (key).

- **Vantagens:**
   - **Alta disponibilidade:** Os dados armazenados são replicados em várias regiões, garantindo alta durabilidade.
   - **Escalabilidade:** Pode armazenar desde pequenos arquivos até petabytes de dados.
   - **Segurança:** Oferece controle de acesso detalhado e criptografia para proteger seus dados.

- **Exemplo de uso:**  
  Imagine que você está desenvolvendo um aplicativo que permite aos usuários fazer upload de fotos. O S3 pode ser usado para armazenar essas fotos, oferecendo acesso rápido e seguro para visualizá-las depois.

- **Quando usar?**
   - Para armazenar backups.
   - Para hospedar sites estáticos (HTML, CSS, JS).
   - Para armazenar e servir arquivos como imagens, vídeos ou documentos.

### 🖥️ Utilização AWS Lambda e Amazon S3

Esses dois serviços são frequentemente usados juntos. Por exemplo, você pode usar o **S3** para armazenar dados, como uploads de arquivos, e o **Lambda** para processar esses dados, como gerar miniaturas para imagens ou transcodificar vídeos. Isso torna o desenvolvimento de aplicações mais ágil e eficiente!

Para fazer a comunicação de uma função lambda com um bucket na S3 é necessário implementar políticas baseadas em recursos, ou seja, mesmo estando na mesma conta da AWS, elas não estão visíveis entre si.

Por exemplo:

- **Políticas**
  - AmazonS3FullAccess - A função lambda em questão vai ter acesso total ao S3, ou seja, a tudo, inclusive todos os buckets; (Não Recomendado)
  - Política em Linha: Cria uma nova politica com as suas configurações específicas:
    - PutObject: Para inserir objetos com o método Put;
    - GetObject: Para buscar dados de objetos;
    - Especificar ARNs (Resource bucket name): Para colocar um bucket específico de comunicação (Ex: url-shortener-storage-lambda);

### 🌐 O que é Amazon API Gateway?

O **Amazon API Gateway** é um serviço da AWS que permite criar, publicar, monitorar e proteger APIs de forma simples e escalável. Ele funciona como uma ponte entre os clientes (usuários ou sistemas) e os serviços de backend (como AWS Lambda, EC2, ou bancos de dados).

- **Como funciona?**  
  Você cria uma API e define suas rotas (endpoints). Cada rota pode ser configurada para executar diferentes ações, como chamar uma função Lambda, acessar dados armazenados no DynamoDB ou retornar respostas estáticas. O API Gateway cuida de autenticação, gerenciamento de tráfego, cache e muito mais.

- **Vantagens:**
  - **Escalabilidade:** Suporta milhões de chamadas de API por segundo sem necessidade de configuração manual.
  - **Integração nativa com AWS:** Se conecta facilmente a serviços como Lambda, S3, DynamoDB, entre outros.
  - **Segurança:** Oferece autenticação com IAM, políticas de segurança, e integração com o Amazon Cognito.
  - **Gerenciamento simplificado:** Permite criar diferentes versões ou estágios da sua API (ex.: *desenvolvimento*, *teste* e *produção*).

- **Exemplo de uso:**  
  Imagine que você está criando um serviço de encurtador de URLs.
  - O **Amazon API Gateway** pode expor um endpoint, como `https://api.meuencurtador.com/criar-url`.
  - Esse endpoint pode acionar uma função **AWS Lambda** que processa a requisição e retorna a URL encurtada.
  - O API Gateway gerencia todas as requisições e respostas, garantindo segurança e desempenho.

- **Quando usar?**
  - Para expor serviços RESTful ou WebSocket.
  - Para criar backends para aplicativos móveis ou web.
  - Para integrar com microserviços ou sistemas legados.

O **API Gateway** é uma ferramenta poderosa para combinar serviços AWS e criar soluções completas. Ele atua como a porta de entrada para as requisições, garantindo que elas sejam direcionadas corretamente e de forma eficiente para os serviços de backend.

## 🛠️ Como Configurar e Executar

### Pré-requisitos

- **Java 8+**
- **AWS CLI** configurada na sua máquina.
- **Maven** para gerenciar dependências e compilar o projeto.

### Configuração

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/url-shortener-lambda.git
   cd url-shortener-lambda
   ```

2. Instale as dependências e compile o projeto:

   ```bash
   mvn clean install
   ```

3. Empacote a aplicação como um JAR:

   ```bash
   mvn package
   ```

4. Faça o deploy da aplicação para a AWS Lambda usando o arquivo JAR gerado.

### Exemplo de Requisição

Envie uma requisição HTTP para a função Lambda com o seguinte corpo:

```json
{
  "body": "{\"originalUrl\":\"https://rocketseat.com.br\",\"expirationTime\":\"2024-12-31\"}"
}
```

A resposta será algo como:

```json
{
  "code": "a1b2c3d4"
}
```

### Erros Comuns

- **Erro ao converter JSON**: Certifique-se de enviar o `body` no formato esperado.
- **Problemas com a AWS Lambda**: Verifique as permissões e a configuração do ambiente.

---

Feito com 💜 pela [Rocketseat](https://rocketseat.com.br) e por [Gabriel Souza](https://github.com/gabrielsouzas)!
