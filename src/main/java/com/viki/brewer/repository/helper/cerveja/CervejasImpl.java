package com.viki.brewer.repository.helper.cerveja;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viki.brewer.dto.CervejaDTO;
import com.viki.brewer.model.Cerveja;
import com.viki.brewer.model.Estilo;
import com.viki.brewer.repository.filter.CervejaFilter;
import com.viki.brewer.repository.helper.AbstractPageHelper;
import com.viki.brewer.storage.FotoStorage;

public class CervejasImpl  extends AbstractPageHelper<CervejaFilter, Cerveja> implements CervejasQueries {

	@Autowired
	private FotoStorage fotoStorage;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> pesquisar(CervejaFilter filtro, Pageable pageable) {
		return filtrar(filtro, pageable, Cerveja.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal valorTotalEstoque() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				getManager().createQuery(
						"select sum(valor*quantidadeEstoque) from Cerveja where quantidadeEstoque >= 0", BigDecimal.class)
							.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long quantidadeItensEstoque() {
		Optional<Long> optional = Optional.ofNullable(
				getManager().createQuery(
						"select sum(quantidadeEstoque) from Cerveja where quantidadeEstoque >= 0", Long.class)
							.getSingleResult());
		return optional.orElse(0L);
	}

	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String jpql = "select new com.viki.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
				+ "from Cerveja where lower(sku) like lower(:skuOuNome) or unaccent(lower(nome)) like unaccent(lower(:skuOuNome))";
		
		List<CervejaDTO> resultado = getManager().createQuery(jpql, CervejaDTO.class)
				.setParameter("skuOuNome", '%'+skuOuNome+'%').getResultList();
		resultado.forEach(c -> c.setUrlThumbnailFoto(
				fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + c.getFoto())));
		return resultado;
	}

	private boolean isEstiloInformado(Estilo estilo) {
		return estilo != null && estilo.getCodigo() != null;
	}
	
	@Override
	protected Predicate[] adicionarFiltro(CervejaFilter filtro, CriteriaBuilder builder, Root<Cerveja> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (!StringUtils.isEmpty(filtro.getSku())) {
			predicates.add(builder.equal(root.get("sku"), filtro.getSku().toUpperCase()));
		}
		if (!StringUtils.isEmpty(filtro.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get("nome")), "%" + filtro.getNome().toLowerCase() + "%"));
		}
		if (!StringUtils.isEmpty(filtro.getOrigem())) {
			predicates.add(builder.equal(root.get("origem"), filtro.getOrigem()));
		}
		if (!StringUtils.isEmpty(filtro.getSabor())) {
			predicates.add(builder.equal(root.get("sabor"), filtro.getSabor()));
		}
		if (isEstiloInformado(filtro.getEstilo())) {
			predicates.add(builder.equal(root.get("estilo"), filtro.getEstilo()));
		}
		if (filtro.getValorDe() != null && filtro.getValorDe().doubleValue() > 0) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("valor"), filtro.getValorDe()));
		}
		if (filtro.getValorAte() != null && filtro.getValorAte().doubleValue() > 0) {
			predicates.add(builder.lessThanOrEqualTo(root.get("valor"), filtro.getValorAte()));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	@Override
	protected void adicionarOrdem(Pageable pageable, CriteriaBuilder builder, CriteriaQuery<?> criteria,
			Root<?> root) {
		Sort sort = pageable.getSort();
		if (sort != null && sort.isSorted()) {
			super.adicionarOrdem(pageable, builder, criteria, root);
		} else {
			criteria.orderBy(builder.asc(root.get("sku")));
		}
	}
	
}

