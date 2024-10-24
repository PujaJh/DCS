package com.amnex.agristack.dao;


import org.springframework.http.HttpStatus;

public class UploadMediaResponse implements MediaResponse {
    private String mediaId;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private HttpStatus status;

    public UploadMediaResponse(String mediaId, String fileName, String fileDownloadUri, String fileType, long size,
            HttpStatus status) {
        this.mediaId = mediaId;
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
        this.status = status;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}