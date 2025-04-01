package com.welfenhub.repositories;

import com.welfenhub.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByCourse(String course);
    Post findTopByCourseOrderByCreatedAtDesc(String course);
    List<Post> findBySubjectOrderByCreatedAtDesc(String subject);
    List<Post> findByTitleContainingIgnoreCase(String title);

    // Gruppierung nach Tag (PostgreSQL-kompatibel)
    @Query("SELECT EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt), EXTRACT(DAY FROM p.createdAt), COUNT(p) " +
            "FROM Post p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt), EXTRACT(DAY FROM p.createdAt) " +
            "ORDER BY EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt), EXTRACT(DAY FROM p.createdAt)")
    List<Object[]> countPostsByDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Gruppierung nach Monat (PostgreSQL-kompatibel)
    @Query("SELECT EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt), COUNT(p) " +
            "FROM Post p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt) " +
            "ORDER BY EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt)")
    List<Object[]> countPostsByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Gruppierung nach Stunde (PostgreSQL-kompatibel)
    @Query("SELECT EXTRACT(HOUR FROM p.createdAt), COUNT(p) " +
            "FROM Post p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(HOUR FROM p.createdAt) " +
            "ORDER BY EXTRACT(HOUR FROM p.createdAt)")
    List<Object[]> countPostsPerHour(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
