package com.dev.backend.repository;

import com.dev.backend.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    Optional<District> findById(Integer id);

    @Query("SELECT d FROM District d WHERE d.province.id = :provinceId")
    List<District> findByProvinceId(Integer provinceId);
}
