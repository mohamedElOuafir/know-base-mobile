package com.servicebackend.repository;

import com.servicebackend.entity.FileUploaded;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadedRepository extends JpaRepository<FileUploaded,Long> {
}
