/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.time.ZonedDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;

@ApplicationScoped
@Transactional
public class SignaleringService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    /**
     * Factory method for constructing Signalering instances.
     *
     * @param type the type op the signalering to construct
     * @return the constructed instance (subject and target are still null, type and tijdstip have been set)
     */
    public Signalering signaleringInstance(final SignaleringType.Type type) {
        final Signalering instance = new Signalering();
        instance.setType(entityManager.find(SignaleringType.class, type.toString()));
        instance.setTijdstip(ZonedDateTime.now());
        return instance;
    }

    public Signalering createSignalering(final Signalering signalering) {
        valideerObject(signalering);
        return entityManager.merge(signalering);
    }

    public Signalering readSignalering(final long id) {
        return entityManager.find(Signalering.class, id);
    }

    public void deleteSignalering(final Signalering signalering) {
        entityManager.remove(signalering);
    }

    public List<Signalering> findSignaleringen(final SignaleringZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Signalering> query = builder.createQuery(Signalering.class);
        final Root<Signalering> root = query.from(Signalering.class);
        query.select(root);
        // TODO #236
        //        .where(builder.equal(root.get(ZAAKTYPE_UUID), zaakTypeUUID));
        TypedQuery<Signalering> emQuery = entityManager.createQuery(query.orderBy(builder.desc(root.get("tijdstip"))));
        return emQuery.getResultList();
    }
}
