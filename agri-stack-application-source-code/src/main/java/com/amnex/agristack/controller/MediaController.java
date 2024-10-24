package com.amnex.agristack.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.MediaMasterV2;
import com.amnex.agristack.service.MessageConfigurationService;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.amnex.agristack.centralcore.dao.PredictionDAO;
import com.amnex.agristack.dao.MediaResponse;
import com.amnex.agristack.dao.NotSupported;
import com.amnex.agristack.dao.UploadMediaResponse;
import com.amnex.agristack.entity.MediaMaster;
import com.amnex.agristack.service.CloudStorageService;
import com.amnex.agristack.service.MediaMasterService;
import com.amnex.agristack.utils.CustomMessages;

import io.swagger.annotations.ApiOperation;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
@RestController
@RequestMapping("/media")
public class MediaController {

	@Autowired
	private MediaMasterService mediaMasterService;

	@Value("${media.folder.image}")
	private String folderImage;
	@Value("${media.folder.audio}")
	private String folderAudio;
	@Value("${media.folder.video}")
	private String folderVideo;

	@Value("${media.folder.document}")
	private String folderDocument;

	@Value("${media.url.path}")
	private String urlPath;

	@Value("${media.virtual.url}")
	private String virtualUrl;

	@Value("${file.upload-dir}")
	private String basePath;

	@Value("${app.datastore.networktype}")
	private int datastoreType;

	@Autowired
	private CloudStorageService cloudStorageService;

	@Autowired
	private MessageConfigurationService messageConfigurationService;
	
	/**
	 * Upload an image file.
	 *
	 * @param file       The image file to be uploaded.
	 * @param folderName The name of the folder to store the image.
	 * @return The response containing the uploaded image information.
	 */
	@ApiOperation(value = "Upload Image")
	@PostMapping("/uploadFile")
	public MediaResponse uploadFile(@Valid @RequestParam("file") MultipartFile file,
			@RequestParam("folderName") String folderName) {
		String fileName = "";


		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}

		System.out.println("file.getContentType() "+file.getContentType());
		if (file.getContentType().startsWith("image/")) {
			// change activity code in below call
			MediaMaster mediaMaster=null;
			System.out.println("datastoreType "+datastoreType);
			switch (datastoreType) {
			case 1:
				mediaMaster= mediaMasterService.storeFile(file, folderName, folderImage, 0);
				break;
			case 2:
				break;
				
			case 3:
				mediaMaster=cloudStorageService.storeFile(file, folderName, folderImage, 0);
				break;
			default:
				
				break;
			}

			String DownloadUri = (virtualUrl + folderName + "/" + folderImage + "/" + mediaMaster.getMediaUrl());
			return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), DownloadUri,
					file.getContentType(), file.getSize(), HttpStatus.OK);

		} else {
			return new NotSupported("jpg/png file types are only supported", HttpStatus.NOT_ACCEPTABLE, fileName);
		}

	}


	@ApiOperation(value = "Upload Image")
	@PostMapping("/uploadFilev2")
	public MediaResponse uploadFileV2(@Valid @RequestParam("file") MultipartFile file,
									@RequestParam("folderName") String folderName,@RequestParam("userID") Integer userID) {
		String fileName = "";


		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}

		System.out.println("file.getContentType() "+file.getContentType());
		if (file.getContentType().startsWith("image/")) {
			// change activity code in below call
			MediaMasterV2 mediaMaster=null;
			System.out.println("datastoreType "+datastoreType);
			switch (datastoreType) {
				case 1:
					mediaMaster= mediaMasterService.storeFilev2(file, folderName, folderImage, 0,userID);
					break;
				case 2:
					break;

				case 3:
					//mediaMaster=cloudStorageService.storeFile(file, folderName, folderImage, 0);
					break;
				default:

					break;
			}

			String DownloadUri = (virtualUrl + folderName + "/" + folderImage + "/" + mediaMaster.getMediaUrl());
			return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), DownloadUri,
					file.getContentType(), file.getSize(), HttpStatus.OK);

		} else {
			return new NotSupported("jpg/png file types are only supported", HttpStatus.NOT_ACCEPTABLE, fileName);
		}

	}


	/**
	 * Upload a video file.
	 *
	 * @param file       The video file to be uploaded.
	 * @param folderName The name of the folder to store the video.
	 * @return The response containing the uploaded video information.
	 */
	@ApiOperation(value = "Upload Video")
	@PostMapping("/uploadVideo")
	public MediaResponse uploadVideo(@Valid @RequestParam("file") MultipartFile file,
			@RequestParam("folderName") String folderName) {
		String fileName = "";
		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}
		fileName = file.getOriginalFilename();
		if (file.getContentType().startsWith("video/")) {
			// change activity code in below call
			
			MediaMaster mediaMaster=null;
			switch (datastoreType) {
			case 1:
				 mediaMaster = mediaMasterService.storeFile(file, folderName, folderVideo, 0);
				break;
			case 2:
				break;
			case 3:
				mediaMaster=cloudStorageService.storeFile(file, folderName, folderVideo, 0);
				break;
			default:
				break;
			}
			

			String fileDownloadUri = ServletUriComponentsBuilder
					.fromHttpUrl(virtualUrl + folderName + "/" + folderVideo + "/" + mediaMaster.getMediaUrl())
					.toUriString();

			return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), fileDownloadUri,
					file.getContentType(), file.getSize(), HttpStatus.OK);

		} else {
			return new NotSupported("video file types are only supported", HttpStatus.NOT_ACCEPTABLE, fileName);
		}
	}

	/**
	 * Download a file.
	 *
	 * @param folderName The name of the folder where the file is stored.
	 * @param fileName   The name of the file to be downloaded.
	 * @param type       The type of the file.
	 * @param request    The HTTP request.
	 * @return The ResponseEntity containing the file to be downloaded.
	 * @throws IOException If an I/O error occurs.
	 */

	@ApiOperation(value = "Download Files")
	@GetMapping("/downloadFile/{folderName}/{type}/{fileName:.+}")
	public ResponseEntity<?> downloadFile(@PathVariable("folderName") String folderName,
			@PathVariable("fileName") String fileName, @PathVariable("type") String type,

			HttpServletRequest request) throws IOException {
		// Load file as Resource
		Resource resource =null;
		
		switch (datastoreType) {
		case 1:
			resource= mediaMasterService.loadFileAsResource(fileName, folderName, type);
			break;
		case 2:
			break;
		case 3:
			resource=cloudStorageService.loadFileAsResource(fileName, folderName, type);
			
			break;
		default:
			break;
		}
		
		byte[] media = IOUtils.toByteArray(resource.getInputStream());

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			// logger.info("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		// code when need to download then enable below code
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);

	}

	/**
	 * Get a file as a byte array.
	 *
	 * @param folderName The name of the folder where the file is stored.
	 * @param fileName   The name of the file to be retrieved.
	 * @param type       The type of the file.
	 * @param request    The HTTP request.
	 * @return The byte array of the file.
	 * @throws IOException If an I/O error occurs.
	 */

	@GetMapping(value = "/d/{folderName}/{type}/{fileName:.+}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] getFile(@PathVariable("folderName") String folderName,
			@PathVariable("fileName") String fileName, @PathVariable("type") String type,

			HttpServletRequest request) throws IOException {

		return IOUtils.toByteArray(mediaMasterService.loadFile(fileName, folderName, type));
	}

	/**
	 * Get media information by ID.
	 *
	 * @param mediaId The ID of the media.
	 * @return The ResponseEntity containing the media information.
	 */
	@ApiOperation(value = "get Media By ID")
	@GetMapping("/getMedia/{mediaId}")
	public ResponseEntity<?> getMedia(@PathVariable(value = "mediaId") String mediaId) {
		return mediaMasterService.getMedia(mediaId);
	}

	/**
	 * Update the media file.
	 *
	 * @param file    The file to be updated.
	 * @param mediaId The ID of the media.
	 * @return The response containing the updated media information.
	 */
	@ApiOperation(value = "Update Media")
	@PutMapping("/updateMedia")
	public MediaResponse updateFile(@Valid @RequestParam("file") MultipartFile file,
			@RequestParam("mediaId") String mediaId) {
		String fileName = "";
		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}

		MediaMaster mediaMaster=null;
		
		switch (datastoreType) {
		case 1:
			mediaMaster = mediaMasterService.updateFile(file, mediaId);		
			break;
		case 2:
			break;
		case 3:
			mediaMaster = cloudStorageService.updateFile(file, mediaId);
			break;
		default:
			
			break;
		}
		String DownloadUri = "";
		if (file.getContentType().startsWith("image/")) {
			DownloadUri = (virtualUrl + "/" + folderImage + "/" + mediaMaster.getMediaUrl());
		} else if (file.getContentType().startsWith("audio/")) {
			DownloadUri = (virtualUrl + "/" + folderAudio + "/" + mediaMaster.getMediaUrl());
		} else if (file.getContentType().startsWith("video/")) {
			DownloadUri = (virtualUrl + "/" + folderVideo + "/" + mediaMaster.getMediaUrl());
		} else {
			return new NotSupported("jpg/png file types are only supported", HttpStatus.NOT_ACCEPTABLE, fileName);
		}
		return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), DownloadUri,
				file.getContentType(), file.getSize(), HttpStatus.OK);
	}

	/**
	 * Upload a document file.
	 *
	 * @param file       The document file to be uploaded.
	 * @param folderName The name of the folder to store the document.
	 * @return The response containing the uploaded document information.
	 */

	@ApiOperation(value = "Upload Document")
	@PostMapping("/uploadDocument")
	public MediaResponse uploadDocument(@Valid @RequestParam("file") MultipartFile file,
			@RequestParam("folderName") String folderName) {
		String fileName = "";
		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}
		fileName = file.getOriginalFilename();
		if (validDocumentVerify(file)) {
			// change activity code in below call
			MediaMaster mediaMaster = null;
			switch (datastoreType) {
			case 1:
				mediaMaster = mediaMasterService.storeFile(file, folderName, folderDocument, 0);
				break;
			case 2:
				break;
			case 3:
				mediaMaster = cloudStorageService.storeFile(file, folderName, folderDocument, 0);
				break;
			default:
				
				mediaMaster = cloudStorageService.storeFile(file, folderName, folderDocument, 0);
				break;
			}
			
			String fileDownloadUri = ServletUriComponentsBuilder
					.fromHttpUrl(virtualUrl + folderName + "/" + folderDocument + "/" + mediaMaster.getMediaUrl())
					.toUriString();

			return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), fileDownloadUri,
					file.getContentType(), file.getSize(), HttpStatus.OK);

		} else {
			return new NotSupported("File format is not acceptable", HttpStatus.NOT_ACCEPTABLE, fileName);
		}
	}

	/**
	 * Verify if the document file format is valid.
	 *
	 * @param file The document file to be verified.
	 * @return True if the document format is valid, False otherwise.
	 */

	public boolean validDocumentVerify(MultipartFile file) {
		switch (file.getContentType()) {
		case "application/json":
			return true;
		case "application/pdf":
			return true;
		case "application/msword":
			return true;
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
			return true;
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
			return true;
		case "application/vnd.ms-word.document.macroEnabled.12":
			return true;
		case "application/vnd.ms-excel":
			return true;
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
			return true;
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":
			return true;
		case "application/vnd.ms-excel.sheet.macroEnabled.12":
			return true;
		case "application/vnd.ms-excel.template.macroEnabled.12":
			return true;
		case "application/vnd.ms-excel.addin.macroEnabled.12":
			return true;
		case "application/vnd.ms-excel.sheet.binary.macroEnabled.12":
			return true;
		case "application/vnd.ms-powerpoint":
			return true;
		case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
			return true;
		case "application/vnd.openxmlformats-officedocument.presentationml.template":
			return true;
		case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
			return true;
		case "application/vnd.ms-powerpoint.addin.macroEnabled.12":
			return true;
		case "application/vnd.ms-powerpoint.presentation.macroEnabled.12":
			return true;
		case "application/vnd.ms-powerpoint.template.macroEnabled.12":
			return true;
		case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12":
			return true;
		default:
			return false;
		}
	}

	/**
	 * Upload an Excel file.
	 *
	 * @param file       The Excel file to be uploaded.
	 * @param folderName The name of the folder to store the Excel file.
	 * @return The response containing the uploaded Excel information.
	 */
	@ApiOperation(value = "Upload Excel")
	@PostMapping("/uploadExcel")
	public MediaResponse uploadExcel(@Valid @RequestParam("file") MultipartFile file,
			@RequestParam("folderName") String folderName) {
		String fileName = "";
		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}
		fileName = file.getOriginalFilename();
		// change activity code in below call
		MediaMaster mediaMaster=null;
		switch (datastoreType) {
		case 1:
			mediaMaster = mediaMasterService.storeFile(file, folderName, folderDocument, 0);
			break;
		case 2:
			break;
		case 3:
			mediaMaster = cloudStorageService.storeFile(file, folderName, folderDocument, 0);
			break;
		default:
			
		
			break;
		}
		
		String fileDownloadUri = ServletUriComponentsBuilder
				.fromHttpUrl(virtualUrl + folderName + "/" + folderDocument + "/" + mediaMaster.getMediaUrl())
				.toUriString();

		return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), fileDownloadUri,
				file.getContentType(), file.getSize(), HttpStatus.OK);

	}

	/**
	 * Upload multiple document files.
	 *
	 * @param files      The array of document files to be uploaded.
	 * @param folderName The name of the folder to store the document files.
	 * @return The list of responses containing the uploaded document information.
	 */
	@ApiOperation(value = "Upload Documents")
	@PostMapping("/uploadDocuments")
	public List<MediaResponse> uploadMultipleDocument(@Valid @RequestParam("files") MultipartFile[] files,
			@RequestParam("folderName") String folderName) {

		return Arrays.asList(files).stream().map(file -> uploadDocument(file, folderName)).collect(Collectors.toList());
	}

	/**
	 * Upload multiple document files.
	 *
	 * @param file     The document file to be uploaded.
	 * @return The list of responses containing the uploaded document information.
	 */
    @PostMapping("/process")
    public ResponseEntity<String> processImage(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file to a temporary location
            File tempFile = File.createTempFile("temp", ".jpeg");
            file.transferTo(tempFile);

            // Perform OCR using Tesseract
            ITesseract tesseract = new Tesseract();
            tesseract.setLanguage("eng"); // Set the language
            tesseract.setOcrEngineMode(3); // Use the LSTM OCR engine
            tesseract.setTessVariable("user_defined_dpi", "300"); // Set the resolution (replace with your desired resolution)
            String text = null;
			try {
				text = tesseract.doOCR(tempFile);
			} catch (TesseractException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // Clean up: delete the temporary file
            tempFile.delete();

            return ResponseEntity.ok(text);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing the image.");
        }
    }

	/**
	 * Send email for downloaded media.
	 *
	 * @return The list of responses containing the downloaded media information.
	 */
	@GetMapping("/sendMailOfDownloadMedia")
	public ResponseModel sendMailOfDownloadMedia(){
		try{
			messageConfigurationService.sendEmailForDownloadFile("www.google.com");
			return new ResponseModel(true,
					null, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
					CustomMessages.METHOD_GET);
		} catch (Exception e){
			return new ResponseModel(null,
					e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Upload document files.
	 *
	 * @param file      The document file to be uploaded.
	 * @param folderName The name of the folder to store the document files.
	 * @return The list of responses containing the uploaded document information.
	 */
	@PostMapping("/uploadDocumentAndSendDownloadMail")
	public MediaResponse uploadDocumentAndSendDownloadMail(@Valid @RequestParam("file") MultipartFile file,
										@RequestParam("folderName") String folderName) throws IOException {
		String fileName = "";
		if (file.isEmpty()) {
			return new NotSupported("please select a file!", HttpStatus.OK, fileName);
		}
		fileName = file.getOriginalFilename();

		byte[] zipFile = createZipFile(file);

		if (validDocumentVerify(file)) {
			// change activity code in below call
			MediaMaster mediaMaster = null;
			switch (datastoreType) {
				case 1:
//					mediaMaster = mediaMasterService.storeFile(file, folderName, folderDocument, 0);
					mediaMaster = cloudStorageService.storeZipFile(file, folderName, folderDocument, 0, zipFile);
					break;
				case 2:
					break;
				case 3:
					mediaMaster = cloudStorageService.storeZipFile(file, folderName, folderDocument, 0, zipFile);
//					mediaMaster = cloudStorageService.storeFile(file, folderName, folderDocument, 0);
					break;
				default:
//					mediaMaster = cloudStorageService.storeZipFile(file, folderName, folderDocument, 0, zipFile);
					mediaMaster = cloudStorageService.storeFile(file, folderName, folderDocument, 0);
					break;
			}

			String fileDownloadUri = ServletUriComponentsBuilder
					.fromHttpUrl(virtualUrl + folderName + "/" + folderDocument + "/" + mediaMaster.getMediaUrl())
					.toUriString();

			messageConfigurationService.sendEmailForDownloadFile(fileDownloadUri);

			return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), fileDownloadUri,
					file.getContentType(), file.getSize(), HttpStatus.OK);

		} else {
			return new NotSupported("File format is not acceptable", HttpStatus.NOT_ACCEPTABLE, fileName);
		}
	}

	private byte[] createZipFile(MultipartFile file) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try(ZipOutputStream zos = new ZipOutputStream(os)){
			zos.putNextEntry(new ZipEntry(file.getOriginalFilename()));
			zos.write(file.getBytes());
			zos.closeEntry();
		}
		return os.toByteArray();
	}

}
