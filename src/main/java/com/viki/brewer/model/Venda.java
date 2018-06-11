package com.viki.brewer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "venda")
@DynamicUpdate
@EqualsAndHashCode(of = "codigo")
@Getter @Setter @ToString
public class Venda implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@Column(name = "data_criacao", updatable = false)
	private LocalDateTime dataCriacao;
	
	@Column(name = "valor_frete")
	private BigDecimal valorFrete = BigDecimal.ZERO;
	
	@Column(name = "valor_desconto")
	private BigDecimal valorDesconto = BigDecimal.ZERO;
	
	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	@NotNull(message = "Status é obrigatório")
	@Enumerated(EnumType.STRING)
	private StatusVenda status = StatusVenda.ORCAMENTO;
	
	private String observacao;
	
	@Column(name = "data_entrega")
	private LocalDateTime dataHoraEntrega;
	
	@ManyToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "codigo_usuario", updatable = false)
	private Usuario usuario;
	
	@OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemVenda> itens = new ArrayList<>();
	
	@Transient
	private String uuid = UUID.randomUUID().toString();
	
	@Transient
	private LocalDate dataEntrega;
	
	@Transient
	private LocalTime horarioEntrega;
	
	public boolean isNova() {
		return this.codigo == null;
	}
	
	public boolean isEdicaoPermitida() {
		return isOrcamento();
	}
	
	public boolean isEdicaoBloqueada() {
		return !isEdicaoPermitida();
	}
	
	public boolean isOrcamento() {
		return this.status.equals(StatusVenda.ORCAMENTO);
	}
	
	public boolean isEmitida() {
		return this.status.equals(StatusVenda.EMITIDA);
	}
	
	public boolean isCancelada() {
		return this.status.equals(StatusVenda.CANCELADA);
	}

	public boolean isCancelarPermitido() {
		return !this.isNova() && !isCancelada();
	}
	
	@PrePersist
	private void prePersit() {
		this.dataCriacao = LocalDateTime.now();
	}

	@PostLoad
	private void postLoad() {
		if (this.dataHoraEntrega != null) {
			this.dataEntrega = this.dataHoraEntrega.toLocalDate();
			this.horarioEntrega = this.dataHoraEntrega.toLocalTime();
		}
	}
	
	public void adicionarItens(List<ItemVenda> itens) {
		this.itens = itens;
		this.itens.forEach(i -> i.setVenda(this));
	}
	
	public boolean isListaItensVazia() {
		return this.itens.isEmpty();
	}

	public BigDecimal getValorTotalItens() {
		return getItens().stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal calcularValorTotal() {
		BigDecimal totalVenda = getValorTotalItens();
		return totalVenda.add(getValorFrete())
				.subtract(getValorDesconto());
	}
	
	public Long getDiasCriacao() {
		LocalDate inicio = dataCriacao != null ? dataCriacao.toLocalDate() : LocalDate.now();
		return ChronoUnit.DAYS.between(inicio, LocalDate.now());
	}
	
}
