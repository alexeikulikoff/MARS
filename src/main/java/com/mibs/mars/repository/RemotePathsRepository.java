package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.RemotePaths;
@Transactional
public interface RemotePathsRepository extends CrudRepository<RemotePaths, Long> {
	List<RemotePaths> findAll();
	RemotePaths findByIpaddressAndDirname(String ip, String dirname);
	RemotePaths findByIpaddressAndDirnameIgnoreCase(String ip, String dirname);
	RemotePaths findById(Long id);
	@Modifying
	@Query("UPDATE RemotePaths r SET r.login = :login, r.passwd =:passwd, r.department=:department, r.ipaddress=:ipaddress, r.dirname=:dirname, r.uniqueid=:uniqueid  WHERE r.id = :id")
	void updateAll(@Param("login") String login, @Param("passwd")  String passwd, @Param("department") String department, @Param("ipaddress") String ipaddress, @Param("dirname") String dirname, @Param("uniqueid") String uniqueid, @Param("id") Long id);
	@Modifying
	@Query("UPDATE RemotePaths r SET r.checked = :checked  WHERE r.id = :id")
	void updateCheked(@Param("checked") Integer checked, @Param("id")  Long id);
	
}
