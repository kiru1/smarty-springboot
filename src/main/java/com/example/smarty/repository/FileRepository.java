package com.example.smarty.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smarty.model.File;

public interface FileRepository extends JpaRepository<File, Long>{
	
}
