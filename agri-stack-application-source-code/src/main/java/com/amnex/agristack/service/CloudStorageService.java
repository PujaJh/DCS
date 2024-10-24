/**
 * 
 */
package com.amnex.agristack.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.entity.MediaMaster;
import com.amnex.agristack.exception.MediaStorageException;
import com.amnex.agristack.exception.MyMediaNotFoundException;
import com.amnex.agristack.repository.MediaMasterRepository;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

/**
 * @author majid.belim
 *
 */
@Service
public class CloudStorageService {

	@Value("${gcp.bucket.name}")
	private String bucketName;

	@Autowired
	private Storage storage;
	@Value("${file.upload-dir}")
	private String path;
	private Path fileStorageLocation;
    @Value("${media.folder.image}")
    private String folderImage;
    @Value("${media.folder.audio}")
    private String folderAudio;
    @Value("${media.folder.video}")
    private String folderVideo;

    @Value("${media.folder.document}")
    private String folderDocument;
	@Autowired
	private MediaMasterRepository mediaMasterRepository;
	
	@Autowired
	ExceptionLogService exceptionLogService;


	/**
	 * Stores the uploaded file in the specified folder and subfolder.
	 *
	 * @param file          The uploaded file
	 * @param folder        The main folder to store the file
	 * @param subFolderPath The subfolder path inside the main folder
	 * @param activityCode  The activity code associated with the file
	 * @return The saved MediaMaster object
	 */
	public MediaMaster storeFile(MultipartFile file, String folder, String subFolderPath, Integer activityCode) {

						// create
		String fileName = reNameFileName(folder, StringUtils.cleanPath(file.getOriginalFilename()));
		String folderName = path + folder + "/" + subFolderPath+ "/" + fileName; // Replace with the name of the folder you want to
							
		try {

			if (fileName.contains("..")) {
				throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			BlobId blobId = BlobId.of(bucketName, folderName );
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
			storage.create(blobInfo, file.getBytes());
			MediaMaster mediaMaster = new MediaMaster();
			mediaMaster.setMediaType(file.getContentType());
			mediaMaster.setMediaUrl(fileName);
			mediaMaster.setActivityCode(activityCode);
			MediaMaster updatedMedia = mediaMasterRepository.save(mediaMaster);
			return updatedMedia;
//			
		} catch (Exception ex) {
			 
			exceptionLogService.addException("Cloud Storage service","storeFile" ,
					ex, null);
			ex.printStackTrace();
			throw new MediaStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	/**
	 * Generates a new file name by appending a random alphanumeric string to the
	 * original file name.
	 *
	 * @param folder   The folder name.
	 * @param filename The original file name.
	 * @return The new file name.
	 */
	private String reNameFileName(String folder, String filename) {
		String extension = Optional.of(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");

		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(8);

		for (int i = 0; i < 8; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));

		}
//		String timeStamp = String.valueOf(System.currentTimeMillis());
		String timeStamp = String.valueOf(System.nanoTime());
		String newFileName = folder + "_" + sb.toString() + "_" + timeStamp + "." + extension;
		return newFileName;
	}
	
	
    /**
     * Loads the file as a resource.
     *
     * @param fileName     The name of the file
     * @param folderName   The name of the main folder
     * @param type         The type of file (image, audio, video, document)
     * @return             The resource representing the file
     */
    public Resource loadFileAsResource(String fileName, String folderName, String type) {
        try {
        	
        	String fullPath = path + folderName+ "/" + type+"/" + fileName;
        	
            // Get the media file from Google Cloud Storage
            Blob blob = storage.get(BlobId.of(bucketName, fullPath));
            
            if (blob != null) {
                // Prepare the response with the media file content and headers
                byte[] content = blob.getContent();
                ByteArrayResource resource = new ByteArrayResource(content,fileName);
                
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new MyMediaNotFoundException("File not found " + fileName);
                }
            }

            throw new MyMediaNotFoundException("File not found " + fileName);
        } catch (Exception ex) {
            throw new MyMediaNotFoundException("File not found " + fileName, ex);
        }
    }

	/**
	 * @param file
	 * @param mediaId
	 * @return
	 */
	public MediaMaster updateFile(@Valid MultipartFile file, String mediaId) {
        String fileName = "";
        String mediaType = "";

        try {
            MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(mediaId);

            if (mediaMaster != null) {

                 // Delete the media file from Google Cloud Storage
                 boolean deleted = storage.delete(BlobId.of(bucketName, mediaMaster.getMediaUrl()));
                 if(deleted) {
                	 mediaMaster.setMediaUrl(null);	 
                 }
                

                
                MediaMaster updatedMediaMaster = mediaMasterRepository.save(mediaMaster);

                return updatedMediaMaster;
            } else {
                throw new MyMediaNotFoundException("File not found " + fileName);
            }

        } catch (Exception ex) {
            throw new MediaStorageException("Could not update file " + fileName + ". Please try again!", ex);
        }
	}



	public MediaMaster storeZipFile(MultipartFile file, String folder, String subFolderPath, Integer activityCode, byte[] zipFileBytes) {

		// create
		String fileName = reNameZipFileName(folder, StringUtils.cleanPath(file.getOriginalFilename()));
		String folderName = path + folder + "/" + subFolderPath+ "/" + fileName; // Replace with the name of the folder you want to

		try {

			if (fileName.contains("..")) {
				throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			BlobId blobId = BlobId.of(bucketName, folderName );
//			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/zip").build();

			storage.create(blobInfo, zipFileBytes);
			MediaMaster mediaMaster = new MediaMaster();
//			mediaMaster.setMediaType(file.getContentType());
			mediaMaster.setMediaType("application/zip");

			mediaMaster.setMediaUrl(fileName);
			mediaMaster.setActivityCode(activityCode);
			MediaMaster updatedMedia = mediaMasterRepository.save(mediaMaster);
			return updatedMedia;
//
		} catch (Exception ex) {

			exceptionLogService.addException("Cloud Storage service","storeFile" ,
					ex, null);
			ex.printStackTrace();
			throw new MediaStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	private String reNameZipFileName(String folder, String filename) {
		String extension = "zip";

		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(8);

		for (int i = 0; i < 8; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));

		}
//		String timeStamp = String.valueOf(System.currentTimeMillis());
		String timeStamp = String.valueOf(System.nanoTime());
		String newFileName = folder + "_" + sb.toString() + "_" + timeStamp + "." + extension;
		return newFileName;
	}

}
