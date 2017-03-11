package com.lcz.file.entity;

import java.io.File;
import java.io.Serializable;

/**
 * Created by liuchuanzhu on 2017/3/7.
 */
public class GenerateXmlParams implements Serializable {

    private File excelFile;

    private File xmlTemplateFile;

    private String xmlStorePath;

    private String batchNo;

    private String archiveNOPre;
    private String archiveNo;
    private String archiveNoPost;

    private String fileNamePre;
    private String fileName;


    public File getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
    }

    public File getXmlTemplateFile() {
        return xmlTemplateFile;
    }

    public void setXmlTemplateFile(File xmlTemplateFile) {
        this.xmlTemplateFile = xmlTemplateFile;
    }

    public String getXmlStorePath() {
        return xmlStorePath;
    }

    public void setXmlStorePath(String xmlStorePath) {
        this.xmlStorePath = xmlStorePath;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getArchiveNOPre() {
        return archiveNOPre;
    }

    public void setArchiveNOPre(String archiveNOPre) {
        this.archiveNOPre = archiveNOPre;
    }

    public String getArchiveNo() {
        return archiveNo;
    }

    public void setArchiveNo(String archiveNo) {
        this.archiveNo = archiveNo;
    }

    public String getArchiveNoPost() {
        return archiveNoPost;
    }

    public void setArchiveNoPost(String archiveNoPost) {
        this.archiveNoPost = archiveNoPost;
    }

    public String getFileNamePre() {
        return fileNamePre;
    }

    public void setFileNamePre(String fileNamePre) {
        this.fileNamePre = fileNamePre;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
