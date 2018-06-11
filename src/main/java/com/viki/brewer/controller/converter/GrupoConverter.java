package com.viki.brewer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Grupo;

@Component
public class GrupoConverter implements Converter<String, Grupo> {

	@Override
	public Grupo convert(String source) {
		if (!StringUtils.isEmpty(source)) {
			Grupo grupo = new Grupo();
			grupo.setCodigo(Long.valueOf(source));
			return grupo;
		}
		return null;
	}

}
