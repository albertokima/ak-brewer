package com.viki.brewer.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.viki.brewer.dto.PeriodoRelatorio;
import com.viki.brewer.service.RelatorioService;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {
	
	@Autowired
	private RelatorioService relatorioService;

	@GetMapping("/vendasEmitidas")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioVendasEmitidas");
		mv.addObject("periodoRelatorio", new PeriodoRelatorio());
		return mv;
	}
	
	@PostMapping("/vendasEmitidas")
	public ResponseEntity<byte[]> gerarRelatorioVendasEmitidas(@Valid PeriodoRelatorio periodoRelatorio){
		try {
			byte[] relatorio = relatorioService.gerarRelatorioVendasEmitidas(periodoRelatorio); 

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Vendas Emitidas.pdf\"");

			return ResponseEntity.ok().headers(headers).body(relatorio);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
		
	}
	
}
