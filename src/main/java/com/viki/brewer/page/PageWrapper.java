package com.viki.brewer.page;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class PageWrapper<T>{
	
	public static final int MAX_PAGE_ITEM_DISPLAY = 5;
	private Page<T> page;
	private List<PageItem> items;
	private int currentNumber;
	private UriComponentsBuilder uriBuilder;
	
	public PageWrapper(Page<T> page, HttpServletRequest request){
		this.page = page;
		
		String httpUrl = request.getRequestURL().append(
				request.getQueryString() != null ? "?" + request.getQueryString() : "")
				.toString().replaceAll("\\+", "%20");
		this.uriBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
		
//		this.uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
		this.items = new ArrayList<>();
		this.currentNumber = page.getNumber() + 1; //start from 1 to match page.page
		
		int start, size;
		
		if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY){
			start = 1;
			size = page.getTotalPages();
		} else {
			if (this.currentNumber <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY/2){
				start = 1;
				size = MAX_PAGE_ITEM_DISPLAY;
			} else if (this.currentNumber >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY/2){
				start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY + 1;
				size = MAX_PAGE_ITEM_DISPLAY;
			} else {
				start = this.currentNumber - MAX_PAGE_ITEM_DISPLAY/2;
				size = MAX_PAGE_ITEM_DISPLAY;
			}
		}
		
		for (int i = 0; i < size; i++){
			items.add(new PageItem(start + i, (start +i) == this.currentNumber));
		}
	}
	
	public boolean isEmpty(){
		return page.getContent().isEmpty();
	}
	
	public String urlToPage(int page) {
		
		//checa o parametro page e troca seu valor
		return uriBuilder
				.replaceQueryParam("page", page)
				.build(true)
				.encode()
				.toUriString();
	}
	
	public String urlSorted(String property){
		
		//monta a url de ordenaca com base no uriBuilder
		UriComponentsBuilder uriBuilderOrder = ServletUriComponentsBuilder
				.fromUriString(this.uriBuilder.build(true).encode().toUriString());
		
		//monta o valor ordenado. ex: sku,asc
		// inverte o valor de asc para desc caso exista. Se nao existir, o default é asc
		String sortValue = String.format("%s,%s", property, invertSort(property));
		
		//checa o parametro sort e troca seu valor
		return uriBuilderOrder
				.replaceQueryParam("sort", sortValue)
				.build(true)
				.encode()
				.toUriString();
	}
	
	public String invertSort(String property){
	
		String defaultOrder = "asc";
		
		//verifica se existe ordenacao para a propriedade
		Sort.Order order = page.getSort() != null ? page.getSort().getOrderFor(property) : null;
		
		//inverte a ordenacao caso exista, ou retorna asc
		return order == null ? defaultOrder
				: order.isAscending() ? "desc" : defaultOrder;
		
	}
	
	public boolean isDesc(String property){
		
		Sort sort = page.getSort();
		Sort.Order order = sort != null ? sort.getOrderFor(property) : null;
		
		return (order != null && sort != null) ? !order.isAscending() : false;
	}
	
	public boolean isSorted(String property){
		
		Sort.Order order = page.getSort() != null ? page.getSort().getOrderFor(property) : null;
		
		return order != null;
	}
	
	public List<PageItem> getItems(){
		return items;
	}
	
	public int getNumber(){
		return currentNumber;
	}
	
	public List<T> getContent(){
		return page.getContent();
	}
	
	public int getSize(){
		return page.getSize();
	}
	
	public int getTotalPages(){
		return page.getTotalPages();
	}
	
	public boolean isFirst(){
		return page.isFirst();
	}
	
	public boolean isLast(){
		return page.isLast();
	}
	
	public boolean isPrevious(){
		return page.hasPrevious();
	}
	
	public boolean isNext(){
		return page.hasNext();
	}
	
	public class PageItem {
		private int number;
		private boolean current;
		public PageItem(int number, boolean current){
			this.number = number;
			this.current = current;
		}
		
		public int getNumber(){
			return this.number;
		}
		
		public boolean isCurrent(){
			return this.current;
		}
	}
}