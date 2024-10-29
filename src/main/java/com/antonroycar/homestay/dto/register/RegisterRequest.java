package com.antonroycar.homestay.dto.register;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "role")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegisterRequestCustomer.class, name = "CUSTOMER"),
        @JsonSubTypes.Type(value = RegisterRequestCrew.class, name = "CREW")
})
public abstract class RegisterRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;
}

