package com.ipiecoles.communes.web.repository;

import com.ipiecoles.communes.web.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = "roles")
    User findByUserName(String userName);

}
