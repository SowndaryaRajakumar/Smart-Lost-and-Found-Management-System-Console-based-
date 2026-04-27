package com.lostfound.service;

import com.lostfound.model.User;
import com.lostfound.repository.UserRepository;
import com.lostfound.util.SecurityUtil;

public class AuthService {

    public static void register(User user){

        if(UserRepository.findUser(user.getUsername())!=null){

            System.out.println("Username already exists");
            return;
        }

        UserRepository.addUser(user);

        System.out.println("Registration successful");
    }

    public static boolean login(String username,String password){

        User user = UserRepository.findUser(username);

        if(user==null)
            return false;

        String encrypted = SecurityUtil.encrypt(password);

        return user.getPassword().equals(encrypted);
    }

    public static User getUser(String username) {
        return UserRepository.findUser(username);
    }

    static {
        if (UserRepository.findUser("admin") == null) {
            User admin = new User("admin",
                    SecurityUtil.encrypt("Admin@123"),
                    "9999999999",
                    "admin@system.com",
                    "ADMIN");
            admin.setAuditInfo("system");
            UserRepository.addUser(admin);
        }
    }
}