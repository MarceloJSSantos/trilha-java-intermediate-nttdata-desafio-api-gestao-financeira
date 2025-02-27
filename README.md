# 🗂️DESAFIO - Trilha Java Intermediate (janeiro/2025) 

## 📝Descrição
API de gestão financeira para clientes bancários que permite aos usuários o gerenciamento de suas finanças pessoais.

### 💻Tecnologias
* Spring Boot 3
* Java 17
* MySQL

### 📚Dependências
* Spring Web
* pring Boot DevTools
* Lombok
* Spring Data JPA
* Validation
* Flyway Migration
* MySQL Driver
* Spring Security

### 🏗️Desenvolvimento
- [x] Criação do projeto (Spring Initializr)
- [x] Criação o BD no MySQL (só o banco as tabelas serão criadas com scripts com o FlayWay)
- [x] Configuração BD/JPA (Inclusão dos parâmetros de acesso ao BD no 'application.properties')
- [x] Criação as requisições 'Usuário'
  * Cadastrar
  * Consultar um Cadastrado
  * Atualizar
  * Desativar e Reativar
  - [x] Controller
  - [x] DTOs
  - [x] Service
  - [x] Model
  - [x] Repository
  - [x] Script BD
- [x] Criação da classe centralizadora de Tratamento de Erros
- [x] Criação as requisições 'Transações' (CRUD principal)
  * Cadastrar
  * Consultar uma cadastrada
  * Atualizar
  * Exclusão definitiva
  * Lista as transações por Período com saldo atualizado de um usuário
  - [x] Controller
  - [x] DTOs
  - [x] Service
  - [x] Models
  - [x] Repositories (tabela e view com saldo)
  - [x] Scripts BD
- [x] Criação da requisição 'Resumo de Transações'
  * Resume as transações por Período, Tipo e Categoria de um usuário
  * Resume as transações por Período, Tipo e Método de um usuário
  - [x] Atualização Controller
  - [x] Criação de DTOs necessários
  - [x] Atualização Service
- [x] Criação da requisição 'Importação de transações por planilha'
  * Implemnta o cadastro de transações por importação de planilha
  - [x] Atualização Controller
  - [x] Criação de DTOs necessários
  - [x] Atualização Service

### 🗃️Collection Postman
- [trilha-java-intermediate.postman_collection.json](arquivos/trilha-java-intermediate.postman_collection_ant.json)