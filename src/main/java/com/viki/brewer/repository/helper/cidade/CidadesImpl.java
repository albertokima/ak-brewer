package com.viki.brewer.repository.helper.cidade;

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

import com.viki.brewer.model.Cidade;
import com.viki.brewer.repository.filter.CidadeFilter;
import com.viki.brewer.repository.helper.AbstractPageHelper;

public class CidadesImpl extends AbstractPageHelper<CidadeFilter, Cidade> implements CidadesQueries {
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> pesquisar(CidadeFilter filtro, Pageable pageable) {
		return filtrar(filtro, pageable, Cidade.class);
	}

	@Override
	protected Predicate[] adicionarFiltro(CidadeFilter filtro, CriteriaBuilder builder, Root<Cidade> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (filtro.getEstado() != null) {
			predicates.add(builder.equal(root.get("estado"), filtro.getEstado()));
		}
		if (!StringUtils.isEmpty(filtro.getNomeCidade())) {
			String cidade = org.apache.commons.lang3.StringUtils.stripAccents(filtro.getNomeCidade());
			cidade = "%" + cidade + "%";
		    final Expression<String> atributo = 
		    		builder.lower(builder.function("unaccent", String.class, root.get("nome")));
		    final Expression<String> valor = builder.lower(builder.literal(cidade));

		    predicates.add(builder.like(atributo, valor));			
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}

