package com.welfenhub.repositories;

import com.welfenhub.dto.JobDTO;
import com.welfenhub.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Job VALUES (null, :title, :content, :url, :date)", nativeQuery = true)
    void saveJob(@Param("title") String title, @Param("content") String content, @Param("url") String url, @Param("date") String date);

    @Query("SELECT new com.welfenhub.dto.JobDTO(j.title, j.content, j.url) FROM Job j ORDER BY j.id DESC")
    List<JobDTO> getJobs();

    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.title = :title AND j.content = :content")
    void removeJob(@Param("title") String title, @Param("content") String content);
}
