package com.mibs.mars.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mibs.mars.entity.Users;

@Transactional
public interface UsersRepository extends CrudRepository<Users, Long>{

	List<Users> findAll();
	List<Users> findByRoles(String role);
	Users findByLogin(String s);
	Users findByEmail(String e);
	Users findById(Long id);
	Users findByIdAndRoles(Long id, String role);
//	@Modifying 
//	@Query("UPDATE Users p SET p.email = :email, p.isEmailChecked = :v  WHERE p.id = :id")
//	void updateEmail(@Param("email") String email,  @Param("v") int v, @Param("id") Long id);
	@Modifying 
	@Query("UPDATE Users p SET p.mailed = :v  WHERE p.id = :id")
	void updateMailed(@Param("v") Long v, @Param("id") Long id);
	@Modifying 
	@Query("UPDATE Users p SET p.photo = :image  WHERE p.id = :id")
	void updateImage(@Param("image") byte[] image,  @Param("id") Long id);
	@Modifying
	@Query("UPDATE Users u SET u.login = :email, u.passwd =:passwd, u.firstname=:firstname, u.lastname=:lastname, u.surname=:surname, u.email=:email, u.photo=:photo WHERE u.id = :id")
	void updateAll(@Param("passwd") String passwd, @Param("firstname") String firstname, @Param("lastname") String lastname, @Param("surname") String surname, @Param("email") String email, @Param("photo") byte[] photo, @Param("id") Long id);

}
