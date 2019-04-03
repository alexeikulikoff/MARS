package com.mibs.mars.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.CheckedEmail;

@Transactional
public interface CheckedEmailRepository extends CrudRepository<CheckedEmail, Long>{
	
	List<CheckedEmail> findAll();
	CheckedEmail findByEmail(String s);
	CheckedEmail findByUuid(String s);
	@Modifying 
	@Query("UPDATE CheckedEmail c SET c.uuid = :uuid, c.t1=:t1 WHERE c.email = :email")
	void updateUuid(@Param("uuid") String uuid, @Param("t1") Long t1, @Param("email") String email);

}
