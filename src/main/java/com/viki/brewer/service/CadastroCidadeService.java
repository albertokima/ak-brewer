package com.viki.brewer.service;

import java.text.Normalizer;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viki.brewer.model.Cidade;
import com.viki.brewer.repository.Cidades;
import com.viki.brewer.service.exception.CidadeEstadoSiglaCadastradoException;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroCidadeService {
	
	@Autowired
	private Cidades cidades;
	
	@Transactional
	public void salvar(Cidade cidade) {
		Optional<Cidade> cidadeExistente = 
				cidades.buscarPorNomeECodigoEstado(cidade.getNomeSemAcento(), cidade.getEstado().getCodigo());
		if (cidadeExistente.isPresent()) {
			throw new CidadeEstadoSiglaCadastradoException("Cidade/Estado já cadastrado");
		}
		cidades.save(cidade);
	}

	public static String removerAcento(String str) {
	    str = Normalizer.normalize(str, Normalizer.Form.NFD);
	    str = str.replaceAll("[^\\p{ASCII}]", "");
	    return str;
	}

	@Transactional
	public void excluir(Cidade cidade) {
		try {
			cidades.deleteById(cidade.getCodigo());
			cidades.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Já existem clientes cadastrados com esta Cidade/UF. Não é possível excluir.");
		}
	}	
	
}
