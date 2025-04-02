package com.welfenhub.repositories;

import com.welfenhub.models.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface FileRepository extends JpaRepository<Files, Long> {

}
