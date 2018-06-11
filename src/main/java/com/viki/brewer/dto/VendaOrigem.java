package com.viki.brewer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class VendaOrigem {

	private Integer ordem;
	private String mes;
	private Integer totalNacional;
	private Integer totalInternacional;
	
}
