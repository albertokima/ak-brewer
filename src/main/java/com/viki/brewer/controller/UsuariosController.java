package com.viki.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viki.brewer.controller.page.PageWrapper;
import com.viki.brewer.model.Genero;
import com.viki.brewer.model.Usuario;
import com.viki.brewer.repository.Grupos;
import com.viki.brewer.repository.Usuarios;
import com.viki.brewer.repository.filter.UsuarioFilter;
import com.viki.brewer.service.CadastroUsuarioService;
import com.viki.brewer.service.StatusUsuario;
import com.viki.brewer.service.exception.UsuarioEmailJaCadastradoException;
import com.viki.brewer.service.exception.UsuarioSenhaObrigatorioException;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	private static final String urlCadastroUsuario = "usuario/CadastroUsuario";
	private static final String urlPesquisaUsuarios = "usuario/PesquisaUsuarios";
	
	@Autowired
	private Grupos grupos;
	
	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	private CadastroUsuarioService cadastroUsuarioService;
	
	@GetMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView(urlCadastroUsuario);
		mv.addObject("grupos", grupos.findAll());
		mv.addObject("generos", Genero.values());
		
		return mv;
	}
	
	@PostMapping( { "/novo", "{\\d+}" } )
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return novo(usuario);
		}
		
		try {
			cadastroUsuarioService.salvar(usuario);
		} catch (UsuarioEmailJaCadastradoException e) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		} catch (UsuarioSenhaObrigatorioException e) {
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
		redirect.addFlashAttribute("mensagem", "Cadastro realizado com sucesso!");
		return new ModelAndView("redirect:/usuarios/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter, BindingResult result,
			@PageableDefault(size=5) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(urlPesquisaUsuarios);
		mv.addObject("grupos", grupos.findAll());

	    Page<Usuario> page = usuarios.pesquisar(usuarioFilter, pageable);
	    PageWrapper<Usuario> pagina = new PageWrapper<>(page, request, false);		
		mv.addObject("pagina", pagina);

		return mv;
	}
	
	@PutMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void alterarStatus(@RequestParam(name="codigos[]") Long[] codigos, @RequestParam(name="status") StatusUsuario status) {
		cadastroUsuarioService.alterarStatus(codigos, status);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Usuario usuario = usuarios.getOne(codigo);
		ModelAndView mv = novo(usuario);
		mv.addObject(usuario);
		return mv;
	}

}
