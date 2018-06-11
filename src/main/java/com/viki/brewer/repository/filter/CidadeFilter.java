package com.viki.brewer.repository.filter;

import com.viki.brewer.model.Estado;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CidadeFilter {

	private Estado estado;
	private String nomeCidade;
	
}
