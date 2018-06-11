package com.viki.brewer.repository.helper.estilo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Estilo;
import com.viki.brewer.repository.filter.EstiloFilter;
import com.viki.brewer.repository.helper.AbstractPageHelper;

public class EstilosImpl extends AbstractPageHelper<EstiloFilter, Estilo> implements EstilosQueries {

	@Override
	@Transactional(readOnly = true)
	public Page<Estilo> pesquisar(EstiloFilter filtro, Pageable pageable) {
		return filtrar(filtro, pageable, Estilo.class);
	}

	@Override
	protected Predicate[] adicionarFiltro(EstiloFilter filtro, CriteriaBuilder builder, Root<Estilo> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (!StringUtils.isEmpty(filtro.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get("nome")), "%" + filtro.getNome().toLowerCase() + "%"));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
