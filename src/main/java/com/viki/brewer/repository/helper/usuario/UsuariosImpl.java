package com.viki.brewer.repository.helper.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viki.brewer.model.Grupo;
import com.viki.brewer.model.Usuario;
import com.viki.brewer.repository.filter.UsuarioFilter;
import com.viki.brewer.repository.helper.AbstractPageHelper;

public class UsuariosImpl extends AbstractPageHelper<UsuarioFilter, Usuario> implements UsuariosQueries {

	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {
		return getManager().createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
					.setParameter("email", email).getResultList().stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		return getManager().createQuery(
				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario).getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> pesquisar(UsuarioFilter filtro, Pageable pageable) {
		return filtrar(filtro, pageable, Usuario.class);
	}

	@Override
	protected Predicate[] adicionarFiltro(UsuarioFilter filtro, CriteriaBuilder builder, Root<Usuario> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (!StringUtils.isEmpty(filtro.getNome())) {
			String nome = org.apache.commons.lang3.StringUtils.stripAccents(filtro.getNome().trim());
			nome = "%" + nome + "%";
		    final Expression<String> atributo = 
		    		builder.lower(builder.function("unaccent", String.class, root.get("nome")));
		    final Expression<String> valor = builder.lower(builder.literal(nome));

		    predicates.add(builder.like(atributo, valor));			
		}
		if (!StringUtils.isEmpty(filtro.getEmail())) {
			String email = org.apache.commons.lang3.StringUtils.stripAccents(filtro.getEmail().trim());
			email = email + "%";
		    final Expression<String> atributo = 
		    		builder.lower(builder.function("unaccent", String.class, root.get("email")));
		    final Expression<String> valor = builder.lower(builder.literal(email));

		    predicates.add(builder.like(atributo, valor));			
		}
		if (filtro.getStatus() != null) {
			predicates.add(builder.equal(root.get("ativo"), filtro.getStatus().booleanValue()));
		}
		if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
			List<Predicate> predicados = new ArrayList<Predicate>();
			Expression<Collection<Grupo>> grupos = root.get("grupos");
			for (Grupo grupo: filtro.getGrupos()) {
				predicados.add(builder.isMember(builder.literal(grupo), grupos));
			}
			if (predicados.size() > 0)
				predicates.add(builder.and(predicados.toArray(new Predicate[predicados.size()])));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
