package com.cognixia.jump.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.exception.InvalidPasswordException;
import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.AuthenticationResponse;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.service.UserService;
import com.cognixia.jump.util.JwtUtil;

@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService myUserDetailsService;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@GetMapping("/user")
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	@GetMapping("/user/id/{id}")
	public User getUserById(@PathVariable Long id) throws ResourceNotFoundException {
		return userService.getUserById(id);
	}
	@GetMapping("/user/username/{username}")
	public User getUserByUsername(@PathVariable String username) throws ResourceNotFoundException {
		return userService.getUserByUsername(username);
	}
	@PostMapping("/add/user")
	public ResponseEntity<?> addUser(@RequestBody User user) throws Exception{
		User created = userService.createNewUser(user);
		return ResponseEntity.status(201).body(created);
	}
	@PatchMapping("/user")
	public ResponseEntity<?> updateUsernamePassword(@RequestBody AuthenticationRequest updatingUser, Authentication req) throws Exception {
		
		String username = req.getName();
		Optional<User> found = userRepository.findByUsername(username);
		if (found.isPresent()) {
			String oldUsername = found.get().getUsername();
			String oldPassword = found.get().getPassword();
			User updatedUser = userService.updateUsernamePassword(updatingUser, found.get());

			return ResponseEntity.status(200)
					.body("Old Username: " + oldUsername + ", Old Encoded Password: " + oldPassword + ". New Username: "
							+ updatedUser.getUsername() + ", New Encoded Password: " + updatedUser.getPassword() + ".");
		}
		throw new ResourceNotFoundException("User");
	}
	@DeleteMapping("/remove/user/{id}")
	public User removeUser(@PathVariable Long id) throws ResourceNotFoundException{
		User deleted = userService.deleteUser(id);
		return deleted;
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {

		try {

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		} catch (Exception e) {
			throw new Exception(e);
		}
		final UserDetails USER_DETAILS = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String JWT = jwtTokenUtil.generateTokens(USER_DETAILS);

		return ResponseEntity.ok(new AuthenticationResponse(JWT));
	}
	@GetMapping("/user/login")
	public User getUserByLogin(@RequestBody AuthenticationRequest login) throws ResourceNotFoundException, InvalidPasswordException {
		Optional<User> found = userRepository.findByUsername(login.getUsername());
		
		if(found.isPresent()) {
			User checker = found.get();
			if(passwordEncoder.matches(login.getPassword(), checker.getPassword())){
				return checker;
			}
			throw new InvalidPasswordException();
		}
		throw new ResourceNotFoundException(login.getUsername());
	}
	

}
