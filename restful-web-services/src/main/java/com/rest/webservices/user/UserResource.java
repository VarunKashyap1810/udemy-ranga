package com.rest.webservices.user;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserResource {
	
	@Autowired
	private UserDaoService service;
	
	@GetMapping("/users")
	public List<User> retrieveAllUsers(){
		
		return service.findAll();
	}

	@GetMapping("/user/{id}")
	public User retrieve(@PathVariable int id) {
		User findUser = service.findUser(id);
		if(findUser == null) {
			throw new UserNotFountException("id:"+id);
		}
		return findUser;
	}
	
	@PostMapping("/user")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User save = service.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(save.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
}
