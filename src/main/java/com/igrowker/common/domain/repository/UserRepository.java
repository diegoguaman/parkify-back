package com.igrowker.common.domain.repository;

import com.igrowker.common.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
