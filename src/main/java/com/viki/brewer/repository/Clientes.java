package com.viki.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viki.brewer.model.Cliente;
import com.viki.brewer.repository.helper.cliente.ClientesQueries;

public interface Clientes extends JpaRepository<Cliente, Long>, ClientesQueries {

	public Optional<Cliente> findByCpfCnpj(String cpfCnpj);

	public Optional<List<Cliente>> findAllByCpfCnpj(String cpfCnpjSemFormatacao);

	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);

	@Query(value = "select * from cliente where upper(unaccent(nome)) ilike upper(unaccent(?1))", nativeQuery = true)
	public List<Cliente> porNomeContendo(String nome);

}
