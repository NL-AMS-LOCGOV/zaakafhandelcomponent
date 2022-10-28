/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates;

import net.atos.zac.mailtemplates.model.MailTemplate;
import net.atos.zac.mailtemplates.model.MailTemplateEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class MailTemplateService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public MailTemplate create(final String mailTemplateNaam, final String onderwerp, final String body,
            final MailTemplateEnum mailTemplateEnum, final Long parent) {
        final MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setOnderwerp(onderwerp);
        mailTemplate.setBody(body);
        mailTemplate.setMailTemplateNaam(mailTemplateNaam);
        mailTemplate.setMailTemplateEnum(mailTemplateEnum);
        mailTemplate.setParent(parent);
        entityManager.persist(mailTemplate);
        return mailTemplate;
    }

    public MailTemplate find(final long id) {
        return entityManager.find(MailTemplate.class, id);
    }

    public void delete(final Long id) {
        final MailTemplate mailTemplate = find(id);
        if (mailTemplate != null) {
            entityManager.remove(mailTemplate);
        }
    }
}
