package com.example.cryptoapi.demo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static List<User> users = new ArrayList();

    private static void init(){
        users.add(new User(1,"bb",9));
        users.add(new User(2,"bb",9));
        users.add(new User(3,"bb",9));
        users.add(new User(4,"bb",9));
    }

    private UserService() {
        init();
    }

    public List<User> getUsers(){
        return users;
    }

    public User getUserById(int id) {
        return users.get(id);
    }
}
