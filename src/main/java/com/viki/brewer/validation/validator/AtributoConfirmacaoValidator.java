package com.viki.brewer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.springframework.beans.BeanWrapperImpl;

import com.viki.brewer.validation.AtributoConfirmacao;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	private String atributo;
	private String atributoConfirmacao;
	
	@Override
	public void initialize(AtributoConfirmacao constraint) {
		this.atributo = constraint.atributo();
		this.atributoConfirmacao = constraint.atributoConfirmacao();
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		BeanWrapperImpl wrapper = new BeanWrapperImpl(object);
		Object valorAtributo = wrapper.getPropertyValue(this.atributo);
		Object valorAtributoConfirmacao = wrapper.getPropertyValue(this.atributoConfirmacao);
		boolean valido = isAtributosNull(valorAtributo, valorAtributoConfirmacao) || isAtributosIguais(valorAtributo, valorAtributoConfirmacao);
		
		if (!valido) {
			context.disableDefaultConstraintViolation();
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return valido;
	}

	private boolean isAtributosIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean isAtributosNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}

}
