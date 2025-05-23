package org.aman.bankapp.controller;

import org.aman.bankapp.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("initial_balance") String balance) {
        String result = bankService.register(username, password, balance);
        HttpStatus status = result.equals("registered") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        System.out.println(result + " " + status);
        return new ResponseEntity<>(result, status);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String username,
            @RequestParam String password) {
        String token = bankService.login(username, password);
        HttpStatus status = token.equals("invalid_input") ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
        return new ResponseEntity<>(token, status);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestParam String token,
            @RequestParam String amount) {
        String result = bankService.deposit(token, amount);
        HttpStatus status = result.equals("invalid_input") ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestParam String token,
            @RequestParam String amount) {
        String result = bankService.withdraw(token, amount);
        HttpStatus status = result.equals("invalid_input") ? HttpStatus.BAD_REQUEST
                : result.equals("withdrawal_failed") ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }

    @GetMapping("/balance")
    public ResponseEntity<String> balance(@RequestParam String token) {
        String result = bankService.balance(token);
        HttpStatus status = result.equals("invalid_input") ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }
}
