package com.lostfound.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ValidationUtil {

    public static String getNonEmpty(Scanner sc, String msg) {

        String input;

        do {
            System.out.print(msg);
            input = sc.nextLine();
        } while (input.trim().isEmpty());

        return input;
    }

    public static String strongPassword(Scanner sc) {

        String password;

        while (true) {

            System.out.print("Enter Password: ");
            password = sc.nextLine();

            boolean length = password.length() >= 8;
            boolean upper = password.matches(".*[A-Z].*");
            boolean lower = password.matches(".*[a-z].*");
            boolean digit = password.matches(".*\\d.*");
            boolean symbol = password.matches(".*[@#$%^&+=!].*");

            if (length && upper && lower && digit && symbol)
                return password;

            System.out.println("\nWeak password detected!");
            System.out.println("Password must contain:");
            System.out.println("• Minimum 8 characters");
            System.out.println("• Uppercase letter");
            System.out.println("• Lowercase letter");
            System.out.println("• Number");
            System.out.println("• Special symbol");

            System.out.println("\nSuggested strong password: " + generateSuggestedPassword());
        }
    }

    private static String generateSuggestedPassword() {

        String base = "Secure@";
        int num = (int) (Math.random() * 900 + 100);

        return base + num;
    }

    public static String validPhone(Scanner sc) {

        String phone;

        while (true) {

            System.out.print("Phone (10 digits): ");
            phone = sc.nextLine();

            if (phone.matches("[6-9]\\d{9}"))
                return phone;

            System.out.println("Invalid phone number.");
        }
    }

    public static String validEmail(Scanner sc) {

        String email;

        while (true) {

            System.out.print("Email: ");
            email = sc.nextLine();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                return email;

            System.out.println("Invalid email format.");
        }
    }

    public static LocalDate validDate(Scanner sc) {

        DateTimeFormatter format =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

        while (true) {

            System.out.print("Date (DD-MM-YYYY): ");
            String input = sc.nextLine();

            try {

                LocalDate date =
                        LocalDate.parse(input, format);

                if (date.isAfter(LocalDate.now())) {

                    System.out.println("Future date not allowed.");
                    continue;
                }

                return date;

            } catch (Exception e) {

                System.out.println("Invalid date format.");
            }
        }
    }
}