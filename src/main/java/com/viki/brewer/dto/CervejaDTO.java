package com.viki.brewer.dto;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.viki.brewer.model.Origem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CervejaDTO {

	private Long codigo;
	private String sku;
	private String nome;
	private String origem;
	private BigDecimal valor;
	private String foto;
	private String urlThumbnailFoto;

	public CervejaDTO(Long codigo, String sku, String nome, Origem origem, BigDecimal valor, String foto) {
		super();
		this.codigo = codigo;
		this.sku = sku;
		this.nome = nome;
		this.origem = origem.getDescricao();
		this.valor = valor;
		this.foto = StringUtils.isEmpty(foto) ? "cerveja-mock.png" : foto;
	}
	
}
