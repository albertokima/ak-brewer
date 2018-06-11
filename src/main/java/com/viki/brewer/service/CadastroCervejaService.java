package com.viki.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viki.brewer.model.Cerveja;
import com.viki.brewer.repository.Cervejas;
import com.viki.brewer.service.event.cerveja.CervejaSalvaEvent;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.viki.brewer.storage.FotoStorage;

@Service
public class CadastroCervejaService {
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja) {
		if (!cerveja.isNovo()) {
			Cerveja cervejaAntes = cervejas.getOne(cerveja.getCodigo());
			if (cervejaAntes.temFoto()) {
				cerveja.setFotoAnterior(cervejaAntes.getFoto());
			}
		}
		
		cervejas.save(cerveja);
		
		publisher.publishEvent(new CervejaSalvaEvent(cerveja));
	}

	@Transactional
	public void excluir(Cerveja cerveja) {
		String foto = cerveja.getFoto();
		try {
			cervejas.delete(cerveja);
			cervejas.flush();
			fotoStorage.apagarFoto(foto);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Já foram realizadas vendas com esta cerveja. Não foi possível excluir.");
		}
	}

}
