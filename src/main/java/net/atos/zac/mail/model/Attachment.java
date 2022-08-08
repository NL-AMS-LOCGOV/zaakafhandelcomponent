package net.atos.zac.mail.model;

import javax.json.bind.annotation.JsonbProperty;

public class Attachment {

    @JsonbProperty("ContentType")
    private String contentType;

    @JsonbProperty("Filename")
    private String filename;

    @JsonbProperty("Base64Content")
    private String base64Content;

    public Attachment(final String contentType, final String filename, final String base64Content) {
        this.contentType = contentType;
        this.filename = filename;
        this.base64Content = base64Content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(final String base64Content) {
        this.base64Content = base64Content;
    }
}
