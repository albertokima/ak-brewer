package com.viki.brewer.repository.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class AbstractPageHelper<T, E> {

	@PersistenceContext
	private EntityManager manager;
	
	public EntityManager getManager() {
		return manager;
	}

	public final Page<E> filtrar(T filtro, Pageable pageable, Class<E> classe) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<E> criteria = builder.createQuery(classe);
		Root<E> root = criteria.from(classe);
		
		Predicate[] predicates = adicionarFiltro(filtro, builder, root);
		criteria.where(predicates);		
		adicionarOrdem(pageable, builder, criteria, root);
		TypedQuery<E> query = manager.createQuery(criteria);
		configurar(query, pageable);
		Long total = total(filtro, classe);
		
		return new PageImpl<>(query.getResultList(), pageable, total);
	}
	
	private final Long total(T filtro, Class<E> classe) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<E> root = criteria.from(classe);
		
		Predicate[] predicates = adicionarFiltro(filtro, builder, root);
		criteria.where(predicates);
		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}

	private final void configurar(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int registroAtual = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(registroAtual);
		query.setMaxResults(totalRegistrosPorPagina);
	}

	protected void adicionarOrdem(Pageable pageable, CriteriaBuilder builder, CriteriaQuery<?> criteria,
			Root<?> root) {
		Sort sort = pageable.getSort();
		if(sort != null && sort.isSorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteria.orderBy(
				order.isAscending() ? builder.asc(root.get(field)) : builder.desc(root.get(field))
			);
		} else {
			criteria.orderBy(builder.asc(root.get("codigo")));
		}
	}

	protected abstract Predicate[] adicionarFiltro(T filtro, CriteriaBuilder builder, Root<E> root);
	
}
