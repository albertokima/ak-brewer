package com.viki.brewer.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @ToString
public class Endereco {

    private String logradouro;
    
    private String numero;
    
    private String complemento;
    
    private String bairro;

    private String cep;
    
    @ManyToOne
    @JoinColumn(name = "codigo_cidade")
    private Cidade cidade;
    
    @Transient
    private Estado estado;
	
    @Transient
    private String erroCepInvalido;
    
    @Transient
    public String getCidadeEstadoSigla() {
    	if (this.cidade != null) {
    		return this.cidade.getNome() + " / " + this.cidade.getEstado().getSigla();
    	}
    	return null;
    }

    public void carregaEstado() {
    	this.estado = this.cidade == null ? null : this.cidade.getEstado();
    }
    
}
