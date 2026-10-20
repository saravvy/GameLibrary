package app.dtos;

import app.entities.User;

public record UserDTO(int id, String name, String role, String password) {



    public static UserDTO fromEntity(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRole().toString(),user.getPassword());
    }
}
