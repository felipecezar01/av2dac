# Sistema de Gestão de Loja de Carros

Esse é um projeto desenvolvido durante o curso de Análise e Desenvolvimento de Sistemas, na disciplina de Desenvolvimento de Aplicações Corporativas. O objetivo foi aplicar conceitos de desenvolvimento backend e práticas de mercado, usando um sistema corporativo em Java.

## 🛠️ Ferramentas Utilizadas

Para o desenvolvimento do projeto, utilizamos as seguintes tecnologias:

- **Git e GitHub**: Versionamento do código;
- **Postman e Swagger**: Testes e documentação da API;
- **PostgreSQL**: Banco de dados relacional;
- **Java**: Linguagem de programação principal;
- **IntelliJ IDEA**: Ambiente de desenvolvimento integrado (IDE);
- **Arquitetura MVC**: Organização do código seguindo a arquitetura Modelo-Visão-Controlador.

[Imagem das ferramentas utilizadas]

## 📖 Sobre o Projeto

Este projeto consiste na criação de uma API para o sistema de uma loja de carros, com foco na construção do backend. O sistema é composto por duas tabelas principais (Carros e Usuários) e implementa funcionalidades de autenticação e autorização, além de criptografia. O sistema permite diferentes níveis de acesso com base no tipo de usuário: 

- **Usuários**: possuem permissões limitadas;
- **Admins**: têm acesso total às funcionalidades do sistema.

[Imagem das tabelas do banco de dados](#estrutura-do-banco-de-dados)

## 🔍 Funcionalidades

O sistema conta com as seguintes funcionalidades:

### Autenticação

- A aplicação só permite acesso a usuários autenticados.

### Permissões do Usuário

Uma vez autenticado, o usuário pode:

1. **Registrar-se e Logar**;
2. **Visualizar todos os carros** com filtros de busca;
3. **Atualizar suas próprias informações** de perfil;
4. **Gerar um QR Code** com suas informações pessoais.

### Permissões do Admin

O administrador tem permissões mais amplas, incluindo:

1. **Registrar-se e Logar**;
2. **Gerenciar o catálogo de carros** (Criar, Atualizar, Deletar e Listar);
3. **Atualizar informações de outros usuários**;
4. **Gerar QR Codes** com informações de veículos;
5. **Gerar relatórios em PDF** contendo a lista de carros e suas especificações completas.

## 🗂️ Estrutura do Banco de Dados

### Tabela Usuários

| Campo         | Tipo        | Descrição                       |
|---------------|-------------|----------------------------------|
| id            | Integer     | Identificador único do usuário  |
| email         | String      | E-mail do usuário               |
| name          | String      | Nome completo                   |
| password      | String      | Senha criptografada             |
| role          | String      | Tipo de usuário (user/admin)    |
| username      | String      | Nome de usuário único           |

### Tabela Carros

| Campo         | Tipo        | Descrição                           |
|---------------|-------------|--------------------------------------|
| id            | Integer     | Identificador único do carro        |
| brand         | String      | Marca do carro                      |
| city          | String      | Localização do carro                |
| license_plate | String      | Placa do carro                      |
| model         | String      | Modelo do carro                     |
| name          | String      | Nome do carro                       |
| price         | Decimal     | Preço do carro                      |
| year          | Integer     | Ano de fabricação                   |
| color         | String      | Cor do carro                        |
| kilometers    | Integer     | Quilometragem do carro              |
