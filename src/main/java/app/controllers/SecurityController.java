package app.controllers;


import app.daos.SecurityDAO;
import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.User;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.bugelhartmann.TokenSecurity;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController{


    private final SecurityDAO securityDAO;
    TokenSecurity tokenSecurity = new TokenSecurity();
    ObjectMapper objectMapper = new ObjectMapper();
    private final UserDAO userDAO;


    public SecurityController(SecurityDAO securityDAO, UserDAO userDAO){
        this.securityDAO = securityDAO;
        this.userDAO = userDAO;
    }

    public void login(Context ctx) {
        ObjectNode node =objectMapper.createObjectNode();

        try{
            UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);
            // System.out.println(userDTO);
            User verifiedUser = securityDAO.getVerifiedUser(userDTO.name(), userDTO.password());
            UserDTO verifiedUserDTO = new UserDTO(verifiedUser.getId(), verifiedUser.getUsername(), verifiedUser.getRoleAsStrings(), verifiedUser.getPassword());
            //verifiedUser.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
            String token = createToken(verifiedUserDTO);


            node.put("token",token);
            node.put("username", verifiedUser.getUsername());
            ctx.json(node).status(200);

        } catch (EntityNotFoundException | ValidationException e){
            e.printStackTrace();
            ctx.status(401);
            ctx.json(node.put("msf", "user "));
        }

    }


    public void register(Context ctx) {
        ObjectNode node =objectMapper.createObjectNode();
        try{
            User user = ctx.bodyAsClass(User.class);

            // System.out.println(userDTO);
            User created = userDAO.create(user);
            String token = createToken(new UserDTO(created.getId(), created.getUsername(),created.getRoleAsStrings(),created.getPassword()));


            node.put("token",token);
            node.put("username", user.getUsername());
            ctx.json(node).status(201);

        } catch (EntityExistsException e){
            ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
            ctx.json(node.put("msf", "user already exists"));
        }
    }

    public void authenticate(Context ctx) {

    }


    public void authorize(Context ctx) {

    }


    private String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            dk.bugelhartmann.UserDTO tokenUser =
                    new dk.bugelhartmann.UserDTO(
                            user.name(),
                            Set.of(user.role())
                    );
            return tokenSecurity.createToken(tokenUser, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            // logger.error("Could not create token", e);
            throw new ApiException(500, "Could not create token");
        }
    }
}
