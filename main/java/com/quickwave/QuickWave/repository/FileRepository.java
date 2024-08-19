package com.quickwave.QuickWave.repository;


import com.quickwave.QuickWave.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  FileRepository extends JpaRepository<File, Long> {
}
