package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.register.RegisterRequest;
import com.antonroycar.homestay.dto.register.RegisterRequestCrew;
import com.antonroycar.homestay.dto.register.RegisterRequestCustomer;
import com.antonroycar.homestay.service.CrewService;
import com.antonroycar.homestay.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CrewService crewService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String response;

        if (registerRequest instanceof RegisterRequestCustomer customerRequest) {
            response = customerService.registerCustomer(customerRequest);
        } else if (registerRequest instanceof RegisterRequestCrew crewRequest) {
            response = crewService.registerCrew(crewRequest);
        } else {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

