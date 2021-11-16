package com.brandonlauder.restfulwebservices.respositories;

import com.brandonlauder.restfulwebservices.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
