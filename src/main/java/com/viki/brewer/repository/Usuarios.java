package com.viki.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viki.brewer.model.Usuario;
import com.viki.brewer.repository.helper.usuario.UsuariosQueries;

public interface Usuarios extends JpaRepository<Usuario, Long>, UsuariosQueries{
	
	public Optional<Usuario> findByEmailIgnoreCase(String email);
	
	public List<Usuario> findByCodigoIn(Long[] codigos);

}
