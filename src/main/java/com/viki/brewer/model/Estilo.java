package com.viki.brewer.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estilo")
@EqualsAndHashCode(of = {"codigo"})
@Getter @Setter
public class Estilo implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotBlank(message = "Nome é obrigatório")
	@Size(max = 15, message = "Nome deve ter no máximo 15 caracteres")
	private String nome;

	public boolean isNovo() {
		return this.codigo == null;
	}
	
}
