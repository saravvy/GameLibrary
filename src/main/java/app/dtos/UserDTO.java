package app.dtos;

import app.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(int id, String name, String role, String password) {



    public static UserDTO fromEntity(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRoleAsStrings(),null);
    }
}
