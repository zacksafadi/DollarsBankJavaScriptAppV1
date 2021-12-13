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
	
	public User getUserById(Long id) throws ResourceNotFoundException {
		Optional<User> found = userRepo.findById(id);
		
		if(found.isPresent())
			return found.get();
		throw new ResourceNotFoundException("User",id);
	}
	public User getUserByUsername(String username) throws ResourceNotFoundException {
		Optional<User> found = userRepo.findByUsername(username);
		
		if(found.isPresent())
			return found.get();
		throw new ResourceNotFoundException("User",username);
	}
	
	public User createNewUser(User registeringNewUser) throws Exception{
		Optional<User> isAlreadyRegistered = userRepo.findByUsername(registeringNewUser.getUsername());
		
		if(isAlreadyRegistered.isPresent()) {
			throw new UserAlreadyExistsException(registeringNewUser.getUsername());
		}
		
		registeringNewUser.setPassword(passwordEncoder.encode(registeringNewUser.getPassword()));
		return userRepo.save(registeringNewUser);
		
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
	
	public User updateUsernamePassword(AuthenticationRequest updatingUser, User currentUser) {
		currentUser.setUsername(updatingUser.getUsername());
		currentUser.setPassword(passwordEncoder.encode(updatingUser.getPassword()));
		userRepo.save(currentUser);	
		return currentUser;
		
	}
	
	public User deleteUser(Long id) throws ResourceNotFoundException {
		Optional<User> found = userRepo.findById(id);
		if(found.isPresent()) {
			User deleted = found.get();
			userRepo.delete(deleted);
			return deleted;
		}
		throw new ResourceNotFoundException("User", id);
	}
	
}