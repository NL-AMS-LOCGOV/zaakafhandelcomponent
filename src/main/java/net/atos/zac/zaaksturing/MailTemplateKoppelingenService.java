/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.util.Optional;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.zaaksturing.model.MailtemplateKoppeling;

@ApplicationScoped
@Transactional
public class MailTemplateKoppelingenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public Optional<MailtemplateKoppeling> find(final long id) {
        final var mailtemplateKoppeling = entityManager.find(MailtemplateKoppeling.class, id);
        return mailtemplateKoppeling != null ? Optional.of(mailtemplateKoppeling) : Optional.empty();
    }

    public void delete(final Long id) {
        find(id).ifPresent(entityManager::remove);
    }

    public MailtemplateKoppeling storeMailtemplateKoppeling(final MailtemplateKoppeling mailtemplateKoppeling) {
        valideerObject(mailtemplateKoppeling);
        if (mailtemplateKoppeling.getId() != null && find(mailtemplateKoppeling.getId()).isPresent()) {
            return entityManager.merge(mailtemplateKoppeling);
        } else {
            entityManager.persist(mailtemplateKoppeling);
            return mailtemplateKoppeling;
        }
    }

    public MailtemplateKoppeling readMailtemplateKoppeling(final long id) {
        final MailtemplateKoppeling mailtemplateKoppeling = entityManager.find(MailtemplateKoppeling.class, id);
        if (mailtemplateKoppeling != null) {
            return mailtemplateKoppeling;
        } else {
            throw new RuntimeException(String.format("%s with id=%d not found",
                    MailtemplateKoppeling.class.getSimpleName(), id));
        }
    }

    public List<MailtemplateKoppeling> listMailtemplateKoppelingen() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MailtemplateKoppeling> query = builder.createQuery(MailtemplateKoppeling.class);
        final Root<MailtemplateKoppeling> root = query.from(MailtemplateKoppeling.class);
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }
}
