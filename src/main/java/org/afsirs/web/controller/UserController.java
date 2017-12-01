package org.afsirs.web.controller;

import org.afsirs.web.dao.UserDAO;
import org.afsirs.web.dao.bean.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserController {

//    private static final String defSalt = BCrypt.gensalt();
//    private static final String hashedpass = getHashedPassword();
    // Authenticate the user by hashing the inputted password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public static boolean register(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (UserDAO.getUserByUsername(username) != null) {
            return false;
        } else {
            return UserDAO.registerUser(new User(username, password));
        }
    }
    
    public static boolean authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        User user = UserDAO.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());
        
        return hashedPassword.equals(user.getHashedPassword());
    }

    // This method doesn't do anything, it's just included as an example
    public static void setPassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newPassword, newSalt);
            // Update the user salt and password
        }
    }
    
//    private static String getSalt() {
//        return defSalt;
//    }
//    
//    private static String getHashedPassword() {
//        return BCrypt.hashpw("123", defSalt);
//    }
}
