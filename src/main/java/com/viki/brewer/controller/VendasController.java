package com.viki.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viki.brewer.controller.page.PageWrapper;
import com.viki.brewer.controller.validator.VendaValidator;
import com.viki.brewer.dto.VendaMes;
import com.viki.brewer.dto.VendaOrigem;
import com.viki.brewer.mail.Mailer;
import com.viki.brewer.model.Cerveja;
import com.viki.brewer.model.ItemVenda;
import com.viki.brewer.model.StatusVenda;
import com.viki.brewer.model.Venda;
import com.viki.brewer.repository.Cervejas;
import com.viki.brewer.repository.Vendas;
import com.viki.brewer.repository.filter.VendaFilter;
import com.viki.brewer.security.UsuarioSistema;
import com.viki.brewer.service.CadastroVendaService;
import com.viki.brewer.service.exception.ImpossivelCancelarVendaException;
import com.viki.brewer.session.TabelaItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	private static final String urlCadastroVenda = "venda/CadastroVenda";
	private static final String urlPesquisaVendas = "venda/PesquisaVendas";
	private static final String urlRedirectVendas = "redirect:/vendas/novo";
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private TabelaItensSession tabelaItens;
	
	@Autowired
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private Mailer mailer;

	@Autowired
	private VendaValidator vendaValidator;
	
	@GetMapping("/novo")
	public ModelAndView novo(Venda venda) {
		ModelAndView mv = new ModelAndView(urlCadastroVenda);
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		
		return mv;
	}

	@PostMapping(value = {"/novo", "{\\d+}"}, params = "salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes redirect, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result);
		if (result.hasErrors()) {
			return novo(venda);
		}

		venda.setUsuario(usuarioSistema.getUsuario());
		venda = cadastroVendaService.salvar(venda);

		redirect.addFlashAttribute("mensagem", String.format("Venda n° %d salva com sucesso!", venda.getCodigo()));
		return new ModelAndView(urlRedirectVendas);
	}
	
	@PostMapping(value = {"/novo", "{\\d+}"}, params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes redirect, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result);
		if (result.hasErrors()) {
			return novo(venda);
		}

		venda.setUsuario(usuarioSistema.getUsuario());
		venda = cadastroVendaService.emitir(venda);

		redirect.addFlashAttribute("mensagem", String.format("Venda n° %d salva e emitida com sucesso!", venda.getCodigo()));
		return new ModelAndView(urlRedirectVendas);
	}
	
	@PostMapping(value = {"/novo", "{\\d+}"}, params = "enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes redirect, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result);
		if (result.hasErrors()) {
			return novo(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		venda = cadastroVendaService.salvar(venda);
		mailer.enviar(venda, true);

		redirect.addFlashAttribute("mensagem", String.format("Venda n° %d salva e enviada por e-mail!", venda.getCodigo()));
		return new ModelAndView(urlRedirectVendas);
	}

	@PostMapping(value = "{\\d+}", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result, RedirectAttributes redirect, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.setValorTotal(venda.calcularValorTotal());
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			ModelAndView mv = new ModelAndView("error");
			mv.addObject("status", 405);
			return mv;
		} catch (ImpossivelCancelarVendaException e) {
			result.rejectValue("dataCriacao", e.getMessage(), e.getMessage());
			return novo(venda);
		}

		redirect.addFlashAttribute("mensagem", String.format("Venda cancelada com sucesso!", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
	}

	@PutMapping(value = "/{codigo}", params = "cancelar")
	public ResponseEntity<?> cancelarPelaGrid(@PathVariable Long codigo) {
		try {
			Venda venda = vendas.getOne(codigo);
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return ResponseEntity.badRequest().body("Você não tem permissão para executar esta ação");
		} catch (ImpossivelCancelarVendaException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Erro ao cancelar a venda. Contate o Administrador do Sistema");
		}

		return ResponseEntity.ok().build();
	}
	
	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter, BindingResult result, 
			@PageableDefault(size=10) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(urlPesquisaVendas);
		mv.addObject("statusAll", StatusVenda.values());

	    Page<Venda> page = vendas.pesquisar(vendaFilter, pageable);
	    PageWrapper<Venda> pagina = new PageWrapper<>(page, request, false);		
		
		mv.addObject("pagina", pagina);
		return mv;
	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.getOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		return mvTabelaItensVenda(uuid);
	}

	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, 
			Integer quantidade, String uuid) {
		tabelaItens.alterarQuantidadeItem(uuid, cerveja, quantidade);
		return mvTabelaItensVenda(uuid);
	}

	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView removerItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid) {
		tabelaItens.removerItem(uuid, cerveja);
		return mvTabelaItensVenda(uuid);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.getOne(codigo);
		for (ItemVenda it: venda.getItens()) {
			tabelaItens.adicionarItem(venda.getUuid(), it.getCerveja(), it.getQuantidade());
		}

		ModelAndView mv = novo(venda);
		mv.addObject(venda);
		return mv;
	}
	
	@GetMapping("/dadosVendaPorMes")
	public @ResponseBody List<VendaMes> totalPorMes() {
		return vendas.graficoTotalVendasPorMes();
	}
	
	@GetMapping("/dadosVendaPorOrigem")
	public @ResponseBody List<VendaOrigem> totalPorOrigem() {
		return vendas.graficoTotalVendasPorOrigem();
	}
	
	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.setValorTotal(venda.calcularValorTotal());
		vendaValidator.validate(venda, result);
	}

}
