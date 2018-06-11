CREATE TABLE estilo (
	codigo serial NOT NULL,
	nome character varying(50) NOT NULL,
	CONSTRAINT estilo_pkey PRIMARY KEY (codigo)
);

CREATE TABLE cerveja (
	codigo serial NOT NULL,
	sku character varying(50) NOT NULL,
	nome character varying(80) NOT NULL,
	descricao text NOT NULL,
	valor numeric(10, 2) NOT NULL,
	teor_alcoolico numeric(10, 2) NOT NULL,
	comissao numeric(10, 2) NOT NULL,
	sabor character varying(50) NOT NULL,
	origem character varying(50) NOT NULL,
	quantidade_estoque integer NOT NULL,
	codigo_estilo bigint NOT NULL,
	CONSTRAINT cerveja_pkey PRIMARY KEY(codigo),
	CONSTRAINT estilo_fkey FOREIGN KEY (codigo_estilo) REFERENCES estilo(codigo)
);

INSERT INTO estilo VALUES (1, 'Amber Lager');
INSERT INTO estilo VALUES (2, 'Dark Lager');
INSERT INTO estilo VALUES (3, 'Pale Lager');
INSERT INTO estilo VALUES (4, 'Pilsner');