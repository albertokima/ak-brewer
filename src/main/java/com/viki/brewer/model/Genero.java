package com.viki.brewer.model;

import lombok.Getter;

@Getter
public enum Genero {

	M("Masculino","https://randomuser.me/api/portraits/men/"),
	F("Feminino","https://randomuser.me/api/portraits/women/"),
	O("Outro","https://randomuser.me/api/portraits/men/undefined.jpg");
	
	private String descricao;
	private String urlFoto;
	
	private Genero(String descricao, String urlFoto) {
		this.descricao = descricao;
		this.urlFoto = urlFoto;
	}

}
