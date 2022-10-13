package com.assignment.postblog.repository;

import com.assignment.postblog.model.Member;
import com.assignment.postblog.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
}
