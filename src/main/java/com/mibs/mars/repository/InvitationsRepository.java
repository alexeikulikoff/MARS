package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.Invitations;

@Transactional
public interface InvitationsRepository extends CrudRepository<Invitations, Long>{
	
	Invitations findById(Long id);
	List<Invitations> findByPatientid(Long id);
	List<Invitations> findByDoctorid(Long id);
	List<Invitations> findByPatientidAndDoctorid(Long id1, Long id2);
	Invitations findByPatientidAndDoctoridAndExplorationid(Long id1, Long id2, Long id3);
	List<Invitations> findByPatientidAndExplorationid(Long id1, Long id2);
	

}
