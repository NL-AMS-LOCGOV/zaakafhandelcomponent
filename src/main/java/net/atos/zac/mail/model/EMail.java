/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

public class EMail {

    @JsonbProperty("HTMLPart")
    private String body;

    @JsonbProperty("From")
    private Verzender verzender;

    @JsonbProperty("To")
    private List<Ontvanger> ontvangers;

    @JsonbProperty("Subject")
    private String onderwerp;

    @JsonbProperty("Attachments")
    private List<Attachment> attachments;

    public EMail(final Verzender verzender, final List<Ontvanger> ontvangers, final String onderwerp, final String body,
            final List<Attachment> attachments) {
        this.verzender = verzender;
        this.ontvangers = ontvangers;
        this.onderwerp = onderwerp;
        this.body = "<pre>" + body + "</pre>";
        this.attachments = attachments;
    }

    public Verzender getVerzender() {
        return verzender;
    }

    public void setVerzender(final Verzender verzender) {
        this.verzender = verzender;
    }

    public List<Ontvanger> getOntvangers() {
        return ontvangers;
    }

    public void setOntvangers(final List<Ontvanger> ontvangers) {
        this.ontvangers = ontvangers;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public void setOnderwerp(final String onderwerp) {
        this.onderwerp = onderwerp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
