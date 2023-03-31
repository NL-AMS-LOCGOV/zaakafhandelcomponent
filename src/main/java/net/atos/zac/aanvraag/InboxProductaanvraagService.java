/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import net.atos.zac.aanvraag.model.InboxProductaanvraag;

@ApplicationScoped
@Transactional
public class InboxProductaanvraagService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public InboxProductaanvraag create(final InboxProductaanvraag inboxProductaanvraag) {
        entityManager.persist(inboxProductaanvraag);
        return inboxProductaanvraag;
    }
}
