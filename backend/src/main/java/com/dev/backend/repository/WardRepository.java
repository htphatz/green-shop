package com.dev.backend.repository;

import com.dev.backend.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Integer> {
    Optional<Ward> findById(Integer id);

    @Query("SELECT w FROM Ward w WHERE w.district.id = :districtId")
    List<Ward> findByDistrictId(Integer districtId);
}
