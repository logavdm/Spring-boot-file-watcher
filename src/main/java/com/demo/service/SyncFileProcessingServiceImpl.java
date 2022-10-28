package com.demo.service;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.demo.config.FileProcessingProps;
import com.demo.entity.AssesmentEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SyncFileProcessingServiceImpl implements FileProcessingService {

	private final FileProcessingProps props;
	
	private final AssesmentService assesmentService;

	@Override
	public synchronized void processFile(File file) {
		try {
			
			if (!checkAllowedFileFormate(file)) {
				log.info("Not allowed file formate moved to error dir");								
				Files.move(file.toPath(), Path.of(props.getErrorDir() + File.separator + generateFileName(file)),ATOMIC_MOVE, REPLACE_EXISTING);
				return;
			}	
			
			if(!processExcelFile(file)) {
				log.info("Exception occured when processing excel file moved to error dir");
				Files.move(file.toPath(), Path.of(props.getErrorDir() + File.separator + generateFileName(file)),ATOMIC_MOVE, REPLACE_EXISTING);
				return;
			}
			
			Files.move(file.toPath(), Path.of(props.getCompletedDir() + File.separator + generateFileName(file)),ATOMIC_MOVE, REPLACE_EXISTING);			
			log.info("File process success");
			
		} catch (IOException e) {
			log.error("Exception occured when file processing :: {}",e.getMessage());
		} 
	}

	public boolean checkAllowedFileFormate(File file) {
		boolean isAllowedFileFormate = false;
		try {
			String fileExtension = FilenameUtils.getExtension(file.getName());
			isAllowedFileFormate = props.getAllowedFileFormate().contains(fileExtension) ? true : false;
		} catch (Exception e) {
			log.error("Exception occured when check allowed file formate");
		}
		return isAllowedFileFormate;
	}
	
	private String generateFileName(File file) {		
		return FilenameUtils.removeExtension(file.getName())+"-"+System.currentTimeMillis()+"."+FilenameUtils.getExtension(file.getName());
	}

	private boolean processExcelFile(File file) {
		boolean isProcessed=false;
		try(XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {		
			XSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet.getRow(i);				
				AssesmentEntity entity=new AssesmentEntity();
				entity.setAit(row.getCell(0).getStringCellValue());
				entity.setAppName(row.getCell(1).getStringCellValue());
				entity.setProxy(row.getCell(2).getStringCellValue());
				assesmentService.saveAssesment(entity);
			}
			isProcessed=true;
		} catch (Exception e) {
			log.error("Exception occured when read excel file :: {}",e.getMessage());
		}
		
		return isProcessed;
	}

}
