package com.viki.brewer.repository.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/* @deprecated (since 1.0.0), use AbstractPageHelper */
@Deprecated
public abstract class AbstractHelper<T, E> {

	@PersistenceContext
	private EntityManager manager;
	
	public EntityManager getManager() {
		return manager;
	}
	
	@SuppressWarnings("unchecked")
	public Page<E> pesquisar(T filtro, Pageable pageable, Class<E> entidade) {
		Criteria criteria = getManager().unwrap(Session.class).createCriteria(entidade);
		
		configurar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		adicionarOrdem(criteria, pageable);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro, entidade));
	}
	
	protected final Long total(T filtro, Class<E> entidade) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(entidade);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	protected final void configurar(Criteria criteria, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int tamanhoPagina = pageable.getPageSize();
		int registroAtual = paginaAtual * tamanhoPagina;
		
		criteria.setFirstResult(registroAtual);
		criteria.setMaxResults(tamanhoPagina);
	}
	
	protected void adicionarOrdem(Criteria criteria, Pageable pageable) {
		Sort sort = pageable.getSort();
		if (sort != null && sort.isSorted()) {
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();
			criteria.addOrder(order.isAscending() ? Order.asc(property) : Order.desc(property));
		} else {
			criteria.addOrder(Order.asc("codigo"));
		}
	}

	protected abstract void adicionarFiltro(T filtro, Criteria criteria);
	
}
