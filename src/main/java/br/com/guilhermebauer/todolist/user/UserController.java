package br.com.guilhermebauer.todolist.user;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @PostMapping
    public void created(@RequestBody UserModel userModel){
        System.out.println(userModel.getName());
        System.out.println(userModel.getUsername());
        System.out.println(userModel.getPassword());

    }

}
