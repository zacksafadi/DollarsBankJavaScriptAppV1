package com.cognixia.jump.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.exception.SameRoleException;
import com.cognixia.jump.exception.UserAlreadyExistsException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.User;
import com.cognixia.jump.model.User.Role;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.util.JwtUtil;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired 
	JwtUtil jwtUtil;
	
	public boolean createNewUser(AuthenticationRequest registeringUser) throws Exception {
		
		Optional<User> isAlreadyRegistered = userRepo.findByUsername(registeringUser.getUsername());
		
		if (isAlreadyRegistered.isPresent()) {
			throw new UserAlreadyExistsException(registeringUser.getUsername());
		}
		
		User newUser = new User();
		newUser.setUsername(registeringUser.getUsername());
		newUser.setPassword(passwordEncoder.encode(registeringUser.getPassword()));
		newUser.setEnabled(true);
		newUser.setRole(Role.valueOf("ROLE_USER"));
		
		userRepo.save(newUser);
		return true;
	}
	
	public boolean promoteUserAuthorization(AuthenticationRequest user) throws Exception {
		
		Optional<User> userFound = userRepo.findByUsername(user.getUsername());
		
		if (userFound.isEmpty()) {
			throw new ResourceNotFoundException(user.getUsername() + " could not be found");
		}
		
		if (userFound.get().getRole() == Role.valueOf("ROLE_ADMIN")) {
			throw new SameRoleException("ROLE_ADMIN", "promoted");
		}
		
		User updated = userFound.get();
		updated.setRole(Role.valueOf("ROLE_ADMIN"));
		
		userRepo.save(updated);
		return true;
	}
	
	public boolean demoteUserAuthorization(AuthenticationRequest user) throws Exception {

		Optional<User> userFound = userRepo.findByUsername(user.getUsername());

		if (userFound.isEmpty()) {
			throw new ResourceNotFoundException(user.getUsername() + " could not be found");
		}

		if (userFound.get().getRole() == Role.valueOf("ROLE_USER")) {
			throw new SameRoleException("ROLE_USER", "demoted");
		}

		User updated = userFound.get();
		updated.setRole(Role.valueOf("ROLE_USER"));

		userRepo.save(updated);
		return true;
	}
	
	public boolean updateUsername(AuthenticationRequest user, String newName) throws ResourceNotFoundException {
		Optional<User> userFound = userRepo.findByUsername(user.getUsername());

		if (userFound.isEmpty()) {
			throw new ResourceNotFoundException(user.getUsername() + " could not be found");
		}
		
		User updated = userFound.get();
		userRepo.updateUsername(newName, updated.getId());
		userRepo.save(updated);
		return true;
	}
	
	public boolean updatePassword(HttpServletRequest req, String newPassword) throws ResourceNotFoundException {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.updatePassword(passwordEncoder.encode(newPassword), user.getId());
		userRepo.save(user);
		return true;
	}
	
}