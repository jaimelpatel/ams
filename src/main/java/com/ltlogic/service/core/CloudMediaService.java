/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.StorageOptions.Builder;
import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.CloudMediaCategory;
import com.ltlogic.constants.CloudMediaType;
import com.ltlogic.db.entity.CloudMedia;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.CloudMediaRepository;
import com.ltlogic.db.repository.DisputeRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Bishistha
 */
@Service
@Transactional
public class CloudMediaService implements InitializingBean {

    @Value("${envTarget:local}")
    private String targetEnv;

    private static final Logger log = LoggerFactory.getLogger(CloudMediaService.class);

    private static String DISPLAY_URL_PREFIX = "https://storage.googleapis.com/";
    private static String USER_PROFILE_PIC_BUCKET = "";
    private static String TEAM_PROFILE_PIC_BUCKET = "";
    private static String DISPUTE_PIC_BUCKET = "";

    private static Storage STORAGE;

    private static boolean isProduction;

    @Override
    public void afterPropertiesSet() throws Exception {
        isProduction = isProduction(targetEnv);
        USER_PROFILE_PIC_BUCKET = isProduction ? "xms-user-profile-pic-prod" : "xms-user-profile-pic-test";
        TEAM_PROFILE_PIC_BUCKET = isProduction ? "xms-team-profile-pic-prod" : "xms-team-profile-pic-test";
        DISPUTE_PIC_BUCKET = isProduction ? "xms-dispute-pic-prod" : "xms-dispute-pic-test";
        //credential = GoogleCredential.getApplicationDefault().;
        STORAGE = isProduction ? StorageOptions.newBuilder().setProjectId("sunny-buttress-176720").build().getService() : StorageOptions.newBuilder().setProjectId("sunny-buttress-176720").build().getService();
        //STORAGE = isProduction ? StorageOptions.getDefaultInstance().getService() : null;
    }

    private boolean isProduction(String env) {
        return "production".equalsIgnoreCase(env);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private DisputeRepository disputeRepository;

    @Autowired
    private CloudMediaRepository cloudMediaRepository;

    private BlobInfo saveImageToGoogleStorage(String bucketName, String fileName, String contentType, byte[] fileContent) {
        return STORAGE.create(BlobInfo.newBuilder(bucketName, fileName).setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Role.READER)))).setContentType(contentType).build(), fileContent);
    }

    public boolean deleteImageFromGoogleStorage(BlobId blobId) {
        return STORAGE.delete(blobId);
    }

    private BlobId blobIdBuilder(String bucketName, String fileName, Long generation) {
        return BlobInfo.newBuilder(bucketName, fileName, generation).build().getBlobId();
    }

    public String updateUserProfilePicture(long userPk, String contentType, byte[] fileContent) throws Exception {
        User user = userRepository.findByPk(userPk);
        CloudMedia cloudMedia;
        boolean profilePicAlreadyExist = user.getCloudMediaPkForProfilePic() != 0;
        if (profilePicAlreadyExist) {
            boolean isDeleteSuccessful = false;
            cloudMedia = cloudMediaRepository.findCloudMediaByPk(user.getCloudMediaPkForProfilePic());
            BlobId blobId = blobIdBuilder(cloudMedia.getBucket(), cloudMedia.getFileName(), cloudMedia.getGeneration());
            isDeleteSuccessful = deleteImageFromGoogleStorage(blobId);
            if (!isDeleteSuccessful) {
                throw new Exception("# Error while trying to delete image from storage. ");
            }
        } else {
            cloudMedia = new CloudMedia();
        }

        String fileName = user.getUsername() + getLocalDateTimeNow();
        BlobInfo blobInfo = saveImageToGoogleStorage(USER_PROFILE_PIC_BUCKET, fileName, contentType, fileContent);
        cloudMedia = createCloudMediaForUserProfilePicture(cloudMedia, blobInfo);
        user.setDisplayUrl(cloudMedia.getDisplayUrl());
        user.setCloudMediaPkForProfilePic(cloudMedia.getPk());
        if(!profilePicAlreadyExist){
            cloudMediaRepository.persistCloudMedia(cloudMedia);
        }
        return cloudMedia.getDisplayUrl();
    }

    public CloudMedia createCloudMediaForUserProfilePicture(CloudMedia cloudMedia, BlobInfo blobInfo) {
        cloudMedia.setCategory(CloudMediaCategory.USER_IMG);
        cloudMedia.setType(CloudMediaType.IMAGE);
        cloudMedia.setBucket(USER_PROFILE_PIC_BUCKET);
        cloudMedia.setGeneration(blobInfo.getGeneration());
        cloudMedia.setGenerationId(blobInfo.getGeneratedId());
        cloudMedia.setMediaLink(blobInfo.getMediaLink());
        cloudMedia.setSelfLink(blobInfo.getSelfLink());
        cloudMedia.setFileName(blobInfo.getName());
        cloudMedia.setFilePath(USER_PROFILE_PIC_BUCKET + "/" + blobInfo.getName());
        cloudMedia.setDisplayUrl(DISPLAY_URL_PREFIX + USER_PROFILE_PIC_BUCKET + "/" + blobInfo.getName());
        cloudMedia.setContentType(blobInfo.getContentType());
        return cloudMedia;
    }

    public String updateTeamProfilePicture(long teamPk, String contentType, byte[] fileContent) throws Exception {
        Team team = teamRepository.findTeamByPk(teamPk);
        CloudMedia  cloudMedia;
        boolean profilePicAlreadyExist = team.getCloudMediaPkForProfilePic() != 0;
        if (profilePicAlreadyExist) {
            boolean isDeleteSuccessful = false;
            cloudMedia = cloudMediaRepository.findCloudMediaByPk(team.getCloudMediaPkForProfilePic());
            BlobId blobId = blobIdBuilder(cloudMedia.getBucket(), cloudMedia.getFileName(), cloudMedia.getGeneration());
            isDeleteSuccessful = deleteImageFromGoogleStorage(blobId);
            if (!isDeleteSuccessful) {
                throw new Exception("# Error while trying to delete image from storage. ");
            }
        } else{
            cloudMedia = new CloudMedia();
        }

        String fileName = team.getTeamPojo().getTeamName() + "_" + team.getTeamId() + "_" + getLocalDateTimeNow();
        BlobInfo blobInfo = saveImageToGoogleStorage(TEAM_PROFILE_PIC_BUCKET, fileName, contentType, fileContent);
        cloudMedia = createCloudMediaForTeamProfilePicture(cloudMedia, blobInfo);
        team.setDisplayUrl(cloudMedia.getDisplayUrl());
        team.setCloudMediaPkForProfilePic(cloudMedia.getPk());
        if(!profilePicAlreadyExist){
            cloudMediaRepository.persistCloudMedia(cloudMedia);
        }
        return cloudMedia.getDisplayUrl();
    }

    public CloudMedia createCloudMediaForTeamProfilePicture(CloudMedia cloudMedia, BlobInfo blobInfo) {
        cloudMedia.setCategory(CloudMediaCategory.TEAM_IMG);
        cloudMedia.setType(CloudMediaType.IMAGE);
        cloudMedia.setBucket(TEAM_PROFILE_PIC_BUCKET);
        cloudMedia.setGeneration(blobInfo.getGeneration());
        cloudMedia.setGenerationId(blobInfo.getGeneratedId());
        cloudMedia.setMediaLink(blobInfo.getMediaLink());
        cloudMedia.setSelfLink(blobInfo.getSelfLink());
        cloudMedia.setFileName(blobInfo.getName());
        cloudMedia.setFilePath(TEAM_PROFILE_PIC_BUCKET + "/" + blobInfo.getName());
        cloudMedia.setDisplayUrl(DISPLAY_URL_PREFIX + TEAM_PROFILE_PIC_BUCKET + "/" + blobInfo.getName());
        cloudMedia.setContentType(blobInfo.getContentType());
        return cloudMedia;
    }

    public String uploadDisputePicture(long userPk, long disputePk, String contentType, byte[] fileContent) {
        Dispute dispute = disputeRepository.findDisputeByPk(disputePk);
        User user = userRepository.findByPk(userPk);
        String fileName = user.getUsername() + "-" + dispute.getTeam().getTeamId() + "-" + dispute.getMatch().getMatchId() + getLocalDateTimeNow();
        BlobInfo blobInfo = saveImageToGoogleStorage(DISPUTE_PIC_BUCKET, fileName, contentType, fileContent);
        CloudMedia cloudMedia = new CloudMedia();
        cloudMedia.setCategory(CloudMediaCategory.DISPUTE_IMG);
        cloudMedia.setType(CloudMediaType.IMAGE);
        cloudMedia.setBucket(DISPUTE_PIC_BUCKET);
        cloudMedia.setGeneration(blobInfo.getGeneration());
        cloudMedia.setGenerationId(blobInfo.getGeneratedId());
        cloudMedia.setMediaLink(blobInfo.getMediaLink());
        cloudMedia.setSelfLink(blobInfo.getSelfLink());
        cloudMedia.setFileName(blobInfo.getName());
        cloudMedia.setFilePath(DISPUTE_PIC_BUCKET + "/" + blobInfo.getName());
        cloudMedia.setDisplayUrl(DISPLAY_URL_PREFIX + DISPUTE_PIC_BUCKET + "/" + blobInfo.getName());
        associateDisputeToCloudMedia(dispute, cloudMedia);
        cloudMediaRepository.persistCloudMedia(cloudMedia);
        return cloudMedia.getDisplayUrl();
    }
    
    private void associateDisputeToCloudMedia(Dispute dispute, CloudMedia cloudMedia) {
        dispute.getCloudMedia().add(cloudMedia);
        cloudMedia.setDispute(dispute);
    }

    public List<CloudMedia> findCloudMediaForUserPk(long userPk, CloudMediaType type, CloudMediaCategory category) throws Exception {
        return cloudMediaRepository.getCloudMediasByUserPkTypeAndCategory(userPk, type, category);
    }

    public String findProfilePicDisplayUrlForUserPk(long userPk) throws Exception {
        List<CloudMedia> cloudMediaList = findCloudMediaForUserPk(userPk, CloudMediaType.IMAGE, CloudMediaCategory.USER_IMG);
        CloudMedia cloudMedia = cloudMediaList.get(0);
        return cloudMedia.getDisplayUrl();
    }

    private String getLocalDateTimeNow() {
       return DateTimeUtil.formatLocalDateTime(DateTimeUtil.getDefaultLocalDateTimeNow(), "-YYYY-MM-dd-HH-mm-ss");
    }

}
