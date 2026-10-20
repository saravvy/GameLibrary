package app.entities;

import java.util.Set;

public interface ISecurityUser {
    String getRoleAsStrings();
    boolean verifyPassword(String pw);



}
