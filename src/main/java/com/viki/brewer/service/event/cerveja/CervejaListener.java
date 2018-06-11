package com.viki.brewer.service.event.cerveja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.viki.brewer.storage.FotoStorage;

@Component
public class CervejaListener {
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@EventListener(condition = "#evento.temFoto() and #evento.novaFoto")
	public void cervejaSalva(CervejaSalvaEvent evento) {
		String nome = evento.getCerveja().getFoto();
		fotoStorage.salvarFoto(nome);
	}

	@EventListener(condition = "#evento.apagarFotoAnterior()")
	public void apagarFoto(CervejaSalvaEvent evento) {
		String nome = evento.getCerveja().getFotoAnterior();
		fotoStorage.apagarFoto(nome);
	}
	
}
