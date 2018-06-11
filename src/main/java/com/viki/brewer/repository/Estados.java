package com.viki.brewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viki.brewer.model.Estado;

@Repository
public interface Estados extends JpaRepository<Estado, Long> {

	public List<Estado> findAllByOrderBySiglaAsc();

}
