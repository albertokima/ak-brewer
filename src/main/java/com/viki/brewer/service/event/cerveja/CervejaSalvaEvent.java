package com.viki.brewer.service.event.cerveja;

import org.springframework.util.StringUtils;

import com.viki.brewer.model.Cerveja;

public class CervejaSalvaEvent {

	private Cerveja cerveja;

	public CervejaSalvaEvent(Cerveja cerveja) {
		this.cerveja = cerveja;
	}
	
	public Cerveja getCerveja() {
		return cerveja;
	}
	
	public boolean temFoto() {
		return !StringUtils.isEmpty(cerveja.getFoto());
	}
	
	public boolean isNovaFoto() {
		return this.cerveja.isNovaFoto();
	}
	
	public boolean apagarFotoAnterior() {
		return this.cerveja.getFotoAnterior() != null 
				&& (naoTemFoto() || mudouFoto());
	}
	
	private boolean naoTemFoto() {
		return !this.cerveja.temFoto();
	}
	
	private boolean mudouFoto() {
		return isNovaFoto() && !this.cerveja.getFotoAnterior().equals(this.cerveja.getFoto()); 
	}
	
}
