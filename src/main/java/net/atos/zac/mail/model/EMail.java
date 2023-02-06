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
    private MailAdres from;

    @JsonbProperty("To")
    private List<MailAdres> to;

    @JsonbProperty("ReplyTo")
    private MailAdres replyTo;

    @JsonbProperty("Subject")
    private String subject;

    @JsonbProperty("Attachments")
    private List<Attachment> attachments;

    public EMail(final MailAdres from, final List<MailAdres> to, final MailAdres replyTo,
            final String subject, final String body, final List<Attachment> attachments) {
        this.from = from;
        this.to = to;
        this.replyTo = replyTo;
        this.subject = subject;
        this.body = "<pre>" + body + "</pre>";
        this.attachments = attachments;
    }

    public MailAdres getFrom() {
        return from;
    }

    public void setFrom(final MailAdres from) {
        this.from = from;
    }

    public List<MailAdres> getTo() {
        return to;
    }

    public void setTo(final List<MailAdres> to) {
        this.to = to;
    }

    public MailAdres getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(final MailAdres replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
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
