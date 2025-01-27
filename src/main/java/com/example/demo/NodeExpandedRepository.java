package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeExpandedRepository extends JpaRepository<NodeExpanded, Long> {

    @Query("SELECT n FROM NodeExpanded n WHERE n.title = ?1")
    NodeExpanded findByTitle(String title);
}