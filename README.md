# Projeto de Cadastro de Chaves Pix

Este projeto implementa um sistema de cadastro de chaves Pix com validação dos tipos de chaves (celular, e-mail, CPF), utilizando um banco de dados Postgress rodando em container Docker.

## Tecnologias Utilizadas

- **Java 17 **
- **Spring Boot**
- **PostgreSQL** (rodando em container Docker)
- **Docker**
- **JPA/Hibernate**
- **Maven** 

## Funcionalidades

- Cadastro de chaves Pix (até 5 para pessoa física)
- Alteração e inativação de chaves cadastradas
- Busca por id
- Busca por filtros combinados
- Validações personalizadas para cada tipo de chave (celular, e-mail, CPF)

---

## Pré-requisitos

Certifique-se de que você tem os seguintes softwares instalados em sua máquina:

- **Docker** (https://www.docker.com/get-started)
- **Java 17** 
- **Maven** (https://maven.apache.org/install.html)

---

## Como rodar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/anazimerer/pixkeymanagement.git
```

### 2. Rode um banco de dados Postgress em container docker

### 3. Crie a tabela principal 
```bash
CREATE TABLE public.pix_key_register (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	"key_type" varchar(9) NOT NULL,
	key_value varchar(77) NOT NULL,
	account_type varchar(10) NOT NULL,
	agency_number int4 NOT NULL,
	account_number int8 NOT NULL,
	account_holder_first_name varchar(30) NOT NULL,
	account_holder_last_name varchar(45) NULL,
	key_registration_date date NOT NULL,
	key_inactivation_date date NULL,
	CONSTRAINT pix_key_register_account_type_check CHECK (((account_type)::text = ANY (ARRAY[('corrente'::character varying)::text, ('poupanca'::character varying)::text, ('CORRENTE'::character varying)::text, ('POUPANCA'::character varying)::text]))),
	CONSTRAINT pix_key_register_pkey PRIMARY KEY (id)
);
```

### 4. Configure as variaveis de ambiente no projeto 
```bash
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```


## API Endpoints

Aqui estão os principais endpoints da API para o cadastro de chaves Pix:

- **Cadastro de chave Pix**:
  - `POST /v1/`
  - Exemplo de request body:
    ```json
    {
      "tipoChave": "CELULAR",
      "valorChave": "+55 21 912345678",
      "tipoConta": "CORRENTE",
      "numeroAgencia": "1234",
      "numeroConta": "12345678",
      "nomeCorrentista": "João",
      "sobrenomeCorrentista": "Silva"
    }
    ```

- **Alteração de chave Pix**:
  - `PATCH /v1/`
  -  Exemplo de request body:
    ```json
    {
      "tipoConta": "CORRENTE",
      "numeroAgencia": "1234",
      "numeroConta": "12345678",
      "nomeCorrentista": "João",
      "sobrenomeCorrentista": "Silva"
    }
    ```

- **Inativação de chave Pix**:
  - `DELETE /v1/{id}`

  - Exemplo de request:
    ```
    DELETE /v1/123e4567-e89b-12d3-a456-426614174000
    ```
 
- **Consulta de chave Pix por ID**:
  - `GET /v1/{id}`
  - Exemplo de request:
    ```
    GET /v1/123e4567-e89b-12d3-a456-426614174000
    ```
  - Exemplo de response:
    ```json
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "tipoChave": "CELULAR",
      "valorChave": "+55 21 912345678",
      "tipoConta": "CORRENTE",
      "numeroAgencia": "1234",
      "numeroConta": "12345678",
      "nomeCorrentista": "João",
      "sobrenomeCorrentista": "Silva",
      "dataHoraInclusao": "2023-09-30T15:45:00"
    }
    ```

- **Consulta de chaves Pix por filtros**:
  - `GET /v1`
  - Query parameters disponíveis:
    - `tipoChave` (opcional)
    - `numeroAgencia` (opcional)
    - `numeroConta` (opcional)
    - `nomeCorrentista` (opcional)
  - Exemplo de request:
    ```
    GET /v1/?tipoChave=CELULAR&numeroAgencia=1234&numeroConta=12345678&nomeCorrentista=João
    ```
  - Exemplo de response:
    ```json
    [
      {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "tipoChave": "CELULAR",
        "valorChave": "+55 21 912345678",
        "tipoConta": "CORRENTE",
        "numeroAgencia": "1234",
        "numeroConta": "12345678",
        "nomeCorrentista": "João",
        "sobrenomeCorrentista": "Silva",
        "dataHoraInclusao": "2023-09-30T15:45:00"
      },
      {
        "id": "987e6543-e21b-98d7-a123-456712340000",
        "tipoChave": "EMAIL",
        "valorChave": "joao@email.com",
        "tipoConta": "CORRENTE",
        "numeroAgencia": "1234",
        "numeroConta": "12345678",
        "nomeCorrentista": "João",
        "sobrenomeCorrentista": "Silva",
        "dataHoraInclusao": "2023-09-29T13:20:00"
      }
    ]
    ```








