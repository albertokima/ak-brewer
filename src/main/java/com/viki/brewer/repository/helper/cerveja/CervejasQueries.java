package com.viki.brewer.repository.helper.cerveja;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viki.brewer.dto.CervejaDTO;
import com.viki.brewer.model.Cerveja;
import com.viki.brewer.repository.filter.CervejaFilter;

public interface CervejasQueries {
	
	public Page<Cerveja> pesquisar(CervejaFilter filtro, Pageable pageable);
	
	public List<CervejaDTO> porSkuOuNome(String skuOuNome);

	public BigDecimal valorTotalEstoque();

	public Long quantidadeItensEstoque();

}
