/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import net.atos.zac.zaaksturing.model.MailtemplateKoppeling;

@ApplicationScoped
@Transactional
public class MailTemplateKoppelingenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public MailtemplateKoppeling find(final long id) {
        return entityManager.find(MailtemplateKoppeling.class, id);
    }

    public void delete(final Long id) {
        final MailtemplateKoppeling mailtemplateKoppeling = find(id);
        if (mailtemplateKoppeling != null) {
            entityManager.remove(mailtemplateKoppeling);
        }
    }

    public MailtemplateKoppeling storeMailtemplate(final MailtemplateKoppeling mailtemplateKoppeling) {
        valideerObject(mailtemplateKoppeling);
        final MailtemplateKoppeling existing = find(mailtemplateKoppeling.getId());
        if (existing != null) {
            return entityManager.merge(mailtemplateKoppeling);
        } else {
            entityManager.persist(mailtemplateKoppeling);
            return mailtemplateKoppeling;
        }
    }

    public MailtemplateKoppeling readMailtemplate(final long id) {
        final MailtemplateKoppeling mailtemplateKoppeling = entityManager.find(MailtemplateKoppeling.class, id);
        if (mailtemplateKoppeling != null) {
            return mailtemplateKoppeling;
        } else {
            throw new RuntimeException(String.format("%s with id=%d not found",
                    MailtemplateKoppeling.class.getSimpleName(), id));
        }
    }
}
