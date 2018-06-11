package com.viki.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viki.brewer.model.Estilo;
import com.viki.brewer.repository.Estilos;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.viki.brewer.service.exception.NomeEstiloJaCadastradoException;

@Service
public class CadastroEstiloService {
	
	@Autowired
	private Estilos estilos;
	
	@Transactional
	public Estilo salvar(Estilo estilo) {
		Optional<Estilo> estiloOptional = estilos.findByNomeIgnoreCase(estilo.getNome());
		if (estiloOptional.isPresent()) {
			throw new NomeEstiloJaCadastradoException("Nome do estilo já cadastrado");
		}
		return estilos.saveAndFlush(estilo);
	}

	@Transactional
	public void excluir(Estilo estilo) {
		try {
			estilos.deleteById(estilo.getCodigo());
			estilos.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Estilo está sendo utilizado no Cadastro de Cerveja. Não foi possível excluir.");
		}
	}

}
