package com.example.SpringBootTutorialProject.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @ManyToOne
    private User user;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Cid;
    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private String lastName;
    @Column(nullable=false)
    private String work;
    @Column(unique = true)
    private String email;
    @Column(nullable=false)
    private String phone;

    @Column(length = 500)
    private String description;

    @Override
    public boolean equals(Object obj) {
        return this.Cid==((Contract)obj).getCid();
    }
}
