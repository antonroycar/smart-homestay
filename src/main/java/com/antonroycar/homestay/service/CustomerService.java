package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.register.RegisterRequestCustomer;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Customer;
import com.antonroycar.homestay.entity.Role;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.repository.CustomerRepository;
import com.antonroycar.homestay.security.BCrypt;
import com.antonroycar.homestay.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public String registerCustomer(RegisterRequestCustomer register) {
        // Validasi input
        validationService.validate(register);

        // Cek apakah username sudah ada di database
        if (accountRepository.existsByUsername(register.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer already exists");
        }

        // Buat instance Account dan set data yang diperlukan dari register request
        Account account = new Account();
        account.setUsername(register.getUsername());  // Set username dari request
        account.setPassword(BCrypt.hashpw(register.getPassword(), BCrypt.gensalt()));  // Hash password dari request
        account.setRole(Role.CUSTOMER);  // Set role sebagai customer
        accountRepository.save(account);

        // Buat instance Customer dan set data yang diperlukan dari register request
        Customer customer = new Customer();
        customer.setCustomerName(register.getName());
        customer.setAge(register.getAge());
        customer.setAddress(register.getAddress());
        customer.setContactNumber(register.getContactNumber());
        customer.setGender(register.getGender());
        customer.setAccount(account);  // Hubungkan Customer dengan Account yang baru dibuat
        customerRepository.save(customer);

        return "Registered successfully";
    }
}

