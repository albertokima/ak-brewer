package com.viki.brewer.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="estado")
@EqualsAndHashCode(of="codigo")
@Getter @Setter
public class Estado {

	@Id
	private Long codigo;
	
	@NotEmpty
	@Size(min=3, max=50)
	private String nome;
	
	@NotEmpty
	@Size(min=2, max=2)
	private String sigla;
	
	@Transient
	public String getSiglaNome() {
		return this.sigla +  " - " + this.nome;
	}
	
}
