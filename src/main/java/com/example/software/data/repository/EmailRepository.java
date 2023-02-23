package com.example.software.data.repository;

import com.example.software.data.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {

    @Query("select c from Email c " +
            "where lower(c.cc) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.to) like lower(concat('%', :searchTerm, '%'))")
    List<Email> search(@Param("searchTerm") String searchTerm);
}
