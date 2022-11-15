/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates;

import net.atos.zac.mailtemplates.model.Mail;
import net.atos.zac.mailtemplates.model.MailTemplate;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import java.util.List;

import static net.atos.zac.util.ValidationUtil.valideerObject;

@ApplicationScoped
@Transactional
public class MailTemplateService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public MailTemplate find(final long id) {
        return entityManager.find(MailTemplate.class, id);
    }

    public void delete(final Long id) {
        final MailTemplate mailTemplate = find(id);
        if (mailTemplate != null) {
            entityManager.remove(mailTemplate);
        }
    }

    public MailTemplate persistMailtemplate(final MailTemplate mailTemplate) {
        valideerObject(mailTemplate);
        final MailTemplate existing = find(mailTemplate.getId());
        if (existing != null) {
            return entityManager.merge(mailTemplate);
        } else {
            entityManager.persist(mailTemplate);
            return mailTemplate;
        }
    }

    public MailTemplate findMailtemplate(final Mail mail) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MailTemplate> query = builder.createQuery(MailTemplate.class);
        final Root<MailTemplate> root = query.from(MailTemplate.class);
        query.select(root).where(builder.equal(root.get(MailTemplate.MAIL), mail));
        final List<MailTemplate> resultList = entityManager.createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public MailTemplate readMailtemplate(final long id) {
        final MailTemplate mailTemplate = entityManager.find(MailTemplate.class, id);
        if (mailTemplate != null) {
            return mailTemplate;
        } else {
            throw new RuntimeException(String.format("%s with id=%d not found", MailTemplate.class.getSimpleName(), id));
        }
    }

    public List<MailTemplate> listMailtemplates() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<net.atos.zac.mailtemplates.model.MailTemplate> query = builder.createQuery(
                net.atos.zac.mailtemplates.model.MailTemplate.class);
        final Root<net.atos.zac.mailtemplates.model.MailTemplate> root = query.from(
                net.atos.zac.mailtemplates.model.MailTemplate.class);
        query.orderBy(builder.asc(root.get("mailTemplateNaam")));
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

}
