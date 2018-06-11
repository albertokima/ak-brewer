package com.viki.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viki.brewer.dto.VendaMes;
import com.viki.brewer.dto.VendaOrigem;
import com.viki.brewer.model.Venda;
import com.viki.brewer.repository.filter.VendaFilter;

public interface VendasQueries {
	
	public Page<Venda> pesquisar(VendaFilter filtro, Pageable pageable); 
	
	public BigDecimal totalVendasNoAno();

	public BigDecimal totalVendasNoMes();

	public BigDecimal totalTicketMedioNoAno();
	
	public List<VendaMes> graficoTotalVendasPorMes();

	public List<VendaOrigem> graficoTotalVendasPorOrigem();

}
