package com.welfenhub.models;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "content", columnDefinition = "BYTEA")
    private byte[] content;

    @Column(name = "semester")
    private String semester;

    @Column(name = "module")
    private String module;

    @Column(name = "fachrichtung")
    private String fachrichtung;

    @Column(name = "tag")
    private String tag;

    public Files() {}

    public Files(Long id, String name, byte[] content, String semester, String module, String fachrichtung, String tag) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.semester = semester;
        this.module = module;
        this.fachrichtung = fachrichtung;
        this.tag = tag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFachrichtung() {
        return fachrichtung;
    }

    public void setFachrichtung(String fachrichtung) {
        this.fachrichtung = fachrichtung;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
