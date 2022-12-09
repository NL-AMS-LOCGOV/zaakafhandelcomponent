/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import net.atos.zac.app.admin.model.RESTMailtemplate;
import net.atos.zac.mailtemplates.MailTemplateHelper;
import net.atos.zac.mailtemplates.model.Mail;
import net.atos.zac.mailtemplates.model.MailTemplate;

public class RESTMailtemplateConverter {

    public RESTMailtemplate convert(final MailTemplate mailTemplate) {
        final RESTMailtemplate restMailtemplate = new RESTMailtemplate();
        restMailtemplate.id = mailTemplate.getId();
        restMailtemplate.mailTemplateNaam = mailTemplate.getMailTemplateNaam();
        restMailtemplate.mail = mailTemplate.getMail().name();
        restMailtemplate.variabelen = mailTemplate.getMail().getVariabelen();
        restMailtemplate.onderwerp = mailTemplate.getOnderwerp();
        restMailtemplate.body = mailTemplate.getBody();
        restMailtemplate.defaultMailtemplate = mailTemplate.isDefaultMailtemplate();

        return restMailtemplate;
    }

    public MailTemplate convert(final RESTMailtemplate restMailtemplate) {
        final MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setId(restMailtemplate.id);
        mailTemplate.setMail(Mail.valueOf(restMailtemplate.mail));
        mailTemplate.setMailTemplateNaam(restMailtemplate.mailTemplateNaam);
        mailTemplate.setOnderwerp(MailTemplateHelper.stripParagraphTags(restMailtemplate.onderwerp));
        mailTemplate.setBody(restMailtemplate.body);
        mailTemplate.setDefaultMailtemplate(restMailtemplate.defaultMailtemplate);
        return mailTemplate;
    }
}
