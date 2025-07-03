package com.example.iccsoft_user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.iccsoft_user.models.Employe;

@Repository
public interface UserRepository extends JpaRepository<Employe, Long> {
	
}
