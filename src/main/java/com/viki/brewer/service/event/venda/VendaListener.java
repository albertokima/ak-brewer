package com.viki.brewer.service.event.venda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.viki.brewer.model.Cerveja;
import com.viki.brewer.repository.Cervejas;

@Component
public class VendaListener {

	@Autowired
	private Cervejas cervejas;
	
	@EventListener(condition = "#evento.emitida")
	public void vendaEmitida(VendaSalvaEvent evento) {
		evento.getVenda().getItens().forEach(i -> {
			atualizaEstoqueCerveja(i.getCerveja().getCodigo(), i.getQuantidade() * -1);
		});
	}
	
	@EventListener(condition = "#evento.cancelada")
	public void vendaCancelada(VendaSalvaEvent evento) {
		evento.getVenda().getItens().forEach(i -> {
			atualizaEstoqueCerveja(i.getCerveja().getCodigo(), i.getQuantidade());
		});
	}

	private void atualizaEstoqueCerveja(Long codigo, Integer quantidade) {
		Cerveja cerveja = cervejas.getOne(codigo);
		cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() + quantidade);
		cervejas.save(cerveja);
	}

}
