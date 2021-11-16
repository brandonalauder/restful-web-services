package com.brandonlauder.restfulwebservices.respositories;

import com.brandonlauder.restfulwebservices.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
