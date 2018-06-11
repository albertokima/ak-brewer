package com.viki.brewer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Estilo;

@Component
public class EstiloConverter implements Converter<String, Estilo> {

	@Override
	public Estilo convert(String source) {
		if (!StringUtils.isEmpty(source)) {
			Estilo estilo = new Estilo();
			estilo.setCodigo(Long.valueOf(source));
			return estilo;
		}
		return null;
	}

}
