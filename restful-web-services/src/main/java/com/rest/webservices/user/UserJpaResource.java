package com.rest.webservices.user;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rest.webservices.jpa.PostRepository;
import com.rest.webservices.jpa.UserRepository;

@RestController
public class UserJpaResource {
	
	@Autowired
	private UserDaoService service;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PostRepository postRepository;
	
	public UserJpaResource(UserDaoService service, UserRepository repository) {
		this.service = service;
		this.repository = repository;
	}

	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		
		return repository.findAll();
	}

	@GetMapping("/jpa/user/{id}")
	public EntityModel<User> retrieve(@PathVariable int id) {
		Optional<User> findById = repository.findById(id);
		if(findById == null) {
			throw new UserNotFountException("id:"+id);
		}
		
		EntityModel<User> entityModel = EntityModel.of(findById.get());
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-user"));
		
		return entityModel;
	}
	
	@PostMapping("/jpa/user")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User save = repository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(save.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/jpa/user/{id}")
	public void deleteUser(@PathVariable int id) {
		repository.deleteById(id);
	}
	
	@GetMapping("/jpa/user/{id}/posts")
	public List<Post> retrievePostsForUser(@PathVariable int id) {
		Optional<User> user = repository.findById(id);
		if(user == null) {
			throw new UserNotFountException("id:"+id);
		}
		return user.get().getPosts();
	}
	
	@PostMapping("/jpa/user/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> user = repository.findById(id);
		if(user == null) {
			throw new UserNotFountException("id:"+id);
		}
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedPost.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
}
