package com.brandonlauder.restfulwebservices.respositories;

import com.brandonlauder.restfulwebservices.models.Post;
import com.brandonlauder.restfulwebservices.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostDaoService {
    private static List<Post> posts = new ArrayList<>();
    private static int postCount = 0;

    public Post save(Post post){
        if(post.getId()==null) {
            post.setId(++postCount);
        }
        posts.add(post);
        return post;
    }

    public Post findbyId(int id){
        for(Post post : posts){
            if(post.getId()==id){
                return post;
            }
        }
        return null;
    }
}
