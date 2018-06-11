package com.viki.brewer.session;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.viki.brewer.model.Cerveja;
import com.viki.brewer.session.TabelaItensVenda;

public class TabelaItensVendaTest {

	private TabelaItensVenda tabelaItensVenda;
	
	@Before
	public void setup() {
		this.tabelaItensVenda = new TabelaItensVenda(UUID.randomUUID().toString());
	}
	
	@Test
	public void deveCalcularValorTotalSemItens() throws Exception {
		assertEquals(BigDecimal.ZERO, this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveCalcularValorTotalComUmItem() throws Exception {
		Cerveja c = new Cerveja();
		BigDecimal v = new BigDecimal("8.80");
		c.setValor(v);
		
		this.tabelaItensVenda.adicionarItem(c, 1);
		
		assertEquals(v, this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveCalcularValorTotalComVariosItens() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		BigDecimal v1 = new BigDecimal("8.80");
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		c1.setCodigo(2L);
		BigDecimal v2 = new BigDecimal("4.89");
		c2.setValor(v2);

		this.tabelaItensVenda.adicionarItem(c1, 1);
		this.tabelaItensVenda.adicionarItem(c2, 1);
		
		assertEquals(v1.add(v2), this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveManterQuantidadeTotalItensAoIncluirMesmoItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		BigDecimal v1 = new BigDecimal("5");
		c1.setValor(v1);

		this.tabelaItensVenda.adicionarItem(c1, 1);
		this.tabelaItensVenda.adicionarItem(c1, 1);
		
		assertEquals(1, this.tabelaItensVenda.getTotal());
		assertEquals(BigDecimal.TEN, this.tabelaItensVenda.getValorTotal());
	}

	@Test
	public void deveAlterarQuantidadeDoMesmoItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		BigDecimal v1 = new BigDecimal("4.50");
		c1.setValor(v1);
		
		this.tabelaItensVenda.adicionarItem(c1, 1);
		this.tabelaItensVenda.alterarQuantidadeItem(c1, 3);

		assertEquals(1, this.tabelaItensVenda.getTotal());
		assertEquals(new BigDecimal("13.50"), this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveRemoverItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		BigDecimal v1 = new BigDecimal("8.80");
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		c1.setCodigo(2L);
		BigDecimal v2 = new BigDecimal("4.89");
		c2.setValor(v2);

		this.tabelaItensVenda.adicionarItem(c1, 2);
		this.tabelaItensVenda.adicionarItem(c2, 1);
		this.tabelaItensVenda.removerItem(c1);
		
		assertEquals(1, this.tabelaItensVenda.getTotal());
		assertEquals(new BigDecimal("4.89"), this.tabelaItensVenda.getValorTotal());
	}
	
}
