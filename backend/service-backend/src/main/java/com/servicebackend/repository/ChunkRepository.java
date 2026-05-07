package com.servicebackend.repository;


import com.servicebackend.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChunkRepository extends JpaRepository<Chunk,Long> {
}
