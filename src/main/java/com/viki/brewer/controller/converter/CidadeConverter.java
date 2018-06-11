package com.viki.brewer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Cidade;

@Component
public class CidadeConverter implements Converter<String, Cidade> {

	@Override
	public Cidade convert(String source) {
		if (!StringUtils.isEmpty(source)) {
			Cidade cidade = new Cidade();
			cidade.setCodigo(Long.valueOf(source));
			return cidade;
		}
		return null;
	}

}
