package com.viki.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viki.brewer.controller.page.PageWrapper;
import com.viki.brewer.model.Cidade;
import com.viki.brewer.repository.Cidades;
import com.viki.brewer.repository.Estados;
import com.viki.brewer.repository.filter.CidadeFilter;
import com.viki.brewer.service.CadastroCidadeService;
import com.viki.brewer.service.exception.CidadeEstadoSiglaCadastradoException;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cidades")
public class CidadesController {
	
	private static final String urlCadastroCidade = "cidade/CadastroCidade";
	private static final String urlPesquisaCidades = "cidade/PesquisaCidades";
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private Cidades cidades;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService;
	
	@GetMapping("/novo")
	public ModelAndView nova(Cidade cidade) {
		ModelAndView mv = new ModelAndView(urlCadastroCidade);
		mv.addObject("estados", estados.findAllByOrderBySiglaAsc());
		return mv;
	}

	@Cacheable(value = "cidades", key = "#codigo")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorEstado(
			@RequestParam(name="estado", defaultValue="-1") Long codigo) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		return cidades.findByEstadoCodigo(codigo);
	}
	
	@PostMapping(value = {"/novo","{\\d+}"})
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return nova(cidade);
		}
		
		try {
			cadastroCidadeService.salvar(cidade);
		} catch (CidadeEstadoSiglaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return nova(cidade);
		}
		
		redirect.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
		return new ModelAndView("redirect:/cidades/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result,
			@PageableDefault(size = 5) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(urlPesquisaCidades);
		mv.addObject("estados", estados.findAllByOrderBySiglaAsc());

		Page<Cidade> page = cidades.pesquisar(cidadeFilter, pageable);
	    PageWrapper<Cidade> pagina = new PageWrapper<>(page, request, false);		
		mv.addObject("pagina", pagina);
		
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Cidade cidade = cidades.getOne(codigo);
		ModelAndView mv = nova(cidade);
		mv.addObject(cidade);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public ResponseEntity<?> excluir(@PathVariable("codigo") Cidade cidade) {
		try {
			cadastroCidadeService.excluir(cidade);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build();
	}
	
}
