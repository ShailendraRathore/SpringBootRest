package com.practise.rest.microservices.restfulwebservices.user;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



@RestController
public class UserJPAResource {

	@Autowired
	UserRepository service;
	@Autowired
	PostRepository postRepository;
	
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		return service.findAll();
	}
	
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id) {
		Optional<User> user = service.findById(id);
		if(!user.isPresent()) {
			throw new UserNotFoundException("ID-"+id);
		}
		
		EntityModel<User> model= EntityModel.of(user.get()); 
		WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		model.add(linkToUsers.withRel("all-users"));
		return model;
	}
	
	@PostMapping("/jpa/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		 User savedUser = service.save(user);
		 URI location = ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(savedUser.getId()).toUri();
				
				return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		service.deleteById(id);
		}
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrieveAllPosts(@PathVariable int id){
		Optional<User> user = service.findById(id);
		if(!user.isPresent()) {
			throw new UserNotFoundException("ID-"+id);
		}
		return user.get().getPosts();
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPost(@PathVariable int id,@RequestBody Post post) {
		Optional<User> user = service.findById(id);
		if(!user.isPresent()) {
			throw new UserNotFoundException("ID-"+id);
		}
		User u = user.get();
		post.setUser(u);
		postRepository.save(post);
		 URI location = ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(post.getId()).toUri();
				
				return ResponseEntity.created(location).build();
	}
}
