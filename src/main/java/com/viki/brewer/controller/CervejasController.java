package com.viki.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viki.brewer.controller.page.PageWrapper;
import com.viki.brewer.dto.CervejaDTO;
import com.viki.brewer.model.Cerveja;
import com.viki.brewer.model.Origem;
import com.viki.brewer.model.Sabor;
import com.viki.brewer.repository.Cervejas;
import com.viki.brewer.repository.Estilos;
import com.viki.brewer.repository.filter.CervejaFilter;
import com.viki.brewer.service.CadastroCervejaService;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cervejas")
public class CervejasController {

	private static final String urlCadastroCerveja  = "cerveja/CadastroCerveja";
	private static final String urlPesquisaCervejas = "cerveja/PesquisaCervejas";
	
	@Autowired
	private Estilos estilos;
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private CadastroCervejaService cadastroCervejaService;
	
	@GetMapping("/novo")
	public ModelAndView novo(Cerveja cerveja) {
		ModelAndView mv = new ModelAndView(urlCadastroCerveja);
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("sabores", Sabor.values());
		mv.addObject("origens", Origem.values());
		return mv;
	}
	
	@PostMapping({ "/novo", "{\\d+}" })
	public ModelAndView salvar(@Valid Cerveja cerveja, BindingResult result, Model model,  RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return novo(cerveja);
		}

		cadastroCervejaService.salvar(cerveja);

		redirect.addFlashAttribute("mensagem", "Cadastro efetuado com sucesso!");
		return new ModelAndView("redirect:/cervejas/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult result, 
			@PageableDefault(size=5) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(urlPesquisaCervejas);
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("sabores", Sabor.values());
		mv.addObject("origens", Origem.values());

	    Page<Cerveja> page = cervejas.pesquisar(cervejaFilter, pageable);
	    PageWrapper<Cerveja> pagina = new PageWrapper<>(page, request, false);		
		
		mv.addObject("pagina", pagina);
		return mv;
	}
	
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) {
		return cervejas.porSkuOuNome(skuOuNome);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja) {
		try {
			cadastroCervejaService.excluir(cerveja);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja) {
		ModelAndView mv = novo(cerveja);
		mv.addObject(cerveja);
		return mv;
	}
	
}
