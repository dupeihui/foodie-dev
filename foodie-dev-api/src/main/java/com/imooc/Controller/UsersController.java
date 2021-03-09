package com.imooc.Controller;

import com.imooc.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/getUser")
    public Object getUser(String id) {
        return usersService.getUserInfo(id);
    }

    @PostMapping("/saveUser")
    public Object saveUser() {
        usersService.saveUser();
        return "OK";
    }

    @PostMapping("/updateUser")
    public Object updateUser(String id) {
        usersService.updateUser(id);
        return "OK";
    }

    @PostMapping("/deleteUser")
    public Object deleteUser(String id) {
        usersService.deleteUser(id);
        return "OK";
    }
}
