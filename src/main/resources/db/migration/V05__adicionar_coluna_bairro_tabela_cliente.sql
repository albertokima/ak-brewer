alter table cliente add column bairro character varying(50);
alter table cliente alter column cpf_cnpj SET NOT NULL;