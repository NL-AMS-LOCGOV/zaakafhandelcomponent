/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
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
        return entityManager.createQuery(
                        query.select(root)
                                .where(builder.and(getPredicates(parameters, builder, root)))
                                .orderBy(builder.desc(root.get("tijdstip"))))
                .getResultList();
    }

    private Predicate[] getPredicates(final SignaleringZoekParameters parameters, final CriteriaBuilder builder, final Root<Signalering> root) {
        final List<Predicate> where = new ArrayList<>();
        if (parameters.getType() != null) {
            where.add(builder.equal(root.get("type").get("id"), parameters.getType().toString()));
        }
        if (parameters.getSubjecttype() != null) {
            where.add(builder.equal(root.get("type").get("subjecttype"), parameters.getSubjecttype()));
        }
        if (parameters.getSubject() != null) {
            where.add(builder.equal(root.get("subject"), parameters.getSubject()));
        }
        if (parameters.getTargettype() != null) {
            where.add(builder.equal(root.get("targettype"), parameters.getTargettype()));
        }
        if (parameters.getTarget() != null) {
            where.add(builder.equal(root.get("target"), parameters.getTarget()));
        }
        return where.toArray(new Predicate[0]);
    }

    public int countSignaleringen(final SignaleringZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<Signalering> root = query.from(Signalering.class);

        query.select(builder.count(root)).where(getPredicates(parameters, builder, root));

        return entityManager.createQuery(query).getSingleResult().intValue();
    }
}
