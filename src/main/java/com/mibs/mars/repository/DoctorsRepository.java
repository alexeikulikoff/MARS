package com.mibs.mars.repository;

import org.springframework.data.repository.CrudRepository;

import com.mibs.mars.entity.Doctors;

public interface DoctorsRepository extends CrudRepository<Doctors, Long>{

	Doctors findByUserid(Long id);
	
}
