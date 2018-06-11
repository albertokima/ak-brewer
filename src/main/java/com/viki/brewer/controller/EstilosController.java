package com.viki.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viki.brewer.controller.page.PageWrapper;
import com.viki.brewer.model.Estilo;
import com.viki.brewer.repository.Estilos;
import com.viki.brewer.repository.filter.EstiloFilter;
import com.viki.brewer.service.CadastroEstiloService;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.viki.brewer.service.exception.NomeEstiloJaCadastradoException;

@Controller
@RequestMapping("/estilos")
public class EstilosController {
	
	private static final String urlCadastroEstilo = "estilo/CadastroEstilo";
	private static final String urlPesquisaEstilos = "estilo/PesquisaEstilos";
	
	@Autowired
	private CadastroEstiloService cadastroEstiloService;
	
	@Autowired
	private Estilos estilos;
	
	@GetMapping("/novo")
	public ModelAndView novo(Estilo estilo) {
		return new ModelAndView(urlCadastroEstilo);
	}

	@PostMapping(value = {"/novo", "{\\d+}" })
	public ModelAndView salvar(@Valid Estilo estilo, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return novo(estilo);
		}
		try {
			cadastroEstiloService.salvar(estilo);
		} catch (NomeEstiloJaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(estilo);
		}
		
		redirect.addFlashAttribute("mensagem", "Estilo salvo com sucesso!");
		return new ModelAndView("redirect:/estilos/novo");
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Estilo estilo, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}

		estilo = cadastroEstiloService.salvar(estilo);
		return ResponseEntity.ok(estilo);
	}
	
	@GetMapping
	public ModelAndView pesquisar(EstiloFilter estiloFilter, BindingResult result, 
			@PageableDefault(size=5) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(urlPesquisaEstilos);
		
//		String filtro = estilo.getNome() == null ? "" : estilo.getNome();
//		Page<Estilo> page = estilos.findAllByNomeContainsAllIgnoreCase(filtro, pageable);
		Page<Estilo> page = estilos.pesquisar(estiloFilter, pageable);

		PageWrapper<Estilo> pagina = new PageWrapper<>(page, request, false);
		mv.addObject("pagina", pagina);
		
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Estilo estilo = estilos.getOne(codigo);
		ModelAndView mv = novo(estilo);
		mv.addObject(estilo);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Estilo estilo) {
		try {
			cadastroEstiloService.excluir(estilo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
}
