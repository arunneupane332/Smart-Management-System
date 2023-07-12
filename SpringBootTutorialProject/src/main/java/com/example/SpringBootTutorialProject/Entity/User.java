package com.example.SpringBootTutorialProject.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 30, message = "Name must be between 2 and 25 characters")
    private String name;

    @NotBlank(message = "Password is required")
//    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String role;
    @Lob
    @Column(nullable = true, length = 64)
    private byte[] imageurl;

    private String about;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Contract> contract = new ArrayList<>();

}
