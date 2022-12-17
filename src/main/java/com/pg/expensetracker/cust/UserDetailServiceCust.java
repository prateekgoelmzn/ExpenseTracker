package com.pg.expensetracker.cust;

import com.pg.expensetracker.model.UserObj;
import com.pg.expensetracker.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceCust implements UserDetailsService {

    private UserRepo userRepo;

    @Autowired
    public UserDetailServiceCust(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserObj> user = userRepo.findByName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username + " Not found !");
        }
        return new UserDetailsCust(user.get());
    }
}
