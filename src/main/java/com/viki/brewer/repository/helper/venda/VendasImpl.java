package com.viki.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viki.brewer.dto.VendaMes;
import com.viki.brewer.dto.VendaOrigem;
import com.viki.brewer.model.StatusVenda;
import com.viki.brewer.model.TipoPessoa;
import com.viki.brewer.model.Venda;
import com.viki.brewer.repository.filter.VendaFilter;
import com.viki.brewer.repository.helper.AbstractPageHelper;

public class VendasImpl extends AbstractPageHelper<VendaFilter, Venda> implements VendasQueries {

	@Override
	@Transactional(readOnly = true)
	public Page<Venda> pesquisar(VendaFilter filtro, Pageable pageable) {
		return filtrar(filtro, pageable, Venda.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal totalVendasNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
			getManager().createQuery(
					"select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
						.setParameter("ano", Year.now().getValue())
						.setParameter("status", StatusVenda.EMITIDA)
						.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal totalVendasNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				getManager().createQuery(
						"select sum(valorTotal) from Venda where year(dataCriacao) = :ano and month(dataCriacao) = :mes and status = :status", BigDecimal.class)
							.setParameter("ano", Year.now().getValue())
							.setParameter("mes", YearMonth.now().getMonthValue())
							.setParameter("status", StatusVenda.EMITIDA)
							.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal totalTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				getManager().createQuery(
						"select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
							.setParameter("ano", Year.now().getValue())
							.setParameter("status", StatusVenda.EMITIDA)
							.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public List<VendaMes> graficoTotalVendasPorMes() {
		List<VendaMes> dados = getManager().createNamedQuery("Vendas.totalPorMes", VendaMes.class).getResultList();
		
		LocalDate hoje = LocalDate.now();
		for (int i=0; i < 6; i++) {
			String mes = hoje.format(DateTimeFormatter.ofPattern("MMM/YYYY"));
			boolean naoExiste = dados.stream().filter(d-> d.getMes().equalsIgnoreCase(mes)).count() == 0;
			if (naoExiste) {
				String ordem = hoje.format(DateTimeFormatter.ofPattern("YYYYMM"));
				dados.add(i, new VendaMes(Integer.parseInt(ordem), mes.toUpperCase(), 0, 0));
			}
			hoje = hoje.minusMonths(1L);
		}
		
		return dados;
	}
	
	@Override
	public List<VendaOrigem> graficoTotalVendasPorOrigem() {
		List<VendaOrigem> dados = getManager().createNamedQuery("Vendas.totalPorOrigem", VendaOrigem.class).getResultList();
		
		LocalDate hoje = LocalDate.now();
		for (int i=0; i < 6; i++) {
			String mes = hoje.format(DateTimeFormatter.ofPattern("MMM/YYYY"));
			boolean naoExiste = dados.stream().filter(d-> d.getMes().equalsIgnoreCase(mes)).count() == 0;
			if (naoExiste) {
				String ordem = hoje.format(DateTimeFormatter.ofPattern("YYYYMM"));
				dados.add(i, new VendaOrigem(Integer.parseInt(ordem), mes.toUpperCase(), 0, 0));
			}
			hoje = hoje.minusMonths(1L);
		}
		
		return dados;
	}

	@Override
	protected Predicate[] adicionarFiltro(VendaFilter filtro, CriteriaBuilder builder, Root<Venda> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (filtro.getCodigo() != null) {
			predicates.add(builder.equal(root.get("codigo"), filtro.getCodigo()));
		} else {
			if (filtro.getStatus() != null) {
				predicates.add(builder.equal(root.get("status"), filtro.getStatus()));
			}
			if (filtro.getDataCriacaoDe() != null) {
				predicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), filtro.getDataCriacaoDe().atTime(0, 0)));
			}
			if (filtro.getDataCriacaoAte() != null) {
				predicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), filtro.getDataCriacaoAte().atTime(23, 59, 59)));
			}
			if (filtro.getValorTotalDe() != null) {
				predicates.add(builder.greaterThanOrEqualTo(root.get("valorTotal"), filtro.getValorTotalDe()));
			}
			if (filtro.getValorTotalAte() != null) {
				predicates.add(builder.lessThanOrEqualTo(root.get("valorTotal"), filtro.getValorTotalAte()));
			}
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				String cliente = org.apache.commons.lang3.StringUtils.stripAccents(filtro.getNomeCliente());
				cliente = "%" + cliente + "%";
			    final Expression<String> atributo = 
			    		builder.lower(builder.function("unaccent", String.class, root.get("cliente").get("nome")));
			    final Expression<String> valor = builder.lower(builder.literal(cliente));

			    predicates.add(builder.like(atributo, valor));			
			}
			if (!StringUtils.isEmpty(filtro.getCpfCnpjCliente())) {
				predicates.add(builder.equal(root.get("cliente").get("cpfCnpj"), TipoPessoa.removerFormatacao(filtro.getCpfCnpjCliente())));
			}
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	@Override
	protected void adicionarOrdem(Pageable pageable, CriteriaBuilder builder, CriteriaQuery<?> criteria, Root<?> root) {
		Sort sort = pageable.getSort();
		if (sort != null && sort.isSorted()) {
			super.adicionarOrdem(pageable, builder, criteria, root);
		} else {
			criteria.orderBy(builder.desc(root.get("dataCriacao")));
		}
	}

}
