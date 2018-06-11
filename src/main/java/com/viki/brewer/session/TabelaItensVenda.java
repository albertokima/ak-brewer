package com.viki.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.viki.brewer.model.Cerveja;
import com.viki.brewer.model.ItemVenda;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of ="uuid")
class TabelaItensVenda {

	private String uuid;
	
	private List<ItemVenda> itensVenda = new ArrayList<>();
	
	public TabelaItensVenda(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getValorTotal() {
		return itensVenda.stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		ItemVenda itemVenda = null;
		if (itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			this.itensVenda.add(0, itemVenda);
		}
	}
	
	public void removerItem(Cerveja cerveja) {
		int indice = IntStream.range(0, itensVenda.size())
				.filter(i -> itensVenda.get(i).getCerveja().equals(cerveja))
				.findAny().getAsInt();
		itensVenda.remove(indice);
	}
	
	public void alterarQuantidadeItem(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
	}
	
	public int getTotal() {
		return this.itensVenda.size();
	}

	public List<ItemVenda> getItens() {
		return this.itensVenda;
	}
	
	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		return itensVenda.stream()
				.filter(i -> i.getCerveja().equals(cerveja)).findAny();
	}

	public String getUuid() {
		return uuid;
	}
	
}
