package com.example.SpringBootTutorialProject.Config;

import com.example.SpringBootTutorialProject.Dao.UserRepository;
import com.example.SpringBootTutorialProject.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //fetching the data from database
        User user=userRepository.getUserByUserName(username);
        if(user==null){
            throw new UsernameNotFoundException("User Could Found!!");
        }
        //passing the value to the user to the constructor of CustomerUserDetails
        CustomerUserDetails customerUserDetails=new CustomerUserDetails(user);
        return customerUserDetails;
    }
}
