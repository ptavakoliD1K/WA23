package com.welfenhub.models;

import javax.persistence.*;

@Entity
@Table(name = "evaluation_files")
public class EvaluationFiles {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mime_type")
    private String mime_type;

    @Column(name = "content", columnDefinition = "BLOB")
    private byte[] content;

    @Column(name = "creation_date")
    private String date;

    public EvaluationFiles(Long id, String mime_type, byte[] content, String date) {
        this.id = id;
        this.mime_type = mime_type;
        this.content = content;
        this.date = date;
    }

    public EvaluationFiles() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
