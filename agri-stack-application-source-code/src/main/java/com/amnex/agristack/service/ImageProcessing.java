package com.amnex.agristack.service;

import com.amnex.agristack.dao.MediaDAO;
import com.amnex.agristack.repository.MediaMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Media;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Service
public class ImageProcessing {

    @Autowired
    MediaMasterRepository mediaMasterRepository;




    private static void readTextFromImageAndSaveToFile(BufferedImage image, String sourceUrl, FileWriter fileWriter) {
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        instance.setLanguage("eng");

        try {
            String result = instance.doOCR(image);
            fileWriter.write("Text from: " + sourceUrl + "\n" + result + "\n\n");
        } catch (TesseractException | IOException e) {
            System.err.println("Error while reading image from " + sourceUrl + ": " + e.getMessage());
        }
    }

    private static BufferedImage downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void processImagesFromUrlsAndSaveResults(List<MediaDAO> imageUrls, String outputFilePath) {
        try (FileWriter fileWriter = new FileWriter(outputFilePath, true)) { // Set true for append mode
            for (MediaDAO imageUrl : imageUrls) {
                String url = "https://updcs.agristack.gov.in/agristack_ag_up/crop-survey-api-beta/agristack/v1/api/media/downloadFile/agristack/image/" +  imageUrl.getMedia_url();
                System.out.println("Processing: " + imageUrl.getMedia_url());
                BufferedImage image = downloadImage(url);
                if (image != null) {
                    readTextFromImageAndSaveToFile(image, imageUrl.getMedia_id(), fileWriter);
                } else {
                    System.out.println("Failed to download image: " + imageUrl);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }

    public  void getImagesList() {
        List<Object[]> imageUrls = mediaMasterRepository.fetchImageUrls();
        List<MediaDAO> mediaList = new ArrayList<>();
        for (Object[] obj: imageUrls) {
            MediaDAO media= new MediaDAO(obj[0].toString(),obj[1].toString());
            mediaList.add(media);
        }
        // Implement this method to fetch your image URLs
        String outputFilePath = "C:\\Users\\krupali.jogi\\Desktop\\Latitude\\output\\output.txt"; // Specify your output file path here
        processImagesFromUrlsAndSaveResults(mediaList, outputFilePath);
    }

}
