package app.controllers;

import app.dtos.UserDTO;
import app.entities.User;
import io.javalin.http.Context;
import app.daos.UserDAO;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserController {
private UserDAO userDAO;


   /* public void login(Context ctx) {
        User loginRequest = ctx.bodyAsClass(User.class);
        User user = userDAO.getByName(loginRequest.getUsername());

        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            ctx.status(401).result("Invalid username or password");
            return;
        }

        ctx.status(200).json(user);
    } */

    public void getAll(Context ctx) {
        Set<User> users = userDAO.getAll();

        List<UserDTO> response = users.stream()
                .map(UserDTO::fromEntity)
                .toList();

        ctx.json(response);
    }

    public void create(Context ctx){
        User user = ctx.bodyAsClass(User.class);
        User newUser = userDAO.create(user);
        UserDTO userDTO = UserDTO.fromEntity(newUser);

        ctx.status(HttpStatus.CREATED);
        ctx.json(userDTO);
    }

    public void delete(Context ctx){

        int id = getId(ctx);
        User user = userDAO.getById(id);

        if (user == null) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "No user found"
            ));
            return;
        }

        boolean deleted = userDAO.remove(user);
        if (deleted) {
            ctx.status(HttpStatus.OK);
            ctx.json(Map.of(
                    "message", "User deleted"
            ));
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of(
                    "message", "No user found "
            ));
        }
    }

    public void update(Context ctx){
        User user = ctx.bodyAsClass(User.class);
        user = userDAO.update(user);
        UserDTO newUserDTO = null;
        UserDTO UserDTO = newUserDTO.fromEntity(user);
        if (UserDTO != null) {
            ctx.status(HttpStatus.OK);
            ctx.json(UserDTO);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of(
                    "message", "No user found"
            ));
        }
    }

    public void getById(Context ctx){
        int id = getId(ctx);
        User user = userDAO.getById(id);
        UserDTO newUserDTO = null;
        UserDTO UserDTO = newUserDTO.fromEntity(user);

        if (UserDTO != null) {
            ctx.status(HttpStatus.OK);
            ctx.json(UserDTO);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of(
                    "message", "No user found with id",
                    "id", id
            ));
        }
    }
    private int getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class).get();
    }

}
