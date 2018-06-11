CREATE TABLE cliente (
    codigo serial NOT NULL,
    nome character varying(80) NOT NULL,
    tipo_pessoa character varying(15) NOT NULL,
    cpf_cnpj character varying(30),
    telefone character varying(20),
    email character varying(50) NOT NULL,
    logradouro character varying(50),
    numero character varying(15),
    complemento character varying(20),
    cep character varying(15),
    codigo_cidade bigint,
    CONSTRAINT cliente_pk PRIMARY KEY (codigo),
    CONSTRAINT cidade_fk FOREIGN KEY (codigo_cidade) REFERENCES cidade(codigo)
);