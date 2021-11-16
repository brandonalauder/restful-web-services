package com.brandonlauder.restfulwebservices.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.brandonlauder.restfulwebservices.exception.UserNotFoundException;
import com.brandonlauder.restfulwebservices.models.Post;
import com.brandonlauder.restfulwebservices.models.User;
import com.brandonlauder.restfulwebservices.respositories.PostDaoService;
import com.brandonlauder.restfulwebservices.respositories.UserDaoService;
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

@RestController
public class UserController {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private PostDaoService postDaoService;

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return userDaoService.findAll();
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id){
        User user = userDaoService.findOne(id);
        if(user == null){
            throw new UserNotFoundException("id-"+id);
        }
        EntityModel<User> model = EntityModel.of(user);
        WebMvcLinkBuilder linktoUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linktoUsers.withRel("all-users"));
        return model;
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        User savedUser = userDaoService.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        User user = userDaoService.deleteById(id);
        if(user == null){
            throw new UserNotFoundException("id-"+id);
        }
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveUsersPosts(@PathVariable int id){
        User user = retrieveUser(id).getContent();
        return user.getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post){
        User user = retrieveUser(id).getContent();
        post.setUser(user);
        Post savedPost = postDaoService.save(post);
        user.addPost(savedPost);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/users/{id}/posts/{post_id}")
    public Post retrievePost(@PathVariable int id, @PathVariable int post_id){
        User user = retrieveUser(id).getContent();
        Post post = postDaoService.findbyId(post_id);
        if(user.getPosts().contains(post)){
            return post;
        }else{
            throw new UserNotFoundException("post id-"+post_id);
        }
    }

    @GetMapping("/hello-world")
    public String helloWorld(){
        return messageSource.getMessage("good.morning.message", null, "Default Message", LocaleContextHolder.getLocale());
    }
}
