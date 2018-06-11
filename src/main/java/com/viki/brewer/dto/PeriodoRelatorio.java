package com.viki.brewer.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PeriodoRelatorio {

	@NotNull(message="Informe o período inicial")
	private LocalDate dataInicio;
	
	@NotNull(message="Informe o período final")
	private LocalDate dataFim;
	
}
