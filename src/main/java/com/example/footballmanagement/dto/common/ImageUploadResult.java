package com.example.footballmanagement.dto.common;

public class ImageUploadResult {

    private String url;
    private String publicId;
    private String originalFileName;

    public ImageUploadResult() {
    }

    public ImageUploadResult(String url, String publicId, String originalFileName) {
        this.url = url;
        this.publicId = publicId;
        this.originalFileName = originalFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}