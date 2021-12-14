package com.cognixia.jump.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognixia.jump.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
	Optional<Account> findById(long id);
	
	@Transactional
	@Modifying
	@Query("UPDATE Account a SET a.balance = :balance WHERE  a.id = :id")
	public void updateBalance(@Param(value="balance") int balance,@Param(value="id") long id);

}
