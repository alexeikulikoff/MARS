package com.mibs.mars.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.ImageEntity;

@Transactional
public interface ImagesRepository extends CrudRepository<ImageEntity, Long> {
	
	List<ImageEntity> findByExplorationidOrderBySerialAsc(Long i);
	List<ImageEntity> findByExplorationid(Long id);
	@Modifying 
	@Query("SELECT id,explorationid,serial, inst from ImageEntity img  WHERE img.explorationid = :explorationid and img.serial = :serial")
	List<ImageEntity> selectBySerial(@Param("explorationid") Long explorationid, @Param("serial") Long serial);
			
}
