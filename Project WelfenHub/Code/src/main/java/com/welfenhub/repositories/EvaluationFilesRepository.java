package com.welfenhub.repositories;

import com.welfenhub.models.EvaluationFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface EvaluationFilesRepository extends JpaRepository<EvaluationFiles, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO evaluation_files VALUES (null, :content, '.pdf', :date)", nativeQuery = true)
    void saveFileToDatabase(@Param("content") byte[] content, @Param("date") String date);

    @Query("SELECT e.content FROM EvaluationFiles e WHERE e.date = :date")
    List<byte[]> getFileFromDatabase(@Param("date") String date);

}
