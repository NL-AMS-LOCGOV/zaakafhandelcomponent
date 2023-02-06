/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.MailTemplateHelper;

public class MailGegevens {
    private final MailAdres from;

    private final MailAdres to;

    private final MailAdres replyTo;

    private final String subject;

    private final String body;

    private final String[] attachments;

    private final boolean createDocumentFromMail;

    public MailGegevens(
            final MailAdres from, final MailAdres to, final MailAdres replyTo,
            final String subject, final String body, final String attachments,
            final boolean createDocumentFromMail) {
        this.from = from;
        this.to = to;
        this.replyTo = replyTo;
        this.subject = MailTemplateHelper.stripParagraphTags(subject);
        this.body = body;
        this.attachments = attachments != null ? attachments.split(";") : new String[0];
        this.createDocumentFromMail = createDocumentFromMail;
    }

    public MailGegevens(
            final MailAdres from, final MailAdres to,
            final String subject, final String body) {
        this(from, to, null, subject, body, null, false);
    }

    public MailAdres getFrom() {
        return from;
    }

    public MailAdres getTo() {
        return to;
    }

    public MailAdres getReplyTo() {
        return replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String[] getAttachments() {
        return attachments;
    }

    public boolean isCreateDocumentFromMail() {
        return createDocumentFromMail;
    }
}
