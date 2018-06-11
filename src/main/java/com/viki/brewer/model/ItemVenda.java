package com.viki.brewer.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_venda")
@EqualsAndHashCode(of = "codigo")
@Getter @Setter
public class ItemVenda implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotNull(message = "Quantidade é obrigatório")
	private Integer quantidade;
	
	@NotNull(message = "Valor unitário é obrigatório")
	@Column(name = "valor_unitario")
	private BigDecimal valorUnitario;
	
	@NotNull(message = "Informe a cerveja")
	@ManyToOne
	@JoinColumn(name = "codigo_cerveja")
	private Cerveja cerveja;
	
	@NotNull(message = "Venda é obrigatório")
	@ManyToOne
	@JoinColumn(name = "codigo_venda")
	private Venda venda;
	
	public BigDecimal getValorTotal() {
		return this.valorUnitario.multiply(new BigDecimal(this.quantidade));
	}

}
