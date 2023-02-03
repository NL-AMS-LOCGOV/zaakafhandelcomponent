/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.MailTemplateHelper;

public class MailGegevens {
    private final MailAdres verzender;

    private final MailAdres ontvanger;

    private final MailAdres replyTo;

    private final String onderwerp;

    private final String body;

    private final String[] bijlagen;

    private final boolean createDocumentFromMail;

    public MailGegevens(final MailAdres verzender, final MailAdres ontvanger, final MailAdres replyTo,
            final String onderwerp, final String body, final String bijlagen, final boolean createDocumentFromMail) {
        this.verzender = verzender;
        this.ontvanger = ontvanger;
        this.replyTo = replyTo;
        this.onderwerp = MailTemplateHelper.stripParagraphTags(onderwerp);
        this.body = body;
        this.bijlagen = bijlagen != null ? bijlagen.split(";") : new String[0];
        this.createDocumentFromMail = createDocumentFromMail;
    }

    public MailGegevens(final MailAdres verzender, final MailAdres ontvanger, final String onderwerp,
            final String body) {
        this(verzender, ontvanger, null, onderwerp, body, null, false);
    }

    public MailAdres getVerzender() {
        return verzender;
    }

    public MailAdres getOntvanger() {
        return ontvanger;
    }

    public MailAdres getReplyTo() {
        return replyTo;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public String getBody() {
        return body;
    }

    public String[] getBijlagen() {
        return bijlagen;
    }

    public boolean isCreateDocumentFromMail() {
        return createDocumentFromMail;
    }
}
