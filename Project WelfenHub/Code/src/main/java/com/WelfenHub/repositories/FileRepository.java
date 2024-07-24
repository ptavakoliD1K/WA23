package com.WelfenHub.repositories;

import com.WelfenHub.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserId(Long userId);
}
