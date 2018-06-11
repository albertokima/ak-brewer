CREATE TABLE usuario (
    codigo serial NOT NULL,
    nome character varying(50) NOT NULL,
    email character varying(50) NOT NULL,
    senha character varying(120) NOT NULL,
    ativo BOOLEAN DEFAULT true,
    data_nascimento DATE,
    CONSTRAINT usuario_pk PRIMARY KEY (codigo)
);

CREATE TABLE grupo (
    codigo bigint NOT NULL,
    nome character varying(50) NOT NULL,
    CONSTRAINT grupo_pk PRIMARY KEY (codigo)
);

CREATE TABLE permissao (
    codigo bigint NOT NULL,
    nome character varying(50) NOT NULL,
	CONSTRAINT permissao_pk PRIMARY KEY (codigo)
);

CREATE TABLE usuario_grupo (
    codigo_usuario bigint NOT NULL,
    codigo_grupo bigint NOT NULL,
    CONSTRAINT usuario_grupo_pk PRIMARY KEY (codigo_usuario, codigo_grupo),
    CONSTRAINT usuario_fk FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo),
    CONSTRAINT grupo_fk FOREIGN KEY (codigo_grupo) REFERENCES grupo(codigo)
);

CREATE TABLE grupo_permissao (
    codigo_grupo bigint NOT NULL,
    codigo_permissao bigint NOT NULL,
    CONSTRAINT grupo_permissao_fk PRIMARY KEY (codigo_grupo, codigo_permissao),
    CONSTRAINT grupo1_fk FOREIGN KEY (codigo_grupo) REFERENCES grupo(codigo),
    CONSTRAINT permissao_fk FOREIGN KEY (codigo_permissao) REFERENCES permissao(codigo)
);