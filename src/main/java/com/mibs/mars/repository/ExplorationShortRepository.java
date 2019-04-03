package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mibs.mars.entity.ExplorationShort;

public interface ExplorationShortRepository  extends CrudRepository<ExplorationShort, Long>{
	 List<ExplorationShort> findByUsersId(Long id);

}
