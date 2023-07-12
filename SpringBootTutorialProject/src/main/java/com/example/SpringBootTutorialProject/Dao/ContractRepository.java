package com.example.SpringBootTutorialProject.Dao;

import com.example.SpringBootTutorialProject.Entity.Contract;
import com.example.SpringBootTutorialProject.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ContractRepository extends JpaRepository<Contract,Integer> {
    @Query("From Contract as c where c.user.id =:userId")
    public Page<Contract> findContractByUser(@Param("userId") int userId, Pageable pageable);//this pageable has two things one current page and another contract per page
}
