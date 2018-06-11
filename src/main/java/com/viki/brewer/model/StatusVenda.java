package com.viki.brewer.model;

import lombok.Getter;

@Getter
public enum StatusVenda {

	ORCAMENTO("Orçamento","label-default"),
	EMITIDA("Emitida", "label-success"),
	CANCELADA("Cancelada", "label-danger");
	
	private StatusVenda(String descricao, String labelPill) {
		this.descricao = descricao;
		this.labelPill = labelPill;
	}

	private String descricao;
	private String labelPill; 
	
}
