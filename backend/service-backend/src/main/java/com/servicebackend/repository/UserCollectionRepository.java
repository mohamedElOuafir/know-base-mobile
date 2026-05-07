package com.servicebackend.repository;


import com.servicebackend.entity.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCollectionRepository extends JpaRepository<UserCollection,Long> {
    List<UserCollection> findByUserIdUser(Long userIdUser);
}
