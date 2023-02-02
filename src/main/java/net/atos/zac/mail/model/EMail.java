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
    private MailAdres verzender;

    @JsonbProperty("To")
    private List<MailAdres> ontvangers;

    @JsonbProperty("ReplyTo")
    private MailAdres replyTo;

    @JsonbProperty("Subject")
    private String onderwerp;

    @JsonbProperty("Attachments")
    private List<Attachment> attachments;

    public EMail(final MailAdres verzender, final List<MailAdres> ontvangers, final MailAdres replyTo,
            final String onderwerp, final String body, final List<Attachment> attachments) {
        this.verzender = verzender;
        this.ontvangers = ontvangers;
        this.replyTo = replyTo;
        this.onderwerp = onderwerp;
        this.body = "<pre>" + body + "</pre>";
        this.attachments = attachments;
    }

    public MailAdres getVerzender() {
        return verzender;
    }

    public void setVerzender(final MailAdres verzender) {
        this.verzender = verzender;
    }

    public List<MailAdres> getOntvangers() {
        return ontvangers;
    }

    public void setOntvangers(final List<MailAdres> ontvangers) {
        this.ontvangers = ontvangers;
    }

    public MailAdres getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(final MailAdres replyTo) {
        this.replyTo = replyTo;
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
