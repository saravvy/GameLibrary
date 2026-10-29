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
private final UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public void getAll(Context ctx) {
        Set<User> users = userDAO.getAll();

        List<UserDTO> response = users.stream()
                .map(UserDTO::fromEntity)
                .toList();

        ctx.json(response);
    }

    public void create(Context ctx){
        User user = ctx.bodyValidator(User.class)
                .check(u -> u.getUsername() != null && !u.getUsername().isBlank(), "Username is required")
                .check(u -> u.getPassword() != null && !u.getPassword().isBlank(), "Password is required")
                .get();

        try {
            if (userDAO.getByName(user.getUsername()) != null) {
                ctx.status(HttpStatus.CONFLICT).json(Map.of(
                        "message", "Username already exists"
                ));
                return;
            }

        User newUser = userDAO.create(user);
        UserDTO userDTO = UserDTO.fromEntity(newUser);

        ctx.status(HttpStatus.CREATED);
        ctx.json(userDTO);
    } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                    "message", "Could not create user"
            )); }}



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
