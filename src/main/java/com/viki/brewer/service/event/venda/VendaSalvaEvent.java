package com.viki.brewer.service.event.venda;

import com.viki.brewer.model.Venda;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class VendaSalvaEvent {

	private Venda venda;
	
	public boolean isEmitida() {
		return this.venda.isEmitida();
	}
	
	public boolean isCancelada() {
		return this.venda.isCancelada();
	}

}
