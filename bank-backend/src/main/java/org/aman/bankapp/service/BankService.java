package org.aman.bankapp.service;

import org.aman.bankapp.model.Session;
import org.aman.bankapp.model.User;
import org.aman.bankapp.repository.SessionRepository;
import org.aman.bankapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class BankService {
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;

    private static final Pattern USER_PATTERN = Pattern.compile("^[_\\-\\.0-9a-z]{1,127}$");
    private static final Pattern INT_PATTERN = Pattern.compile("^(0|[1-9][0-9]*)$");
    private static final Pattern FRAC_PATTERN = Pattern.compile("^[0-9]{2}$");

    public BankService(UserRepository userRepo, SessionRepository sessionRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
    }

    public String register(String username, String password, String balanceStr) {
        if(balanceStr != null){
            if (!validUser(username) || !validUser(password) || !validAmount(balanceStr)) return "invalid_input";
        }
        if (userRepo.findByUsername(username).isPresent()) return "invalid_input";

        BigDecimal balance = new BigDecimal(balanceStr);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setBalance(balance);
        userRepo.save(user);

        return "registered";
    }

    public String login(String username, String password) {
//        Optional<User> user = userRepo.findByUsernameAndPassword(username, password);
//        if (user.isEmpty()) return "invalid_input";


        //To do: change only for testing
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_app?useSSL=false&serverTimezone=UTC", "root", "")) {
            System.out.println("SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'");
            String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("in here");
                String token = UUID.randomUUID().toString();
                Session session = new Session();
                session.setToken(token);
                System.out.println(rs.getString(2) +  rs.getString(3));
                Optional<User> user = userRepo.findByUsernameAndPassword(rs.getString(2), rs.getString(3));
                session.setUser(user.get());
                sessionRepo.save(session);
                return token;
            } else {
                return "invalid_input";
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "invalid_input";
        }

    }

    public String deposit(String token, String amountStr) {
        if (!validAmount(amountStr)) return "invalid_input";
        Optional<Session> session = sessionRepo.findByToken(token);
        if (session.isEmpty()) return "invalid_input";

        User user = session.get().getUser();
        BigDecimal amount = new BigDecimal(amountStr);
        user.setBalance(user.getBalance().add(amount));
        userRepo.save(user);
        return user.getBalance().toPlainString();
    }

    public String withdraw(String token, String amountStr) {
        if (!validAmount(amountStr)) return "invalid_input";
        Optional<Session> session = sessionRepo.findByToken(token);
        if (session.isEmpty()) return "invalid_input";

        User user = session.get().getUser();
        BigDecimal amount = new BigDecimal(amountStr);
        if (user.getBalance().compareTo(amount) < 0) return "withdrawal_failed";

        user.setBalance(user.getBalance().subtract(amount));
        userRepo.save(user);
        return user.getBalance().toPlainString();
    }

    public String balance(String token) {
        Optional<Session> session = sessionRepo.findByToken(token);
        return session.map(s -> s.getUser().getBalance().toPlainString()).orElse("invalid_input");
    }

    private boolean validUser(String input) {
        if(!USER_PATTERN.matcher(input).matches()) {
            System.out.println("ss");
            System.out.println(input);
        }
        return input != null && USER_PATTERN.matcher(input).matches();
    }

    private boolean validAmount(String amountStr) {
        try {
            if (amountStr == null){
                System.out.println("amt1 " + amountStr +" " + (amountStr == null) +" " + !amountStr.contains("."));
                return false;
            }
            if(amountStr.contains(".")){
                String[] parts = amountStr.split("\\.");
                if (parts.length != 2){
                    System.out.println("amt2");
                    return false;
                }
                if (!INT_PATTERN.matcher(parts[0]).matches()) {
                    System.out.println("amt3");
                    return false;
                }
                if (!FRAC_PATTERN.matcher(parts[1]).matches()){
                    System.out.println("amt4");
                    return false;
                }
            }

            BigDecimal amount = new BigDecimal(amountStr);
            return amount.compareTo(BigDecimal.ZERO) >= 0 && amount.compareTo(new BigDecimal("4294967295.99")) <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    private String logintemp(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_app?useSSL=false&serverTimezone=UTC", "root", "Marmalude12!")) {
            // 🚨 HIGHLY UNSAFE: builds SQL string directly with user input
            String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return "Login successful";
            } else {
                return "Login failed";
            }
        } catch (SQLException e) {
            return "DB error";
        }
    }
}