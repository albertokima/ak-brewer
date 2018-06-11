package com.viki.brewer.repository.helper.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viki.brewer.model.Cliente;
import com.viki.brewer.repository.filter.ClienteFilter;

public interface ClientesQueries {

	public Page<Cliente> pesquisar(ClienteFilter filtro, Pageable pageable);
	
}
