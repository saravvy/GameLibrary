package app.controllers;


import app.config.AplicationConfig;
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
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SecurityController{


    private final SecurityDAO securityDAO;
    TokenSecurity tokenSecurity = new TokenSecurity();
    ObjectMapper objectMapper = new ObjectMapper();
    private final UserDAO userDAO;
    private static final org.slf4j.Logger logger =
            LoggerFactory.getLogger(AplicationConfig.class);



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
            //UserDTO verifiedUserDTO = new UserDTO(verifiedUser.getId(), verifiedUser.getUsername(), verifiedUser.getRoleAsStrings(), verifiedUser.getPassword());
            //verifiedUser.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
            String token = createToken(verifiedUser.getUsername(),verifiedUser.getRoleAsStrings());


            node.put("token",token);
            node.put("username", verifiedUser.getUsername());
            ctx.json(node).status(200);

        } catch (EntityNotFoundException | ValidationException e){
            e.printStackTrace();
            ctx.status(401);
            ctx.json(node.put("msg", "user "));
        }

    }


    public void register(Context ctx) {
        ObjectNode node =objectMapper.createObjectNode();
        try{
            UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

            User newUser = new User(userDTO.name(), userDTO.password());
            newUser.setRole(app.entities.Role.USER);

            User created = userDAO.create(newUser);
            String token = createToken(created.getUsername(),created.getRoleAsStrings());


            node.put("token",token);
            node.put("username", created.getUsername());
            ctx.json(node).status(201);

        } catch (EntityExistsException e){
            ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
            ctx.json(node.put("msf", "user already exists"));
        }
    }

    public void authenticate(Context ctx) {
        if (ctx.method().name().equals("OPTIONS")) {
            return;
        }

        Set<RouteRole> allowedRoles = ctx.routeRoles();

        // Åbne endpoints kræver ikke et token
        if (isOpenEndpoint(allowedRoles)) {
            return;
        }

        // Kontrollér token og gem brugeren på requesten
        dk.bugelhartmann.UserDTO user =
                validateAndGetUserFromToken(ctx);
        ctx.attribute("user", user);
    }


    public void authorize(Context ctx) {
        Set<RouteRole> allowedRoles = ctx.routeRoles();

        // Åbne endpoints kræver ikke en bestemt rolle
        if (isOpenEndpoint(allowedRoles)) {
            return;
        }

        dk.bugelhartmann.UserDTO user = ctx.attribute("user");

        if (user == null) {
            throw new ForbiddenResponse("You are not authorized to access this resource");
        }

        if (!userHasAllowedRole(user, allowedRoles)) {
            throw new ForbiddenResponse("You do not have the required role");
        }
    }


    private String createToken(String username, String role) {
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
                    new dk.bugelhartmann.UserDTO(username, Set.of(role));
            return tokenSecurity.createToken(tokenUser, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            logger.error("Could not create token", e);
            throw new ApiException(500, "Could not create token");
        }
    }

    private dk.bugelhartmann.UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);

        if (token == null) {
            throw new UnauthorizedResponse("Missing or invalid token");
        }

        return verifyToken(token);
    }
    private String getToken(Context ctx) {
        String header = ctx.header("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String[] parts = header.split(" ", 2);

        if (parts.length != 2 || parts[1].isBlank()) {
            return null;
        }

        return parts[1];
    }
    private dk.bugelhartmann.UserDTO verifyToken(String token) {
        String secret;

        if (System.getenv("DEPLOYED") != null) {
            secret = System.getenv("SECRET_KEY");
        } else {
            secret = Utils.getPropertyValue("SECRET_KEY", "config.properties");
        }

        try {
            if (!tokenSecurity.tokenIsValid(token, secret)
                    || !tokenSecurity.tokenNotExpired(token)) {
                throw new UnauthorizedResponse("Invalid or expired token");
            }

            return tokenSecurity.getUserWithRolesFromToken(token);

        } catch (Exception e) {
            throw new UnauthorizedResponse("Invalid or expired token");
        }
    }

    private boolean userHasAllowedRole(
            dk.bugelhartmann.UserDTO user,
            Set<RouteRole> allowedRoles) {

        if (user == null || user.getRoles() == null) {
            return false;
        }

        for (RouteRole role : allowedRoles) {
            if (user.getRoles().stream()
                    .anyMatch(userRole ->
                            userRole.equalsIgnoreCase(role.toString()))) {
                return true;
            }
        }

        return false;
    }
    private boolean isOpenEndpoint(Set<RouteRole> allowedRoles) {
        return allowedRoles == null
                || allowedRoles.isEmpty()
                || allowedRoles.stream()
                .anyMatch(role -> role.toString().equals("ANYONE"));
    }
}
