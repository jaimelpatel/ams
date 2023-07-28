/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.CloudMediaCategory;
import com.ltlogic.constants.CloudMediaType;
import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author Bishistha
 */
@NamedQueries({
    @NamedQuery(
            name = "CloudMedia.getCloudMediasByUserPkTypeAndCategory",
            query = "SELECT c FROM CloudMedia c WHERE c.type = :type AND c.category = :category"
    )
})
@Entity
public class CloudMedia extends SuperEntity {

    /*blobId = {bucket, fileName, generation}*/
    private String fileName;
    private String bucket;
    private Long generation;

    private String filePath;
    private String contentType;

    private String mediaLink;
    private String selfLink;
    private String displayUrl;
    private String generationId;

    @Enumerated(EnumType.STRING)
    private CloudMediaType type;

    @Enumerated(EnumType.STRING)
    private CloudMediaCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_pk", referencedColumnName = "pk")
    private Dispute dispute;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public CloudMediaType getType() {
        return type;
    }

    public void setType(CloudMediaType type) {
        this.type = type;
    }

    public CloudMediaCategory getCategory() {
        return category;
    }

    public void setCategory(CloudMediaCategory category) {
        this.category = category;
    }

    public Dispute getDispute() {
        return dispute;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public Long getGeneration() {
        return generation;
    }

    public void setGeneration(Long generation) {
        this.generation = generation;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getGenerationId() {
        return generationId;
    }

    public void setGenerationId(String generationId) {
        this.generationId = generationId;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
      
}
