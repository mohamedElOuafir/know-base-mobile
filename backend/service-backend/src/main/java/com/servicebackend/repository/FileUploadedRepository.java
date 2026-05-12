package com.servicebackend.repository;

import com.servicebackend.entity.FileUploaded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadedRepository extends JpaRepository<FileUploaded,Long> {
    List<FileUploaded> findByCollectionIdCollection(Long idCollection);
    Long countByCollectionUserIdUser(Long idUser);
}
