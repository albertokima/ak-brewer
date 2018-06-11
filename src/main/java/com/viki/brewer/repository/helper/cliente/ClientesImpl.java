package com.viki.brewer.repository.helper.cliente;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Cliente;
import com.viki.brewer.model.TipoPessoa;
import com.viki.brewer.repository.filter.ClienteFilter;
import com.viki.brewer.repository.helper.AbstractPageHelper;

public class ClientesImpl extends AbstractPageHelper<ClienteFilter, Cliente> implements ClientesQueries {
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> pesquisar(ClienteFilter filtro, Pageable pageable) {
		return filtrar(filtro, pageable, Cliente.class);
	}
	
	@Override
	protected Predicate[] adicionarFiltro(ClienteFilter filtro, CriteriaBuilder builder, Root<Cliente> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (!StringUtils.isEmpty(filtro.getNome())) {
			String cliente = org.apache.commons.lang3.StringUtils.stripAccents(filtro.getNome());
			cliente = "%" + cliente + "%";
			final Expression<String> atributo = 
		    		builder.lower(builder.function("unaccent", String.class, root.get("nome")));
		    final Expression<String> valor = builder.lower(builder.literal(cliente));
		    predicates.add(builder.like(atributo, valor));			
		}
		if (!StringUtils.isEmpty(filtro.getCpfCnpj())) {
			predicates.add(builder.equal(
					root.get("cpfCnpj"), TipoPessoa.removerFormatacao(filtro.getCpfCnpj())));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
