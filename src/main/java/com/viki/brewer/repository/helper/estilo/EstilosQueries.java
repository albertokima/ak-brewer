package com.viki.brewer.repository.helper.estilo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viki.brewer.model.Estilo;
import com.viki.brewer.repository.filter.EstiloFilter;

public interface EstilosQueries {
	
	public Page<Estilo> pesquisar(EstiloFilter filtro, Pageable pageable);

}
