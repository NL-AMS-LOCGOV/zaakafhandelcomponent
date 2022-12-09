/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.mailtemplates.MailTemplateHelper;

public class MailGegevens {
    private final Ontvanger ontvanger;

    private final String onderwerp;

    private final String body;

    private final boolean createDocumentFromMail;

    private final String[] bijlagen;

    public MailGegevens(final Ontvanger ontvanger, final String onderwerp, final String body, final String bijlagen,
            final boolean createDocumentFromMail) {
        this.ontvanger = ontvanger;
        this.onderwerp = MailTemplateHelper.stripParagraphTags(onderwerp);
        this.body = body;
        this.bijlagen = bijlagen != null ? bijlagen.split(";") : new String[0];
        this.createDocumentFromMail = createDocumentFromMail;
    }

    public MailGegevens(final Ontvanger ontvanger, final String onderwerp, final String body) {
        this(ontvanger, onderwerp, body, null, false);
    }

    public Ontvanger getOntvanger() {
        return ontvanger;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public String getBody() {
        return body;
    }

    public boolean isCreateDocumentFromMail() {
        return createDocumentFromMail;
    }

    public String[] getBijlagen() {
        return bijlagen;
    }
}
