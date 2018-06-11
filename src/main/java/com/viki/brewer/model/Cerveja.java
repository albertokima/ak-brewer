package com.viki.brewer.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.StringUtils;

import com.viki.brewer.repository.listener.CervejaEntityListener;
import com.viki.brewer.validation.SKU;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@EntityListeners(CervejaEntityListener.class)
@Table(name = "cerveja")
@DynamicUpdate
@EqualsAndHashCode(of = {"codigo"})
@Getter @Setter @ToString
public class Cerveja implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@SKU
	@NotBlank(message = "SKU é obrigatório")
	private String sku;

	@NotBlank(message = "Nome é obrigatório")
	@Size(max=30, message = "Nome deve ter entre 1 e 30 caracteres")
	private String nome;

	@NotBlank(message = "Descrição é obrigatório")
	@Size(max=100, message = "Descrição deve ter entre 1 e 100 caracteres")
	private String descricao;
	
	@NotNull(message = "Valor é obrigatório")
	@DecimalMin(value = "0.10", message = "Valor de ser maior que R$ 0,09")
	@DecimalMax(value = "999.99", message = "Valor deve ser menor que R$ 1.000,00")
	private BigDecimal valor = BigDecimal.ZERO;
	
	@NotNull(message = "Teor Alcoólico é obrigatório")
	@DecimalMin(value = "1.00", message = "Teor Alcoólico de ser maior que 0,99%")
	@DecimalMax(value = "99.99", message = "Teor Alcoólico de ser menor que 100%")
	@Column(name = "teor_alcoolico")
	private BigDecimal teorAlcoolico = BigDecimal.ZERO;
	
	@NotNull(message = "Comissão é obrigatória")
	@Range(min = 5, max = 30, message = " Comissão deve ser de 5% a 30%")
	private BigDecimal comissao = BigDecimal.ZERO;
	
	@Max(value = 9999, message = "Quantidade de estoque deve ser menor que 10.000")
	@Column(name = "quantidade_estoque")
	private Integer quantidadeEstoque = 0;
	
	@NotNull(message = "Origem é obrigatória")
	@Enumerated(EnumType.STRING)
	private Origem origem = Origem.NACIONAL;
	
	@NotNull(message = "Sabor é obrigatório")
	@Enumerated(EnumType.STRING)
	private Sabor sabor;
	
	@NotNull(message = "Estilo é obrigatório")
	@ManyToOne
	@JoinColumn(name = "codigo_estilo")
	private Estilo estilo;
	
	private String foto;
	
	@Column(name = "content_type")
	private String contentType;
	
	@Transient
	private boolean novaFoto;
	
	@Transient
	private String fotoAnterior;
	
	@Transient
	private String urlFoto;
	
	@Transient
	private String urlThumbnailFoto;
	
	@PrePersist @PreUpdate
	private void onPersistUpdate() {
		this.sku = this.sku.toUpperCase();
	}
	
	public String getFotoOuMock() {
		return this.temFoto() ? this.foto : "cerveja-mock.png";
	}
	
	public boolean temFoto() {
		return !StringUtils.isEmpty(this.foto);
	}

	public boolean isNovo() {
		return this.codigo == null;
	}
	
}
