CREATE TABLE venda (
    codigo serial NOT NULL,
    data_criacao timestamp without time zone NOT NULL,
    valor_frete numeric(10,2),
    valor_desconto numeric(10,2),
    valor_total numeric(10,2) NOT NULL,
    status character varying(30) NOT NULL,
    observacao character varying(200),
    data_entrega timestamp without time zone,
    codigo_cliente bigint NOT NULL,
    codigo_usuario bigint NOT NULL,
    CONSTRAINT venda_pk PRIMARY KEY (codigo),
    CONSTRAINT v_cliente_fk FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo),
    CONSTRAINT v_usuario_fk FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo)
);

CREATE TABLE item_venda (
    codigo serial NOT NULL,
    quantidade INTEGER NOT NULL,
    valor_unitario numeric(10,2) NOT NULL,
    codigo_cerveja BIGINT NOT NULL,
    codigo_venda BIGINT NOT NULL,
    CONSTRAINT item_venda_pk PRIMARY KEY (codigo),
    CONSTRAINT it_cerveja_fk FOREIGN KEY (codigo_cerveja) REFERENCES cerveja(codigo),
    CONSTRAINT it_venda_fk FOREIGN KEY (codigo_venda) REFERENCES venda(codigo)
);