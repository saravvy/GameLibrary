package app.entities;


import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "users")
public class User implements ISecurityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Library library;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wishlist wishlist;

    public User(String username, String password) {
        this.username = username;
        String salt = BCrypt.gensalt();
        this.password = BCrypt.hashpw(password, salt);
    }


    @Override
    public String getRoleAsStrings() {

        return this.getRole().toString();
    }

    @Override
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw,this.password);
    }




}
