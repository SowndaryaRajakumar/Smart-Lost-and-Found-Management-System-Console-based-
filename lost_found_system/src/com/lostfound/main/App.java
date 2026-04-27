package com.lostfound.main;

import com.lostfound.model.*;
import com.lostfound.repository.ItemRepository;
import com.lostfound.repository.UserRepository;
import com.lostfound.service.*;
import com.lostfound.util.*;

import java.time.LocalDate;
import java.util.Scanner;
import java.io.Console;

public class App {

    static String loggedUser = null;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int ch;

        do {

            System.out.println("\n===== LOST & FOUND SYSTEM =====");
            System.out.println("1 Register");
            System.out.println("2 Login");
            System.out.println("3 Exit");
            System.out.print("Choice: ");

            ch = Integer.parseInt(sc.nextLine());

            switch (ch) {

                case 1 -> register(sc);

                case 2 -> login(sc);

                case 3 -> System.out.println("Exiting system...");
            }

        } while (ch != 3);
    }

    // ================= REGISTER =================

    static void register(Scanner sc) {

        System.out.println("\n--- User Registration ---");

        String user = ValidationUtil.getNonEmpty(sc, "Username: ");

        String password = ValidationUtil.strongPassword(sc);

        String phone = ValidationUtil.validPhone(sc);

        String email = ValidationUtil.validEmail(sc);

        String encryptedPassword = SecurityUtil.encrypt(password);

        User u = new User(user, encryptedPassword, phone, email, "USER");

        u.setAuditInfo(user);

        AuthService.register(u);
    }

    // ================= LOGIN =================

    static void login(Scanner sc) {

        System.out.println("\n--- Login ---");

        String user = ValidationUtil.getNonEmpty(sc, "Username: ");

        String pass;

        Console console = System.console();

        if (console != null) {

            char[] passChars = console.readPassword("Password: ");
            pass = new String(passChars);

        } else {

            System.out.print("Password: ");
            pass = sc.nextLine();
        }

        if (AuthService.login(user, pass)) {

            loggedUser = user;

            System.out.println("Login successful.");

            User u = AuthService.getUser(user);

            if (u != null && "ADMIN".equalsIgnoreCase(u.getRole())) {
                adminDashboard(sc);
            } else {
                dashboard(sc);
            }

        } else {

            System.out.println("Invalid username or password.");
        }
    }

    // ================= DASHBOARD =================

    static void dashboard(Scanner sc) {

        int op;

        do {

            System.out.println("\n===== USER DASHBOARD =====");
            System.out.println("1 Report Lost Item");
            System.out.println("2 Report Found Item");
            System.out.println("3 View Lost Items");
            System.out.println("4 Match Lost & Found Items");
            System.out.println("5 System Statistics");
            System.out.println("6 Logout");
            System.out.print("Choice: ");

            op = Integer.parseInt(sc.nextLine());

            switch (op) {

                case 1 -> reportLost(sc);

                case 2 -> reportFound(sc);

                case 3 -> ItemService.viewLostItems();

                case 4 -> matchItems(sc);

                case 5 -> StatisticsService.showStats();

                case 6 -> {
                    loggedUser = null;
                    System.out.println("Logged out.");
                }

                default -> System.out.println("Invalid choice.");
            }

        } while (op != 6);
    }

    // ================= ADMIN DASHBOARD =================

    static void adminDashboard(Scanner sc) {

        int op;

        do {

            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1 View All Users");
            System.out.println("2 View Lost Items");
            System.out.println("3 View Found Items");
            System.out.println("4 Match Lost & Found Items");
            System.out.println("5 System Statistics");
            System.out.println("6 Logout");
            System.out.print("Choice: ");

            op = Integer.parseInt(sc.nextLine());

            switch (op) {

                case 1 -> {
                    System.out.println("\n--- Registered Users ---");
                    UserRepository.getUsers().forEach(u ->
                            System.out.println(u.getUsername() + " (" + u.getRole() + ")"));
                }

                case 2 -> ItemService.viewLostItems();

                case 3 -> ItemService.viewFoundItems();

                case 4 -> matchItems(sc);

                case 5 -> StatisticsService.showStats();

                case 6 -> {
                    loggedUser = null;
                    System.out.println("Logged out.");
                }

                default -> System.out.println("Invalid choice.");
            }

        } while (op != 6);
    }

    // ================= REPORT LOST =================

    static void reportLost(Scanner sc){

    System.out.println("\n--- Report Lost Item ---");

    String name =
            ValidationUtil.getNonEmpty(sc,"Item Name: ");

    String date =
            ValidationUtil.validDate(sc).toString();

    String marks =
            ValidationUtil.getNonEmpty(sc,"Identifying Marks: ");

    String place =
            ValidationUtil.getNonEmpty(sc,"Place Lost: ");

    String contact =
            ValidationUtil.validPhone(sc);

    String imagePath =
            ValidationUtil.getNonEmpty(sc,"Image Path: ");

    String description =
            ValidationUtil.getNonEmpty(sc,"Additional Message: ");

    LostItem item = new LostItem(
            ItemRepository.getNextId(),
            name,
            date,
            marks,
            place,
            contact,
            imagePath,
            description,
            loggedUser
    );

    ItemService.addLostItem(item);
}
    // ================= REPORT FOUND =================

    static void reportFound(Scanner sc) {

        System.out.println("\n--- Report Found Item ---");

        String name = ValidationUtil.getNonEmpty(sc, "Item Name: ");

        LocalDate date = ValidationUtil.validDate(sc);

        String category = ValidationUtil.getNonEmpty(sc, "Category: ");

        String placeFound = ValidationUtil.getNonEmpty(sc, "Place Found: ");

        String finderContact = ValidationUtil.validPhone(sc);

        FoundItem item = new FoundItem(
                ItemRepository.getNextId(),
                name,
                date.toString(),
                category,
                placeFound,
                finderContact,
                loggedUser
        );

        ItemService.addFoundItem(item);
    }

    static void matchItems(Scanner sc) {
        System.out.println("\n--- Match Lost & Found Items ---");

        try {
            int lostId = Integer.parseInt(ValidationUtil.getNonEmpty(sc, "Lost Item ID: "));
            int foundId = Integer.parseInt(ValidationUtil.getNonEmpty(sc, "Found Item ID: "));

            ItemService.resolveMatch(lostId, foundId, loggedUser == null ? "system" : loggedUser);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric IDs.");
        }
    }
}
