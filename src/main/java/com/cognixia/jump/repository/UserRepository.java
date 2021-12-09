package com.cognixia.jump.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognixia.jump.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByUsername(String username);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.username = :username WHERE  u.id = :id")
	public void updateUsername(@Param(value="username") String username,@Param(value="id") long id);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.password = :password WHERE  u.id = :id")
	public void updatePassword(@Param(value="password") String password,@Param(value="id") long id);

}