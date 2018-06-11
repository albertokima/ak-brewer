package com.viki.brewer.model;

import com.viki.brewer.model.validation.groups.CnpjGroup;
import com.viki.brewer.model.validation.groups.CpfGroup;

import lombok.Getter;

@Getter
public enum TipoPessoa {

	FISICA ("Física", "CPF", "000.000.000-00", CpfGroup.class) {
		@Override
		public String formatar(String cpfCnpj) {
			return cpfCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3-");
		}
	},
	JURIDICA ("Jurídica", "CNPJ", "00.000.000/0000-00", CnpjGroup.class) {
		@Override
		public String formatar(String cpfCnpj) {
			return cpfCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})", "$1.$2.$3/$4-");
		}
	};
	
	private String descricao;
	private String documento;
	private String mascara;
	private Class<?> grupo;
	
	private TipoPessoa(String descricao, String documento, String mascara, Class<?> grupo) {
		this.descricao = descricao;
		this.documento = documento;
		this.mascara = mascara;
		this.grupo = grupo;
	}

	public static String removerFormatacao(String cpfCnpj) {
		return cpfCnpj.replaceAll("\\.|-|/", "");
	}

	public abstract String formatar(String cpfCnpj);

}
