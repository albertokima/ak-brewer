package com.viki.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viki.brewer.model.Cidade;
import com.viki.brewer.repository.helper.cidade.CidadesQueries;

@Repository
public interface Cidades extends JpaRepository<Cidade, Long>, CidadesQueries {

	public List<Cidade> findByEstadoCodigo(Long codigoEstado);

	public Optional<Cidade> findByNomeIgnoreCaseAndEstadoCodigo(String nome, Long estadoCodigo);

	@Query(value = "select * from cidade where upper(unaccent(nome)) = upper(?1) and codigo_estado = ?2", nativeQuery = true)
	public Optional<Cidade> buscarPorNomeECodigoEstado(String nome, Long codigoEstado);	
	
}
