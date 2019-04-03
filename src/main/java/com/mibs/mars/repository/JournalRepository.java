package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mibs.mars.entity.Journal;

public interface JournalRepository extends CrudRepository<Journal, Long>{
	
	public Journal findById(Long id);
	public List<Journal> findAll();
	public List<Journal> findByDateBetween(Long d1, Long d2);

}
