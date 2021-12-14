package com.cognixia.jump.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.exception.NegativeBalanceException;
import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.Account;
import com.cognixia.jump.repository.AccountRepository;
import com.cognixia.jump.service.AccountService;

@RequestMapping("/api")
@RestController
public class AccountController {
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	AccountService accountService;
	
	@GetMapping("/account")
	public List<Account> getAccounts() {
		return accountRepo.findAll();
	}
	
	@GetMapping("/user/accounts")
	public ResponseEntity<?> getAccount(HttpServletRequest req) {
		List<Account> accounts = accountService.findByUserId(req);
		
		return ResponseEntity.ok(accounts);
		
	}
	
	@PatchMapping("/account/balance")
	public ResponseEntity<?> updateBalance(@RequestBody Map<String, String> updateInfo, HttpServletRequest req) throws NegativeBalanceException, ResourceNotFoundException {
		return accountService.updateBalance(updateInfo, req);
	}

}
