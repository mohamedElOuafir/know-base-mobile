package com.servicebackend.repository;


import com.servicebackend.entity.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCollectionRepository extends JpaRepository<UserCollection,Long> {
    List<UserCollection> findByUserIdUser(Long userIdUser);
}
