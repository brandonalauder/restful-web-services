package com.brandonlauder.restfulwebservices.controllers;

import com.brandonlauder.restfulwebservices.exception.UserNotFoundException;
import com.brandonlauder.restfulwebservices.models.Post;
import com.brandonlauder.restfulwebservices.models.User;
import com.brandonlauder.restfulwebservices.respositories.PostDaoService;
import com.brandonlauder.restfulwebservices.respositories.PostRepository;
import com.brandonlauder.restfulwebservices.respositories.UserDaoService;
import com.brandonlauder.restfulwebservices.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserJPAController {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/jpa/users")
    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/jpa/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            throw new UserNotFoundException("id-"+id);
        }
        EntityModel<User> model = EntityModel.of(user.get());
        WebMvcLinkBuilder linktoUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linktoUsers.withRel("all-users"));
        return model;
    }

    @PostMapping("/jpa/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
    }

    @GetMapping("/jpa/users/{id}/posts")
    public List<Post> retrieveUsersPosts(@PathVariable int id){
        User user = retrieveUser(id).getContent();
        return user.getPosts();
    }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post){
        User user = retrieveUser(id).getContent();
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        user.addPost(savedPost);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/jpa/users/{id}/posts/{post_id}")
    public Post retrievePost(@PathVariable int id, @PathVariable int post_id){
        User user = retrieveUser(id).getContent();
        Optional<Post> post = postRepository.findById(post_id);
        if(post.isPresent() || user.getPosts().contains(post.get())){
            return post.get();
        }else{
            throw new UserNotFoundException("post id-"+post_id);
        }
    }

    @GetMapping("/jpa/hello-world")
    public String helloWorld(){
        return messageSource.getMessage("good.morning.message", null, "Default Message", LocaleContextHolder.getLocale());
    }
}
