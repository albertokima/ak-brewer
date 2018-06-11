package com.viki.brewer.repository.filter;

import java.util.List;

import com.viki.brewer.model.Grupo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class UsuarioFilter {

	private String nome;
	private String email;
	private List<Grupo> grupos;
	private Boolean status = Boolean.TRUE;
	
}
