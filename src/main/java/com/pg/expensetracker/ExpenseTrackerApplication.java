package com.pg.expensetracker;

import com.pg.expensetracker.model.UserObj;
import com.pg.expensetracker.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ExpenseTrackerApplication {

    @Autowired
    private UserRepo userRepo;

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }

    @PostConstruct
    private void loadUserData() {
        UserObj userObj = new UserObj();
        userObj.setName("user");
        userObj.setPassword("password");
        userObj.setRole("USER");
        userRepo.save(userObj);
    }
}
