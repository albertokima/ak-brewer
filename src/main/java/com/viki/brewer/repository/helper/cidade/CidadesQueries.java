package com.viki.brewer.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viki.brewer.model.Cidade;
import com.viki.brewer.repository.filter.CidadeFilter;

public interface CidadesQueries {

	public Page<Cidade> pesquisar(CidadeFilter filtro, Pageable pageable);
	
}
