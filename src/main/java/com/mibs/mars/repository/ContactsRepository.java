package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.Contacts;

@Transactional
public interface ContactsRepository  extends CrudRepository<Contacts, Long>{

	Contacts findById(Long id);
	List<Contacts> findByOwnerid(Long id);
	List<Contacts> findByChildid(Long id);
	List<Contacts> findByOwneridAndHidden(Long id, Long h);
	Contacts findByOwneridAndChildid(Long i1, Long i2);
	@Modifying 
	@Query("UPDATE Contacts c SET c.accessed = :a  WHERE c.id = :id")
	void updateAccess(@Param("id") Long id, @Param("a") Long a  );
	@Modifying 
	@Query("UPDATE Contacts c SET c.hidden = :h  WHERE c.id = :id")
	void updateHidden(@Param("id") Long id, @Param("h") Long h  );
	
}
