package com.viki.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viki.brewer.model.Venda;
import com.viki.brewer.repository.helper.venda.VendasQueries;

public interface Vendas extends JpaRepository<Venda, Long>, VendasQueries {

}
