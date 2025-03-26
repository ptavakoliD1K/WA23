package com.welfenhub.repositories;

import com.welfenhub.models.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface FileRepository extends JpaRepository<Files, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO files VALUES (null, :fileName, :fileBytes, :semester, :module, :fachrichtung, :tag)", nativeQuery = true)
    void uploadFile(@Param("fileName") String fileName, @Param("fileBytes") byte[] fileBytes, @Param("semester") String semester, @Param("module") String module, @Param("fachrichtung") String fachrichtung, @Param("tag") String tag);

}
