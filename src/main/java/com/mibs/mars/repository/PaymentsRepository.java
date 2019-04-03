package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mibs.mars.entity.Payments;

public interface PaymentsRepository extends CrudRepository<Payments, Long>{
	List<Payments> findByUserid(Long id);
	Payments findById(Long id);
}
