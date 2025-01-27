package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long> {
    List<RoadmapNode> findByTitleContainingIgnoreCase(String query);
}