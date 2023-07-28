/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Bishistha
 */
@Component
public class ImageValidator {

    public void validateContentTypeAndDimensionForImage(MultipartFile multipartFile, Errors errors) {
        if (multipartFile.isEmpty()) {
            errors.reject("fileContent", "Empty file. Please select a file and click upload.");
        } else {

            String contentType = multipartFile.getContentType();
            if (contentType == null) {
                errors.reject("contentType", "Content Type for the file is corrupt. Please pick a different file.");
            }
            boolean isAllowedContentType = "image/jpeg".equalsIgnoreCase(contentType) || "image/png".equalsIgnoreCase(contentType);
            if (!isAllowedContentType) {
                errors.reject("contentType", "Only JPEG or PNG images are allowed.");
            } else {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(multipartFile.getInputStream());
                    Integer width = image.getWidth();
                    boolean isCorrectWidth = width.compareTo(801) == -1;
                    Integer height = image.getHeight();
                    boolean isCorrectHeight = height.compareTo(801) == -1;

                    if (!(isCorrectWidth && isCorrectHeight)) {
                        errors.reject("contentType", "Image dimensions should be 800 by 800 pixels or less but found: " + width + " by " + height + " pixels.");
                    }
                } catch (IOException ex) {
                    System.out.println("############### ERROR");
                    errors.reject("contentType", "Invalid file. Please pick an image.");
                }
            }
        }
    }

    public void validateContentTypeAndDimensionForImage2(MultipartFile multipartFile) throws Exception {
        if (multipartFile.isEmpty()) {
            throw new Exception("Empty file. Please select a file and click upload.");
        } else {

            String contentType = multipartFile.getContentType();
            if (contentType == null) {
                throw new Exception("Content Type for the file is corrupt. Please pick a different file.");
            }
            boolean isAllowedContentType = "image/jpeg".equalsIgnoreCase(contentType) || "image/png".equalsIgnoreCase(contentType);
            if (!isAllowedContentType) {
                throw new Exception("Only JPEG or PNG images are allowed.");
            } else {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(multipartFile.getInputStream());
                    Integer width = image.getWidth();
                    boolean isCorrectWidth = width.compareTo(1501) == -1;
                    Integer height = image.getHeight();
                    boolean isCorrectHeight = height.compareTo(1501) == -1;

                    if (!(isCorrectWidth && isCorrectHeight)) {
                        throw new Exception("Image dimensions should be 1500 by 1500 pixels or less but found: " + width + " by " + height + " pixels.");
                    }
                } catch (IOException ex) {
                    throw new Exception("Invalid file. Please pick an image.");
                }
            }
        }
    }
}
