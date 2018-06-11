package com.viki.brewer.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "usuario_grupo")
@EqualsAndHashCode
@Getter @Setter @ToString
public class UsuarioGrupo {
	
	@EmbeddedId
	private UsuarioGrupoId id;

}
