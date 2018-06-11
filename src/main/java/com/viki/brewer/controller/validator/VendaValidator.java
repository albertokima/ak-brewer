package com.viki.brewer.controller.validator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.viki.brewer.model.Venda;

@Component
public class VendaValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cliente.codigo", "", "Informe o cliente a partir da pesquisa");
		//ValidationUtils.rejectIfEmpty(errors, "usuario.codigo", "", "O usuário da venda não foi informado");
		
		Venda venda = (Venda)target;
		validacaoDataHorarioEntrega(errors, venda);
		validarSeInformouAlgumItem(errors, venda);
		validarSeValorTotalEstaNegativo(errors, venda);
	}

	private void validarSeValorTotalEstaNegativo(Errors errors, Venda venda) {
		if (venda.calcularValorTotal().compareTo(BigDecimal.ZERO)<0) {
			errors.reject("", "O valor total da venda não pode ser negativo");
		}
	}

	private void validarSeInformouAlgumItem(Errors errors, Venda venda) {
		if (venda.isListaItensVazia()) {
			errors.reject("", "Informe pelo menos um item para a venda");
		}
	}

	private void validacaoDataHorarioEntrega(Errors errors, Venda venda) {
		if (venda.getDataEntrega() != null && venda.getHorarioEntrega() == null) {
			errors.rejectValue("horarioEntrega", "", "Informe o horário da entrega");
		}
		if (venda.getDataEntrega() == null && venda.getHorarioEntrega() != null) {
			errors.rejectValue("dataEntrega", "", "Informe a data da entrega");
		}
		if (venda.getDataEntrega() != null && venda.getHorarioEntrega() != null) {
			LocalDateTime dataCriacao = venda.getDataCriacao()==null ? LocalDateTime.now() : venda.getDataCriacao();
			if (dataCriacao.isAfter(LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega()))) {
				errors.rejectValue("dataEntrega", "", "Data e Horário da entrega deve ser maior que a Data da venda");
			}
		}
	}

}
