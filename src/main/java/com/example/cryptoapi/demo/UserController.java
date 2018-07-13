package com.example.cryptoapi.demo;

import com.example.cryptoapi.springmvc.extension.annotation.RequestDecryptBody;
import com.example.cryptoapi.springmvc.extension.annotation.ResponseEncryptBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
    @ResponseEncryptBody
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id){
        return userService.getUserById(id);
    }

    @PostMapping
    public User addUser(@RequestDecryptBody User user){
        userService.getUsers().add(user);
        return userService.getUserById(user.getId());
    }

}
