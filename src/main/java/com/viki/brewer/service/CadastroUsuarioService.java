package com.viki.brewer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Usuario;
import com.viki.brewer.repository.Usuarios;
import com.viki.brewer.service.exception.UsuarioEmailJaCadastradoException;
import com.viki.brewer.service.exception.UsuarioSenhaObrigatorioException;

@Service
public class CadastroUsuarioService {
	
	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public void salvar(Usuario usuario) {
		Usuario usuarioExistente = null;
		Optional<Usuario> usuarioOptional = usuarios.findByEmailIgnoreCase(usuario.getEmail());
		if (usuarioOptional.isPresent()) {
			if (usuarioOptional.get().equals(usuario)) {
				usuarioExistente = usuarioOptional.get();
			} else {
				throw new UsuarioEmailJaCadastradoException("E-mail já cadastrado no Sistema.");
			}
		}
		if (!usuario.isNovo() && usuarioExistente == null) {
			usuarioExistente = usuarios.getOne(usuario.getCodigo());
		}
		if (StringUtils.isEmpty(usuario.getSenha())) {
			if (usuario.isNovo()) {
				throw new UsuarioSenhaObrigatorioException("Senha é obrigatória");
			} else {
				usuario.setSenha(usuarioExistente.getSenha());
			}
		} else {
			//senha encriptada
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			usuario.setConfirmacaoSenha(usuario.getSenha());
		}
		//alterando o próprio cadastro
		if (usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioExistente.getAtivo());
		}
		
		usuarios.save(usuario);
	}
	
	@Transactional
	public void alterarStatus(Long[] codigos, StatusUsuario status) {
		status.executar(codigos, usuarios);
	}

}
