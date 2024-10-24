package com.amnex.agristack.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.LandParcelSurveyCropMediaMapping;
import com.amnex.agristack.entity.MediaMasterUserMapping;
import com.amnex.agristack.entity.MediaMasterV2;
import com.amnex.agristack.repository.MediaMasterRepositoryV2;
import com.amnex.agristack.repository.UserMediaMappingRepository;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.entity.MediaMaster;
import com.amnex.agristack.exception.MediaStorageException;
import com.amnex.agristack.exception.MyMediaNotFoundException;
import com.amnex.agristack.repository.MediaMasterRepository;

@Service
public class MediaMasterService {

    @Autowired
    private Environment environment;

    @Autowired
    private MediaMasterRepository mediaMasterRepository;

    @Autowired
    private MediaMasterRepositoryV2 mediaMasterRepositoryV2;

    @Autowired
    private UserMediaMappingRepository userMediaMappingRepository;

    private Path fileStorageLocation;

    @Value("${file.upload-dir}")
    private String path;

    @Value("${media.virtual.url}")
    private String virtualUrl;

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

    @Value("${app.datastore.networktype}")
    private int datastoreType;

    @Autowired
    private CloudStorageService cloudStorageService;
    
    
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

        String fileName = "";
        try {
            // Check if the file's name contains invalid characters

            MediaMaster mediaMaster = new MediaMaster();
            switch (datastoreType) {
                case 1:
                    this.fileStorageLocation = Paths.get(path + "/" + folder + "/" + subFolderPath).toAbsolutePath()
                            .normalize();
                    try {
                        Files.createDirectories(this.fileStorageLocation);
                        //System.out.println("fileStorageLocation "+fileStorageLocation);
                    } catch (Exception ex) {
                        throw new MediaStorageException(
                                "Could not create the directory where the uploaded files will be stored.",
                                ex);
                    }

                    fileName = reNameFileName(folder, StringUtils.cleanPath(file.getOriginalFilename()));
                    if (fileName.contains("..")) {
                        throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
                    }
                    Path targetLocation = this.fileStorageLocation.resolve(fileName);

                    String contentType = file.getContentType();
                    // InputStream fStream = file.getInputStream();
                    // try {
                    // Files.copy(fStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    // } catch (Exception e) {
                    // System.out.println(e.getMessage());
                    // } finally {
                    // fStream.close();
                    // }
                    try (OutputStream os = Files.newOutputStream(targetLocation)) {
                        os.write(file.getBytes());
                    }
                    System.out.println("fileName "+fileName);

                    mediaMaster.setMediaType(contentType);
                    mediaMaster.setMediaUrl(fileName);
                    mediaMaster.setActivityCode(activityCode);
                    mediaMaster = mediaMasterRepository.save(mediaMaster);
                    System.out.println("mediaMaster"+mediaMaster.getMediaId());

                    break;
                case 2:
                    break;
                default:
                    mediaMaster = cloudStorageService.storeFile(file, folder, subFolderPath, 0);
                    break;
            }

            // Copy file to the target location (Replacing existing file with the same name)

            return mediaMaster;
        } catch (Exception ex) {
        	exceptionLogService.addException("Media Master service","storeFile" ,
					ex, null);
        	ex.printStackTrace();
            throw new MediaStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public MediaMasterV2 storeFilev2(MultipartFile file, String folder, String subFolderPath, Integer activityCode,Integer userID) {

        String fileName = "";
        try {
            // Check if the file's name contains invalid characters

            MediaMasterV2 mediaMaster = new MediaMasterV2();
            switch (datastoreType) {
                case 1:
                    this.fileStorageLocation = Paths.get(path + "/" + folder + "/" + userID.toString()).toAbsolutePath()
                            .normalize();
                    try {
                        Files.createDirectories(this.fileStorageLocation);
                        //System.out.println("fileStorageLocation "+fileStorageLocation);
                    } catch (Exception ex) {
                        throw new MediaStorageException(
                                "Could not create the directory where the uploaded files will be stored.",
                                ex);
                    }

                    fileName = file.getOriginalFilename();// reNameFileName(folder, StringUtils.cleanPath(file.getOriginalFilename()));
                    if (fileName.contains("..")) {
                        throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
                    }
                    Path targetLocation = this.fileStorageLocation.resolve(fileName);

                    String contentType = file.getContentType();
                    // InputStream fStream = file.getInputStream();
                    // try {
                    // Files.copy(fStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    // } catch (Exception e) {
                    // System.out.println(e.getMessage());
                    // } finally {
                    // fStream.close();
                    // }
                    try (OutputStream os = Files.newOutputStream(targetLocation)) {
                        os.write(file.getBytes());
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    String dateString = extractDateString(fileName, String.valueOf(userID));
                     long millis = convertDateToMillis(dateString);
                    mediaMaster.setMediaUrl(fileName);
                    mediaMaster.setActivityCode(activityCode);
                    mediaMaster.setInserted_at(new Timestamp(millis));
                    mediaMaster.setMediaType(contentType);
                    mediaMaster.setUserId(Long.valueOf(userID));

                    mediaMaster = mediaMasterRepositoryV2.save(mediaMaster);

                  //  MediaMasterUserMapping mediaMasterUserMapping = new MediaMasterUserMapping();
                    //mediaMasterUserMapping.setMediaID(mediaMaster.getMediaId());
                   // mediaMasterUserMapping.setUserId(Long.valueOf(userID));
                   // userMediaMappingRepository.save(mediaMasterUserMapping);

                    System.out.println("mediaMaster"+mediaMaster.getMediaId());

//                    fileStorageLocation = Paths.get("/data/kerala/crop_survey1/agri-stack-application/agristack/userWiseImage/" + userID +"/")
//                            .normalize();
//                                        fileName = file.getOriginalFilename();//reNameFileName(folder, StringUtils.cleanPath(file.getOriginalFilename()));
//
//
//                    try {
//                        Files.createDirectories(fileStorageLocation);
//                        System.out.println("fileStorageLocation: " + fileStorageLocation);
//                    } catch (IOException ex) {
//                        throw new MediaStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//                    }
//
//                    String contentType = file.getContentType();
//
//                    if (fileName.contains("..")) {
//                        throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//                    }
//
//                    Path targetLocation = fileStorageLocation.resolve(fileName);
//                    System.out.println("targetLocation "+targetLocation);
//
//                    try (InputStream fStream = file.getInputStream()) {
//                        Files.copy(fStream, targetLocation);
//                        System.out.println("File stored successfully at: " + targetLocation);
//                    } catch (IOException e) {
//                        throw new MediaStorageException("Failed to store file " + fileName, e);
//                    }
//                    this.fileStorageLocation = Paths.get(path + "/" + folder + "/" + subFolderPath).toAbsolutePath()
//                            .normalize();

                    // Define the storage path based on userID
//                    fileStorageLocation = Paths.get("/data/kerala/crop_survey1/agri-stack-application/agristack/userWiseImage/" + userID)
//                            .normalize(); // Normalize the path to avoid redundant elements
//
//                    try {
//                        // Create directories if they don't exist
//                        Files.createDirectories(fileStorageLocation);
//                        System.out.println("fileStorageLocation: " + fileStorageLocation);
//                    } catch (Exception ex) {
//                        throw new MediaStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//                    }
//
//                    fileName = file.getOriginalFilename();//reNameFileName(folder, StringUtils.cleanPath(file.getOriginalFilename()));
//                    String contentType = file.getContentType();
//
//                    if (fileName.contains("..")) {
//                        throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//                    }
//                    Path targetLocation = fileStorageLocation.resolve(fileName);
//
////                    // Use Files.copy to transfer the file content to the target location
////                    try (InputStream fStream = file.getInputStream()) {
////                        Files.copy(fStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
////                        System.out.println("File stored successfully at: " + targetLocation);
////                    } catch (IOException e) {
////                        throw new MediaStorageException("Failed to store file " + fileName, e);
////                    }
//                    try (OutputStream os = Files.newOutputStream(targetLocation)) {
//                        os.write(file.getBytes());
//                        System.out.println("fileName "+fileName);
//
//                    }






                   // mediaMaster.setMediaType(contentType);
//                    mediaMaster.setMediaUrl(fileName);
//                    mediaMaster.setActivityCode(activityCode);
//                   // mediaMaster.setInserted_at(new Timestamp(millis));
//                    mediaMaster = mediaMasterRepositoryV2.save(mediaMaster);
//
//                    MediaMasterUserMapping mediaMasterUserMapping = new MediaMasterUserMapping();
//                    mediaMasterUserMapping.setMediaID(mediaMaster.getMediaId());
//                    mediaMasterUserMapping.setUserId(Long.valueOf(userID));
//                    userMediaMappingRepository.save(mediaMasterUserMapping);
//
//                    System.out.println("mediaMaster"+mediaMaster.getMediaId());

                    break;
                case 2:
                    break;
                default:
                   // mediaMaster = cloudStorageService.storeFile(file, folder, subFolderPath, 0);
                    break;
            }

            // Copy file to the target location (Replacing existing file with the same name)

            return mediaMaster;
        } catch (Exception ex) {
            exceptionLogService.addException("Media Master service","storeFile" ,
                    ex, null);
            ex.printStackTrace();
            throw new MediaStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public static long convertDateToMillis(String dateString) {
        // Adjust the date string format (replace underscores with colons)
        dateString = dateString.replace('_', ':');

        // Define the date format matching the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss O yyyy", Locale.ENGLISH);

        // Parse the date string to ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);

        // Convert to milliseconds since epoch
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static String extractDateString(String filename, String userId) {
        // Find the index of the userId and extract the part after it
        int index = filename.indexOf(userId);

        if (index != -1) {
            // Extract the part after the userId
            String afterUserId = filename.substring(index + userId.length());

            // Optional: Remove the file extension if necessary
            int dotIndex = afterUserId.lastIndexOf('.');
            if (dotIndex != -1) {
                afterUserId = afterUserId.substring(0, dotIndex);
            }

            return afterUserId.trim(); // Clean up whitespace
        }

        return null; // Return null if userId is not found
    }
 
    /**
     * Loads the file as a resource.
     *
     * @param fileName   The name of the file
     * @param folderName The name of the main folder
     * @param type       The type of file (image, audio, video, document)
     * @return The resource representing the file
     */
    public Resource loadFileAsResource(String fileName, String folderName, String type) {
        try {
            Path filePath = Paths.get(path + "/" + folderName + "/" + type + "/" + fileName).toAbsolutePath()
                    .normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyMediaNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyMediaNotFoundException("File not found " + fileName, ex);
        }
    }

    /**
     * Loads the file as an InputStream.
     *
     * @param fileName   The name of the file
     * @param folderName The name of the main folder
     * @param type       The type of file (image, audio, video, document)
     * @return The InputStream representing the file
     * @throws FileNotFoundException If the file is not found
     */
    public InputStream loadFile(String fileName, String folderName, String type) throws FileNotFoundException {
        try {
            Path filePath = Paths.get(path + "/" + folderName + "/" + type + "/" + fileName).toAbsolutePath()
                    .normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {

                InputStream in = new FileInputStream(new File(filePath.toString()));
                return in;
            } else {
                throw new MyMediaNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyMediaNotFoundException("File not found " + fileName, ex);
        }
    }

    /**
     * Retrieves the media information by media ID.
     *
     * @param mediaId The ID of the media
     * @return The ResponseEntity containing the media information
     */
    public ResponseEntity<?> getMedia(String mediaId) {

        try {

            MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(mediaId);
            if (mediaMaster != null) {

                if (mediaMaster.getMediaType().startsWith("image/")) {
                    mediaMaster.setMediaUrl(
                            virtualUrl + "agristack" + "/" + "image/" + mediaMaster.getMediaUrl());
                } else if (mediaMaster.getMediaType().startsWith("audio/")) {
                    mediaMaster.setMediaUrl(
                            virtualUrl + "agristack" + "/" + "audio/" + mediaMaster.getMediaUrl());
                } else if (mediaMaster.getMediaType().startsWith("video/")) {
                    mediaMaster.setMediaUrl(
                            virtualUrl + "agristack" + "/" + "video/" + mediaMaster.getMediaUrl());
                } else if (mediaMaster.getMediaType().startsWith("application/pdf")) {
                    mediaMaster.setMediaUrl(
                            virtualUrl + "agristack" + "/" + "document/" + mediaMaster.getMediaUrl());
                } else {
                    mediaMaster.setMediaUrl(
                            virtualUrl + "agristack" + "/" + "document/" + mediaMaster.getMediaUrl());
                }
                return new ResponseEntity<MediaMaster>(mediaMaster, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("Not Found",
                        HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            throw new MyMediaNotFoundException("media not found.");
        }

    }

    /**
     * Retrieves the media information by media ID.
     *
     * @param mediaId The ID of the media
     * @return The MediaMaster object representing the media
     */
    public MediaMaster getMediaMaster(String mediaId) {
        try {
            MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(mediaId);
            if (mediaMaster != null) {
                return mediaMaster;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new MyMediaNotFoundException("media not found.");
        }
    }

    /**
     * Updates the file with the provided media ID.
     *
     * @param file    The new file to be uploaded
     * @param mediaId The ID of the media
     * @return The updated MediaMaster object
     */
    public MediaMaster updateFile(@Valid MultipartFile file, String mediaId) {

        String fileName = "";
        String mediaType = "";

        try {
            MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(mediaId);

            if (mediaMaster != null) {
                fileName = mediaMaster.getMediaUrl();
                if (file.getContentType().startsWith("image/")) {
                    mediaType = folderImage;
                } else if (file.getContentType().startsWith("audio/")) {
                    mediaType = folderAudio;
                } else if (file.getContentType().startsWith("video/")) {
                    mediaType = folderVideo;
                }
                File existingFile = new File(
                        path + "/" + mediaType + "/" + mediaMaster.getMediaUrl());
                String newFileName = reNameFileName("",
                        StringUtils.cleanPath(file.getOriginalFilename()));
                this.fileStorageLocation = Paths.get(path + "/" + mediaType)
                        .toAbsolutePath().normalize();
                if (existingFile.delete()) {
                    writeFile(file, newFileName);
                } else {
                    writeFile(file, newFileName);
                }

                mediaMaster.setMediaUrl(newFileName);
                MediaMaster updatedMediaMaster = mediaMasterRepository.save(mediaMaster);

                return updatedMediaMaster;
            } else {
                throw new MyMediaNotFoundException("File not found " + fileName);
            }

        } catch (Exception ex) {
            throw new MediaStorageException("Could not update file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Writes the provided MultipartFile to the target location.
     *
     * @param file     The MultipartFile to write.
     * @param fileName The name of the file.
     * @throws IOException If an I/O error occurs.
     */
    private void writeFile(@Valid MultipartFile file, String fileName) throws IOException {
        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
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
        String extension = Optional.of(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));

        }
        String timeStamp = String.valueOf(System.nanoTime());
        String newFileName = folder + "_" + sb.toString() + "_" + timeStamp + "." + extension;
        return newFileName;
    }

    /**
     * Deletes the media file associated with the given ID.
     *
     * @param id The ID of the media file to delete.
     * @return The response model indicating the success or failure of the deletion.
     */
    public ResponseModel deleteMedia(String id) {
        String fileName = "";
        String mediaType = "";
        try {

            MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(id);
            System.out.println(mediaMaster);
            if (mediaMaster != null) {

                fileName = mediaMaster.getMediaUrl();
                if (mediaMaster.getMediaType().startsWith("image/")) {
                    mediaType = folderImage;
                } else if (mediaMaster.getMediaType().startsWith("audio/")) {
                    mediaType = folderAudio;
                } else if (mediaMaster.getMediaType().startsWith("video/")) {
                    mediaType = folderVideo;
                } else if (mediaMaster.getMediaType().startsWith("application/pdf")) {
                    mediaType = folderDocument;
                }

                try {
                    Files.deleteIfExists(Paths.get(path + "/" + "agristack" + "/" + mediaType + "/" + fileName));
                    mediaMasterRepository.delete(mediaMaster);

                } catch (NoSuchFileException e) {
                    System.out.println("No such file/directory exists");
                } catch (DirectoryNotEmptyException e) {
                    System.out.println("Directory is not empty.");
                } catch (IOException e) {
                    System.out.println("Invalid permissions.");
                }
                return new ResponseModel(id, "Media deleted successfully", CustomMessages.GET_DATA_SUCCESS,
                        CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

            } else {
                return new ResponseModel(id, "Media deleted successfully", CustomMessages.NO_DATA_FOUND,
                        CustomMessages.FAILED, CustomMessages.METHOD_POST);
            }

        } catch (Exception ex) {
            throw new MediaStorageException("Could not Delete file " + fileName + ".Please try again!", ex);
        }
    }

    /**
     * Retrieves the details of the media file with the given ID.
     *
     * @param mediaId The ID of the media file.
     * @return The MediaMaster object containing the media details, or null if not
     *         found.
     */
    public MediaMaster getMediaDetail(String mediaId) {
        MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(mediaId);
        if (mediaMaster != null) {

            if (mediaMaster.getMediaType().startsWith("image/")) {
                mediaMaster.setMediaUrl(
                        virtualUrl + "agristack" + "/" + "image/" + mediaMaster.getMediaUrl());
            } else if (mediaMaster.getMediaType().startsWith("audio/")) {
                mediaMaster.setMediaUrl(
                        virtualUrl + "agristack" + "/" + "audio/" + mediaMaster.getMediaUrl());
            } else if (mediaMaster.getMediaType().startsWith("video/")) {
                mediaMaster.setMediaUrl(
                        virtualUrl + "agristack" + "/" + "video/" + mediaMaster.getMediaUrl());
            } else if (mediaMaster.getMediaType().startsWith("application/pdf")) {
                mediaMaster.setMediaUrl(
                        virtualUrl + "agristack" + "/" + "document/" + mediaMaster.getMediaUrl());
            } else {
                mediaMaster.setMediaUrl(
                        virtualUrl + "agristack" + "/" + "document/" + mediaMaster.getMediaUrl());
            }
            return mediaMaster;
        }
        return null;
    }

    public MediaMaster storeFileWithSameName(MultipartFile file, String folder, String subFolderPath,
            Integer activityCode) {
        this.fileStorageLocation = Paths.get(path + "/" + folder + "/" + subFolderPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new MediaStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }

        String fileName = file.getOriginalFilename();

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new MediaStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            MediaMaster mediaMaster = new MediaMaster();
            mediaMaster.setMediaType(file.getContentType());
            mediaMaster.setMediaUrl(fileName);
            mediaMaster.setActivityCode(activityCode);
            MediaMaster updatedMedia = mediaMasterRepository.save(mediaMaster);

            return updatedMedia;
        } catch (IOException ex) {
            throw new MediaStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}