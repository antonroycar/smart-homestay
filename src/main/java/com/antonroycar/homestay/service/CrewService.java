package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.login.LoginRequest;
import com.antonroycar.homestay.dto.register.RegisterRequestCrew;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Crew;
import com.antonroycar.homestay.entity.Role;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.repository.CrewRepository;
import com.antonroycar.homestay.security.BCrypt;
import com.antonroycar.homestay.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CrewService {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ValidationService validationService;

    public String registerCrew(RegisterRequestCrew register){
        validationService.validate(register);

        // Cek apakah username sudah ada di database
        if (accountRepository.existsByUsername(register.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Crew already exists");
        }

        // Buat instance Account dan set data yang diperlukan dari register request
        Account account = new Account();
        account.setUsername(register.getUsername());  // Set username dari request
        account.setPassword(BCrypt.hashpw(register.getPassword(), BCrypt.gensalt()));  // Hash password dari request
        account.setRole(Role.CREW);  // Set role sebagai customer
        accountRepository.save(account);

        Crew crew = new Crew();
        crew.setCrewName(register.getName());
        crew.setJobTitle(register.getJobTitle());
        crew.setAccount(account);
        crewRepository.save(crew);

        return "Crew registered successfully";
    }

    public UserDetails loadUserByUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(account.getUsername(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_CREW")));
    }
}
