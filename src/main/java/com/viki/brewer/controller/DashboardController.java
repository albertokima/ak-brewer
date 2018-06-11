package com.viki.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.viki.brewer.repository.Cervejas;
import com.viki.brewer.repository.Clientes;
import com.viki.brewer.repository.Vendas;

@Controller
public class DashboardController {
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private Clientes clientes;
	
	@GetMapping("/")
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		mv.addObject("valorEstoque", cervejas.valorTotalEstoque());
		mv.addObject("itensEstoque", cervejas.quantidadeItensEstoque());
		mv.addObject("totalClientes", clientes.count());
		
		mv.addObject("vendasNoAno", vendas.totalVendasNoAno());
		mv.addObject("vendasNoMes", vendas.totalVendasNoMes());
		mv.addObject("ticketMedio", vendas.totalTicketMedioNoAno());
		
		return mv;
	}

}
