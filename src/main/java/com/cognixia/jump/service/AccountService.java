package com.cognixia.jump.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.cognixia.jump.exception.NegativeBalanceException;
import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.Account;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.AccountRepository;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.util.JwtUtil;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Service
public class AccountService {
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired 
	JwtUtil jwtUtil;
	
	public Account createNewAccount(Account account, HttpServletRequest req) {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		account.setId(-1L);
		account.setUser(user);
		
		Account saved = accountRepo.save(account);
		
		return saved;
	}
	
	public List<Account> findByUserId(HttpServletRequest req) {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		return user.getAccounts();
	}
	
	public ResponseEntity<?> updateBalance(Map<String, String> updateInfo, HttpServletRequest req) throws NegativeBalanceException, ResourceNotFoundException {
		
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		// extract data
		long id = Long.parseLong(updateInfo.get("id"));
		int balance = Integer.parseInt(updateInfo.get("balance"));
		String headerInfo = updateInfo.get("headerInfo");
		
		if (user.getAccounts().stream()
				.filter(a -> a.getId() == id)
				.findFirst().isEmpty()) {
			throw new ResourceNotFoundException("Account", id);
		}
		
		// save() -> update account set balance = ?, type = ? where id = ?
		// custom query -> update account set balance = ? where id = ?
		
		if(balance < 0) {
			throw new NegativeBalanceException(balance);
		}
		
		// check if account does exist...
		if(accountRepo.findById(id).isPresent()) {
			
			int oldBalance = accountRepo.findById(id).get().getBalance();
			
			// call a custom query to update the balance
			accountRepo.updateBalance(balance, id);
			
			// return response
			return ResponseEntity.status(200).header("headerInfo", headerInfo)
					.body("Old balance = " + oldBalance + ", New balance = " + balance);
			
		}
		
		// ...if not, throw not found exception
		throw new ResourceNotFoundException("Account", id);
		
	}

}
