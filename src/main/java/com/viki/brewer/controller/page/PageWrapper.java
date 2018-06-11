package com.viki.brewer.controller.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class PageWrapper<T> {

	private static final int NUMERO_PAGINAS_EXIBIDAS = 5;
	private int inicio, fim;
	private Page<T> page;
	private UriComponentsBuilder uriBuilder;
	

	public PageWrapper(Page<T> page, HttpServletRequest request) {
		this.page = page;
		this.uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
	}

	public PageWrapper(Page<T> page, HttpServletRequest request, boolean semLimite) {
		this.page = page;
		String httpUrl = request.getRequestURL().append(
				request.getQueryString() != null ? "?" + request.getQueryString() : "")
				.toString().replaceAll("\\+", "%20").replaceAll("excluido", "");
		this.uriBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
		
		if (semLimite) {
			this.inicio = 1;
			this.fim = page.getTotalPages();
		} else {
			if (page.getTotalPages() < NUMERO_PAGINAS_EXIBIDAS) {
				this.inicio = 1;
				this.fim = page.getTotalPages();
			} else {
			
				if (getAtual() + 1 <= NUMERO_PAGINAS_EXIBIDAS - NUMERO_PAGINAS_EXIBIDAS/2) {
					this.inicio = 1;
					this.fim = NUMERO_PAGINAS_EXIBIDAS;
				} else if (getAtual() <= page.getTotalPages() - 1 - NUMERO_PAGINAS_EXIBIDAS/2) {
					this.inicio = getAtual() + 1 - NUMERO_PAGINAS_EXIBIDAS/2;
					this.fim = getAtual() + 1 + NUMERO_PAGINAS_EXIBIDAS/2;
				} else {
					this.inicio = page.getTotalPages() + 1 - NUMERO_PAGINAS_EXIBIDAS;
					this.fim = page.getTotalPages();
				}
			
			}
		}
	}
	
	public String url(int pagina) {
		return uriBuilder.replaceQueryParam("page", pagina).build(true).encode().toUriString();
	}

	public String urlOrdenada(String campo) {
		UriComponentsBuilder orderBuilder = 
				UriComponentsBuilder.fromUriString(uriBuilder.build(true).encode().toUriString()); 
		
		String ordem = String.format("%s,%s", campo, inverterOrdem(campo));
		
		return orderBuilder.replaceQueryParam("sort", ordem).build(true).encode().toUriString();
	}
	
	private String inverterOrdem(String campo) {
		String ordem = "asc";
		Sort.Order order = (page.getSort()!=null && page.getSort().isSorted()) ? page.getSort().getOrderFor(campo) : null;
		if (order != null) {
			ordem = order.isAscending() ? "desc" : "asc";
		} 
		return ordem;
	}

	public List<T> getConteudo() {
		return page.getContent();
	}

	public int getInicio() {
		return inicio;
	}
	
	public int getFim() {
		return fim;
	}
	
	public int getAtual() {
		return page.getNumber();
	}
	
	public int getTotalPaginas() {
		return page.getTotalPages();
	}
	
	public int getTamanho() {
		return page.getSize();
	}
	
	public String getPaginasLabel() {
		return this.getAtual()+1 + " de " + this.getTotalPaginas();
	}
	
	public boolean isVazia() {
		return page.getContent().isEmpty();
	}
	
	public boolean isPrimeira() {
		return page.isFirst();
	}
	
	public boolean isUltima() {
		return page.isLast();
	}
	
	public boolean isUnica() {
		return this.getTotalPaginas() == 1;
	}
	
	public boolean isNavegavel() {
		return this.getTotalPaginas() > 2;
	}
	
	public boolean descendente(String campo) {
		Sort.Order order = (page.getSort() != null && page.getSort().isSorted()) ? page.getSort().getOrderFor(campo) : null; 
		if (order == null) {
			return false;
		}
		return !order.isAscending();
	}

	public boolean semOrdenacao(String campo) {
		return page.getSort() == null || page.getSort().isUnsorted() || page.getSort().getOrderFor(campo) == null;
	}
	
	public boolean aleatoria(String campo) {
		return !isVazia() && semOrdenacao(campo);
	}
	
}
