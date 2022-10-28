package com.demo.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.entity.AssesmentEntity;

@Repository
public interface AssesmentDto extends JpaRepository<AssesmentEntity,Long>{

}
