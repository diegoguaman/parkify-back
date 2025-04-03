package com.igrowker.feature.parkify.repository;

import com.igrowker.feature.parkify.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
