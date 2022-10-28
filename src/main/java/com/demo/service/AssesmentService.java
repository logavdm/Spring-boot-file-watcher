package com.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.dto.AssesmentDto;
import com.demo.entity.AssesmentEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssesmentService {

	private final AssesmentDto assesmentDto;
	
	
	@Transactional(readOnly = false)
	public AssesmentEntity saveAssesment(AssesmentEntity entity) {		
		return assesmentDto.save(entity);
	}
	
	
}
