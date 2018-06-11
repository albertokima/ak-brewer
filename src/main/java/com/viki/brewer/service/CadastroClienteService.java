package com.viki.brewer.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viki.brewer.model.Cliente;
import com.viki.brewer.repository.Clientes;
import com.viki.brewer.service.exception.CpfCnpjJaCadastradoException;
import com.viki.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroClienteService {

	@Autowired
	private Clientes clientes;
	
	@Transactional
	public void salvar(Cliente cliente) {
		verificaCpfCnpjJaCadastrado(cliente);
		
		clientes.save(cliente);
	}
	
	@Transactional
	public void excluir(Cliente cliente) {
		try {
			clientes.deleteById(cliente.getCodigo());
			clientes.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Existem vendas cadastradas para este Cliente. Não foi possível excluir.");
		}
	}

	private void verificaCpfCnpjJaCadastrado(Cliente cliente) {
		Optional<List<Cliente>> clienteExistente = clientes.findAllByCpfCnpj(cliente.getCpfCnpjSemFormatacao());
		if (clienteExistente.isPresent()) {
			boolean mesmoCliente = false;
			for (Cliente c: clienteExistente.get()) {
				if (!cliente.isNovo() && c.getCodigo() == cliente.getCodigo()) {
					mesmoCliente = true;
					break;
				}
			}
			if (!mesmoCliente)
				throw new CpfCnpjJaCadastradoException(cliente.getTipoPessoa().getDocumento() + " já cadastrado no Sistema");
		}
	}
	
}
