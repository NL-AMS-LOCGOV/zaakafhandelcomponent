/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import net.atos.zac.app.admin.model.RESTMailtemplate;
import net.atos.zac.mailtemplates.model.MailTemplate;
import net.atos.zac.mailtemplates.model.Mail;

public class RESTMailtemplateConverter {

    public RESTMailtemplate convert(final MailTemplate mailTemplate) {
        final RESTMailtemplate restMailtemplate = new RESTMailtemplate();
        restMailtemplate.id = mailTemplate.getId();
        restMailtemplate.mailTemplateNaam = mailTemplate.getMailTemplateNaam();
        restMailtemplate.mail = mailTemplate.getMail().name();
        restMailtemplate.variabelen = mailTemplate.getMail().getVariabelen().stream().map(Object::toString).toList();
        restMailtemplate.onderwerp = mailTemplate.getOnderwerp();
        restMailtemplate.body = mailTemplate.getBody();
        restMailtemplate.parent = mailTemplate.getParent();

        return restMailtemplate;
    }

    public MailTemplate convert(final RESTMailtemplate restMailtemplate) {
        final MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setId(restMailtemplate.id);
        mailTemplate.setMail(Mail.valueOf(restMailtemplate.mail));
        mailTemplate.setMailTemplateNaam(restMailtemplate.mailTemplateNaam);
        mailTemplate.setOnderwerp(restMailtemplate.onderwerp);
        mailTemplate.setBody(restMailtemplate.body);
        mailTemplate.setParent(restMailtemplate.parent);
        return mailTemplate;
    }
}
