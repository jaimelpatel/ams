/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.utility;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.ltlogic.DateTimeUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Bishistha
 */
public class CloudStorageHelper {

    private static Storage storage = null;

    public static void main(String[] args) throws MalformedURLException, IOException {
//        String fileName = "digital_image_processing";
//        String website = "http://tutorialspoint.com/java_dip/images/digital_image_processing.jpg";
//        String bucketName = "xms-user-profile-pic-test";
//        System.out.println("Downloading File From: " + website);

        //using input stream
//        URL url = new URL(website);
//        InputStream inputStream = url.openStream();
        //String mediaLink = uploadFileUsingInputStream(fileName, inputStream, bucketName);
//        System.out.println("media link: " + mediaLink);
        //using byte array
//        byte[] bytes = ByteStreams.toByteArray(inputStream);
//        String ab = uploadFileUsingByteArray(fileName, bytes, bucketName);
//        System.out.println("ab: " + ab);
        //testing Delete
//        {bucket=xms-user-profile-pic-test, name=digital_image_processing-2017-07-14-062112946, generation=1500013274074420}
//        BlobId blobId = BlobInfo.newBuilder("xms-user-profile-pic-test", "digital_image_processing-2017-07-14-062112946", 1500013274074420L).build().getBlobId();
//        deleteFile(blobId);

//        String fn = "imagetest_" + 1;
//        String wb = "http://tutorialspoint.com/java_dip/images/digital_image_processing.jpg";
//        String bn = "xms-user-profile-pic-test";
//        for (int i = 0; i <= 20; i++) {
//            URL url = new URL(wb);
//            InputStream inputStream = url.openStream();
//            byte[] bytes = ByteStreams.toByteArray(inputStream);
//            String ab = uploadFileUsingByteArray(fn, bytes, bn);
//            System.out.println("MEDIA_LINKS");
//            System.out.println(ab);
//        }
//       String fn = "testfile";
//       fn = fn + DateTimeUtil.getDefaultLocalDateTimeNow().toString(DateTimeFormat.forPattern("-YYYYMMddHHmmss"));
//        System.out.println(fn);
    }

    public static void getImage(String bucketName, String fileName, Long generation) throws IOException {
        // "xms-user-profile-pic-test", "digital_image_processing-2017-07-09-225402906", 1499640844251665L // JUST USE BLOBID INSTEAD
        BlobId id = BlobInfo.newBuilder(bucketName, fileName, generation).build().getBlobId();

        Blob blob = storage.get(id);
        byte[] file = blob.getContent();
        File outputFile = new File("/Users/Bishistha/Desktop/gce/img1");

        try (FileOutputStream outputStream = new FileOutputStream(outputFile);) {

            outputStream.write(file);  //write the bytes and your done. 

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BlobInfo uploadFileUsingByteArray(String fileName, byte[] fileContent, final String bucketName) throws IOException {
        fileName = fileName + getLocalDateTimeNow();

        BlobInfo blobInfo = storage.create(BlobInfo.newBuilder(bucketName, fileName).setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER)))).build(), fileContent);

//        System.out.println("## self link: " + blobInfo.getSelfLink());
//        System.out.println("## blobId: " + blobInfo.getBlobId());
//        System.out.println("## generation" + blobInfo.getGeneration());
//        System.out.println("## generation" + blobInfo.getGeneratedId());
//                System.out.println("## NAMe: " + blobInfo.getName());
//                        System.out.println("## owner: " + blobInfo.getOwner().toString());


        return blobInfo;
    }

    public static String uploadFileUsingInputStream(String fileName, InputStream inputStream, final String bucketName) throws IOException {
        fileName = fileName + getLocalDateTimeNow();

        // the inputstream is closed by default, so we don't need to close it here
        BlobInfo blobInfo
                = storage.create(
                        BlobInfo
                        .newBuilder(bucketName, fileName)
                        // Modify access list to allow all users with link to read file
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
                        .build(),
                        inputStream);
        System.out.println("## Blob stuff: " + blobInfo.getSelfLink());
        System.out.println(blobInfo.getBlobId());
        System.out.println(blobInfo.getGeneration());
        System.out.println(blobInfo.getGeneratedId());
        return blobInfo.getMediaLink();
    }

    static {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    public static boolean deleteFile(BlobId blobId) throws IOException {
        boolean isDeleted = storage.delete(blobId);
        System.out.println("########## Deleted? " + isDeleted);
        return isDeleted;
    }

    public String uploadImageToCloudStorage(HttpServletRequest req, HttpServletResponse resp,
            final String bucket) throws IOException, ServletException {
        Part filePart = req.getPart("file");
        final String fileName = filePart.getSubmittedFileName();
        String imageUrl = req.getParameter("imageUrl");
        if (fileName != null && !fileName.isEmpty() && fileName.contains(".")) {
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String[] allowedExt = {"jpg", "jpeg", "png", "gif"};
            for (String s : allowedExt) {
                if (extension.equals(s)) {
                    return this.uploadFile(filePart, bucket);
                }
            }
            throw new ServletException("file must be an image");
        }
        return imageUrl;
    }

    public String uploadFile(Part filePart, final String bucketName) throws IOException {
        final String fileName = filePart.getSubmittedFileName() + getLocalDateTimeNow();

        // the inputstream is closed by default, so we don't need to close it here
        BlobInfo blobInfo
                = storage.create(
                        BlobInfo
                        .newBuilder(bucketName, fileName)
                        // Modify access list to allow all users with link to read file
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
                        .build(),
                        filePart.getInputStream());
        // return the public download link
        return blobInfo.getMediaLink();
    }
    
    private static String getLocalDateTimeNow() {
        return DateTimeUtil.formatLocalDateTime(DateTimeUtil.getDefaultLocalDateTimeNow(), "-YYYY-MM-dd-HH-mm-ss");
    }
    
}
