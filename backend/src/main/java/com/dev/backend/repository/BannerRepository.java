package com.dev.backend.repository;

import com.dev.backend.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, String> {
    Optional<Banner> findById(String id);
    Page<Banner> findAll(Pageable pageable);
}
