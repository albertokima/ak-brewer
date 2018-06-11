package com.viki.brewer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;

import com.viki.brewer.validation.AtributoConfirmacao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@DynamicUpdate
@EqualsAndHashCode(of = "codigo")
@Getter @Setter
@AtributoConfirmacao(atributo = "senha", atributoConfirmacao = "confirmacaoSenha", message = "A confirmação da senha não confere")
public class Usuario implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotBlank(message = "Nome é obrigatório")
	private String nome;
	
	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;
	
	private String senha;
	
	private Boolean ativo = true;
	
	@Column(name=  "data_nascimento")
	private LocalDate dataNascimento;
	
	@Size(min = 1, message = "Selecione pelo menos um Grupo")
	@ManyToMany
	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name = "codigo_usuario"), 
		inverseJoinColumns = @JoinColumn(name = "codigo_grupo"))
	private List<Grupo> grupos;
	
	@NotNull(message = "Gênero é obrigatório")
	@Enumerated(EnumType.STRING)
	private Genero genero;
	
	@Transient
	private String confirmacaoSenha;
	
	@PreUpdate
	private void PreUpdate() {
		this.confirmacaoSenha = senha;
	}
	
	public boolean isNovo() {
		return this.codigo == null;
	}
	
	public String getStatus() {
		return this.ativo ? "Ativo" : "Inativo";
	}
	
	public String getFotoPerfil() {
		if (this.codigo != null) {
			if (this.genero.equals(Genero.O)) {
				return this.genero.getUrlFoto();
			}
			return this.genero.getUrlFoto() + this.codigo + ".jpg";
		}
		return "https://randomuser.me/api/portraits/men/undefined.jpg";
	}

}
