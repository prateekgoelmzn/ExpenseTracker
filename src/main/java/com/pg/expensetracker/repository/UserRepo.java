package com.pg.expensetracker.repository;

import com.pg.expensetracker.model.UserObj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserObj, Long> {
    public Optional<UserObj> findByName(String userName);
}
