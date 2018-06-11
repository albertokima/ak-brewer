package com.viki.brewer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Estado;

@Component
public class EstadoConverter implements Converter<String, Estado> {

	@Override
	public Estado convert(String source) {
		if (!StringUtils.isEmpty(source)) {
			Estado estado = new Estado();
			estado.setCodigo(Long.valueOf(source));
			return estado;
		}
		return null;
	}

}
