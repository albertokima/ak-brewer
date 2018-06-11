package com.viki.brewer.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.group.GroupSequenceProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viki.brewer.model.validation.ClienteGroupSequenceProvider;
import com.viki.brewer.model.validation.groups.CnpjGroup;
import com.viki.brewer.model.validation.groups.CpfGroup;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cliente")
@GroupSequenceProvider(ClienteGroupSequenceProvider.class)
@EqualsAndHashCode(of = "codigo")
@Getter @Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotNull(message = "Tipo pessoa é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa")
    private TipoPessoa tipoPessoa = TipoPessoa.JURIDICA;
    
    @NotBlank(message = "CPF / CNPJ é obrigatório")
    @CPF(groups = CpfGroup.class)
    @CNPJ(groups = CnpjGroup.class)
    @Column(name = "cpf_cnpj")
    private String cpfCnpj;
    
    private String telefone;
    
    @Email(message = "E-mail é inválido!")
    private String email;
    
    @Embedded
    @JsonIgnore
    private Endereco endereco;
    
    @PrePersist @PreUpdate
    private void prePersistUpdate() {
    	this.cpfCnpj = TipoPessoa.removerFormatacao(this.cpfCnpj);
    }

    @PostLoad
    private void postLoad() {
    	this.cpfCnpj = this.tipoPessoa.formatar(this.cpfCnpj);
    	this.endereco.carregaEstado();
    }
    
    public String getCpfCnpjSemFormatacao() {
    	return TipoPessoa.removerFormatacao(this.cpfCnpj);
    }

    public boolean isNovo() {
    	return this.codigo == null;
    }
    
}
