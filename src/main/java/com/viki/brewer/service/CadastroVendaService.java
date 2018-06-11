package com.viki.brewer.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viki.brewer.model.StatusVenda;
import com.viki.brewer.model.Venda;
import com.viki.brewer.repository.Vendas;
import com.viki.brewer.service.event.venda.VendaSalvaEvent;
import com.viki.brewer.service.exception.ImpossivelCancelarVendaException;

@Service
public class CadastroVendaService {
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private ApplicationEventPublisher publisher;

	@Transactional
	public Venda salvar(Venda venda) {
		if (!venda.isNova()) {
			Optional<Venda> vendaExistente = vendas.findById(venda.getCodigo());
			if (vendaExistente.isPresent() && vendaExistente.get().isEdicaoBloqueada()) {
				throw new RuntimeException("Não é permitido realizar mais alterações");
			}
		}
		
		if (venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(
					LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega()));
		}
		
		return vendas.saveAndFlush(venda);
	}

	@Transactional
	public Venda emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		venda = salvar(venda);
		
		publisher.publishEvent(new VendaSalvaEvent(venda));
		return venda;
	}

	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')")
	@Transactional
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.getOne(venda.getCodigo());
		if (vendaExistente.isEmitida() && vendaExistente.getDiasCriacao() > 10L) {
			throw new ImpossivelCancelarVendaException("Venda realizada há mais de 10 dias. Não é possível 'Cancelar'");
		}
		
		boolean recuperaEstoque = vendaExistente.isEmitida();
		
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		vendas.save(vendaExistente);
		
		if (recuperaEstoque) {
			publisher.publishEvent(new VendaSalvaEvent(vendaExistente));
		}
	}
	
}
