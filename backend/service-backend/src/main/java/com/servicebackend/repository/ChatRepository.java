package com.servicebackend.repository;


import com.servicebackend.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserCollectionIdCollection(Long idCollection);
    Long countByUserCollectionUserIdUser(Long idUser);
}
