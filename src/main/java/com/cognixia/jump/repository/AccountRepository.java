package com.cognixia.jump.repository;

import java.util.Optional;

import com.cognixia.jump.model.Account;

public interface AccountRepository {
	
	Optional<Account> findById(long id);

}
