package com.viki.brewer.repository.filter;

import java.math.BigDecimal;

import com.viki.brewer.model.Estilo;
import com.viki.brewer.model.Origem;
import com.viki.brewer.model.Sabor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CervejaFilter {

	private String sku;
	private String nome;
	private Origem origem;
	private Estilo estilo;
	private Sabor sabor;
	private BigDecimal valorDe = BigDecimal.ZERO;
	private BigDecimal valorAte;
	
}
