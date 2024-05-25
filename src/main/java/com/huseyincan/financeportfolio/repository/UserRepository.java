package com.huseyincan.financeportfolio.repository;

import com.huseyincan.financeportfolio.dao.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{email: '?0'}")
    User findItemByEmail(String email);

    @Query(value = "{email:'?0'}")
        // , fields="{'name' : 1, 'quantity' : 1}"
    List<User> findAll(String email);

    boolean existsUserByEmail(String email);
    long count();

}
