package com.viki.brewer.repository.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.viki.brewer.model.StatusVenda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class VendaFilter {

	private Long codigo;
	private StatusVenda status;
	private LocalDate dataCriacaoDe;
	private LocalDate dataCriacaoAte;
	private BigDecimal valorTotalDe = BigDecimal.ZERO;
	private BigDecimal valorTotalAte;
	private String nomeCliente;
	private String cpfCnpjCliente;
	
}
