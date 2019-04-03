package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.Exploration;

@Transactional
public interface ExplorationRepository  extends CrudRepository<Exploration, Long>{
	List<Exploration> findAll();
	List<Exploration> findByUsersId(Long id);
	Exploration findById(Long id);
	Exploration findByUniqueid(String uniqueid);
	@Modifying 
	@Query("UPDATE Exploration e SET e.dicomSize = :dicomSize WHERE e.id = :id")
	void updateDicomSize(@Param("dicomSize") Long dicomSize,  @Param("id") Long id);
	@Modifying 
	@Query("UPDATE Exploration e SET e.remotepath = :remotepath WHERE e.id = :id")
	void updatePath(@Param("remotepath") String remotepath,  @Param("id") Long id);

}
