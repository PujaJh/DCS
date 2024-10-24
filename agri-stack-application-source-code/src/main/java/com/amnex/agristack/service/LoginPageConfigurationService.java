package com.amnex.agristack.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import com.amnex.agristack.dao.LoginPageConfigurationDto;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.LoginPageConfiguration;
import com.amnex.agristack.repository.LoginPageConfigurationRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.regex.Pattern;
import com.amnex.agristack.utils.CustomMessages;
/*
 * @author janmaijaysingh.bisen	
 */
/**
 * 
 * @author janmaijaysingh.bisen
 *
 */
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class LoginPageConfigurationService {

	@Value("${app.datastore.networktype}")
	private Integer storageType;

	@Value("${file.upload-dir}")
	private String localStoragePath;

	private LoginPageConfigurationRepository loginPageConfigurationRepository;

	public LoginPageConfigurationService(LoginPageConfigurationRepository loginPageConfigurationRepository) {
		this.loginPageConfigurationRepository = loginPageConfigurationRepository;
	}
/**
 * save or update login page configuration for state
 * @param loginPageConfigurationDto
 * @return
 * @throws IOException
 */
//	public ResponseModel saveOrUpdateLoginPageConfiguration(LoginPageConfigurationDto loginPageConfigurationDto)
//			throws IOException {
//		
//			// Define allowed extensions and MIME types
//		 List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
//		 List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
//		 
//		 // Define maximum file size in bytes
//		    long maxFileSize = 10 * 1024 * 1024; // 10MB
//
//		String message = CustomMessages.RECORD_ADD;
//		LoginPageConfiguration loginPageConfiguration = new LoginPageConfiguration();
//		if (loginPageConfigurationDto.getId() != null) {
//			Optional<LoginPageConfiguration> loginPageConfiguratioOp = loginPageConfigurationRepository
//					.findByStateLgdCodeAndLandingPageFor(loginPageConfigurationDto.getStateLgdCode(),loginPageConfigurationDto.getLandingPageFor());
//			if (loginPageConfiguratioOp.isPresent()) {
//				loginPageConfiguration = loginPageConfiguratioOp.get();
//				message = CustomMessages.RECORD_UPDATE;
//			}
//		}
//
//		if (loginPageConfigurationDto.getLogoImage() != null && !loginPageConfigurationDto.getLogoImage().isEmpty()) {
//			MultipartFile logoFile = loginPageConfigurationDto.getLogoImage();
//			
//			if (!isValidFile(logoFile, allowedExtensions, allowedMimeTypes, maxFileSize)) {
//	            return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILURE, CustomMessages.METHOD_POST);
//	        }
//
//			InputStream in = null;
//			String destDir = localStoragePath + File.separator + "Logos";
//			File f = new File(destDir);
//			f.mkdir();
//			if (storageType == 1) {
//
//			}
//			byte[] buffer = new byte[1024];
//			// if (extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("csv"))
//			// {
//			in = file.getInputStream();
//			File logoImageFile = new File(destDir, file.getOriginalFilename());
//
//			BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
//
//			FileOutputStream fos;
//
//			fos = new FileOutputStream(logoImageFile);
//
//			int len, i = 0;
//			while ((len = bufferedInputStream.read(buffer)) > 0) {
//				fos.write(buffer, 0, len);
//			}
//			fos.close();
//			loginPageConfiguration.setLogoImagePath(logoImageFile.getAbsolutePath());
//
//		}
//		if (loginPageConfigurationDto.getBackgroundImage() != null) {
//			MultipartFile bgFile = loginPageConfigurationDto.getBackgroundImage();
//			
//			if (!isValidFile(bgFile, allowedExtensions, allowedMimeTypes, maxFileSize)) {
//	            return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILURE, CustomMessages.METHOD_POST);
//	        }
//
//			InputStream in = null;
//			String destDir = localStoragePath + File.separator + "BackGround";
//			File f = new File(destDir);
//			f.mkdir();
//			if (storageType == 1) {
//
//			}
//			byte[] buffer = new byte[1024];
//			// if (extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("csv"))
//			// {
//			in = file.getInputStream();
//			File backGroundImageFile = new File(destDir, file.getOriginalFilename());
//			BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
//
//			FileOutputStream fos;
//
//			fos = new FileOutputStream(backGroundImageFile);
//
//			int len, i = 0;
//			while ((len = bufferedInputStream.read(buffer)) > 0) {
//				fos.write(buffer, 0, len);
//			}
//			fos.close();
//			loginPageConfiguration.setBackgroundImagePath(backGroundImageFile.getAbsolutePath());
//		}
//		if (!StringUtils.isEmpty(loginPageConfigurationDto.getLandingPageTitleContent())) {
//			loginPageConfiguration.setLandingPageTitleContent(loginPageConfigurationDto.getLandingPageTitleContent());
//		}
//		if (!StringUtils.isEmpty(loginPageConfigurationDto.getLandingPageDescContent())) {
//			loginPageConfiguration.setLandingPageDescContent(loginPageConfigurationDto.getLandingPageDescContent());
//		}
//
//		if (loginPageConfigurationDto.getStateLgdCode() != null) {
//
//			loginPageConfiguration.setStateLgdCode(loginPageConfigurationDto.getStateLgdCode());
//		}
//		loginPageConfiguration.setLandingPageFor(loginPageConfigurationDto.getLandingPageFor());
//		loginPageConfiguration = loginPageConfigurationRepository.save(loginPageConfiguration);
//
//		// return null;
//
//		return new ResponseModel(loginPageConfiguration, message, CustomMessages.ADD_SUCCESSFULLY,
//				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
//	}
//	
//	private boolean isValidFile(MultipartFile file, List<String> allowedExtensions, List<String> allowedMimeTypes, long maxFileSize) {
//	    // File size check
//	    if (file.getSize() > maxFileSize) {
//	        return false;
//	    }
//
//	    // File extension check
//	    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
//	    if (!allowedExtensions.contains(fileExtension)) {
//	        return false;
//	    }
//
//	    // MIME type check
//	    String fileContentType = file.getContentType();
//	    if (!allowedMimeTypes.contains(fileContentType)) {
//	        return false;
//	    }
//
//	    // Filename check
//	    String fileName = file.getOriginalFilename();
//	    if (fileName == null || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\") || fileName.contains("\0")) {
//	        return false;
//	    }
//
//	    return true;
//	}
	
	public ResponseModel saveOrUpdateLoginPageConfiguration(LoginPageConfigurationDto loginPageConfigurationDto) throws IOException {
	    // Define allowed extensions and MIME types
	    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
	    List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
	    
	    // Define maximum file size in bytes
	    long maxFileSize = 10 * 1024 * 1024; // 10MB

	    String message = CustomMessages.RECORD_ADD;
	    LoginPageConfiguration loginPageConfiguration = new LoginPageConfiguration();

	    if (loginPageConfigurationDto.getId() != null) {
	        Optional<LoginPageConfiguration> loginPageConfiguratioOp = loginPageConfigurationRepository
	                .findByStateLgdCodeAndLandingPageFor(loginPageConfigurationDto.getStateLgdCode(), loginPageConfigurationDto.getLandingPageFor());
	        if (loginPageConfiguratioOp.isPresent()) {
	            loginPageConfiguration = loginPageConfiguratioOp.get();
	            message = CustomMessages.RECORD_UPDATE;
	        }
	    }

	    // Validate logo image
	    if (loginPageConfigurationDto.getLogoImage() != null && !loginPageConfigurationDto.getLogoImage().isEmpty()) {
	        MultipartFile logoFile = loginPageConfigurationDto.getLogoImage();
	        if (!isValidFile(logoFile, allowedExtensions, allowedMimeTypes, maxFileSize)) {
	            return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILURE, CustomMessages.METHOD_POST);
	        }
	        String logoImagePath = saveFile(logoFile, "Logos");
	        loginPageConfiguration.setLogoImagePath(logoImagePath);
	    }

	    // Validate background image
	    if (loginPageConfigurationDto.getBackgroundImage() != null && !loginPageConfigurationDto.getBackgroundImage().isEmpty()) {
	        MultipartFile bgFile = loginPageConfigurationDto.getBackgroundImage();
	        if (!isValidFile(bgFile, allowedExtensions, allowedMimeTypes, maxFileSize)) {
	            return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_EXTENSION_NOT_ALLOWED, CustomMessages.METHOD_POST);
	        }
	        String bgImagePath = saveFile(bgFile, "Background");
	        loginPageConfiguration.setBackgroundImagePath(bgImagePath);
	    }

	    // Set other properties
	    if (!StringUtils.isEmpty(loginPageConfigurationDto.getLandingPageTitleContent())) {
	        loginPageConfiguration.setLandingPageTitleContent(loginPageConfigurationDto.getLandingPageTitleContent());
	    }
	    if (!StringUtils.isEmpty(loginPageConfigurationDto.getLandingPageDescContent())) {
	        loginPageConfiguration.setLandingPageDescContent(loginPageConfigurationDto.getLandingPageDescContent());
	    }
	    if (loginPageConfigurationDto.getStateLgdCode() != null) {
	        loginPageConfiguration.setStateLgdCode(loginPageConfigurationDto.getStateLgdCode());
	    }
	    loginPageConfiguration.setLandingPageFor(loginPageConfigurationDto.getLandingPageFor());
	    loginPageConfiguration = loginPageConfigurationRepository.save(loginPageConfiguration);

	    return new ResponseModel(loginPageConfiguration, message, CustomMessages.GET_DATA_SUCCESS,
	            CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	private boolean isValidFile(MultipartFile file, List<String> allowedExtensions, List<String> allowedMimeTypes, long maxFileSize) {
	    // File size check
	    if (file.getSize() > maxFileSize) {
	        return false;
	    }

	    // File extension check
	    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
	    if (!allowedExtensions.contains(fileExtension)) {
	        return false;
	    }

	    // MIME type check
	    String fileContentType = file.getContentType();
	    if (!allowedMimeTypes.contains(fileContentType)) {
	        return false;
	    }

	    // Filename check
	    String fileName = file.getOriginalFilename();
	    if (fileName == null || Pattern.compile("[\\\\/:*?\"<>|]").matcher(fileName).find()) {
	        return false;
	    }
	    
	    // Check for double extensions or double dots
	    if (fileName.split("\\.").length > 2) {
	        return false;
	    }
	    
	    // Check for null bytes
	    if (fileName.contains("\0")) {
	        return false;
	    }


	    return true;
	}

	private String saveFile(MultipartFile file, String directoryName) throws IOException {
//	    String destDir = localStoragePath + File.separator + directoryName;
//	    File destDirFile = new File(destDir);
//	    destDirFile.mkdirs();
//
//	    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//	    String filePath = destDir + File.separator + fileName;
//	    Path path = Paths.get(filePath);
//	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//	    return filePath;
		
		String destDir = localStoragePath + File.separator + directoryName;
	    
	    // Create the destination directory if it doesn't exist
	    File destDirFile = new File(destDir);
	    destDirFile.mkdirs();

	    // Sanitize the file name
	    String originalFilename = file.getOriginalFilename();
	    String fileName = originalFilename != null ? FilenameUtils.getName(originalFilename) : "unknown";

	    // Construct the full file path
	    String filePath = destDir + File.separator + fileName;

	    // Copy the file to the destination directory
	    Path path = Paths.get(filePath);
	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	    return filePath; // Return the file path
	}

/**
 * fetch login page configuration for state and project type
 * @param stateLgdCode
 * @param landPageFor
 * @return
 * @throws IOException
 */
	public ResponseModel getLoginPageConfiguration(Long stateLgdCode, Integer landPageFor) throws IOException {
		Optional<LoginPageConfiguration> loOptional = loginPageConfigurationRepository
				.findByStateLgdCodeAndLandingPageFor(stateLgdCode, landPageFor);
		if (loOptional.isPresent()) {
			LoginPageConfiguration loginPageConfiguration = loOptional.get();
			if (loginPageConfiguration.getLogoImagePath() != null) {
				File logFile = new File(loginPageConfiguration.getLogoImagePath());
				loginPageConfiguration.setLogoImageInByte(Files.readAllBytes(logFile.toPath()));

			}
			if (loginPageConfiguration.getBackgroundImagePath() != null) {
				File bgFile = new File(loginPageConfiguration.getBackgroundImagePath());

				loginPageConfiguration.setBackgroundImageInByte(Files.readAllBytes(bgFile.toPath()));
			}

			return new ResponseModel(loginPageConfiguration, CustomMessages.SUCCESS, CustomMessages.ADD_SUCCESSFULLY,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} else {
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.GET_DATA_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_GET);
		}
	}

}
