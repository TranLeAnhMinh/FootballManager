package com.example.footballmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.PitchType;

@Repository
public interface PitchTypeRepository extends JpaRepository<PitchType, Short> {
}
