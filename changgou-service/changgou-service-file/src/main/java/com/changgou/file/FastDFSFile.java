package com.changgou.file;

/**
 * @ClassName FastDFSFile
 * @Description 封装附件信息对象
 * @Author 传智播客
 * @Date 10:44 2020/9/5
 * @Version 2.1
 **/
public class FastDFSFile {

    private String name;        // 附件名称
    private byte[] content;     // 附件内容
    private String ext;         // 附件扩展名
    private String md5;         // 附件摘要（备注）
    private String author;      // 附件作者

    public FastDFSFile(String name, byte[] content, String ext, String md5, String author) {
        this.name = name;
        this.content = content;
        this.ext = ext;
        this.md5 = md5;
        this.author = author;
    }

    public FastDFSFile(String name, byte[] content, String ext) {
        this.name = name;
        this.content = content;
        this.ext = ext;
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

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
