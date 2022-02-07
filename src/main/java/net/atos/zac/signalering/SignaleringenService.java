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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.event.EventingService;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.websocket.event.ScreenEventType;

@ApplicationScoped
@Transactional
public class SignaleringenService {


    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private EventingService eventingService;

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

    public boolean isSubcribedTo(final Signalering signalering) {
        // TODO Here will be business logic to check if a valid subscription exists for a given signalering.
        return true;
    }

    public Signalering createSignalering(final Signalering signalering) {
        valideerObject(signalering);
        final Signalering created = entityManager.merge(signalering);
        eventingService.send(ScreenEventType.SIGNALERINGEN.updated(created));
        return created;
    }

    public void deleteSignalering(final Signalering signalering) {
        eventingService.send(ScreenEventType.SIGNALERINGEN.updated(signalering));
        entityManager.remove(signalering);
    }

    public void deleteSignalering(final SignaleringZoekParameters parameters) {
        final List<Signalering> signaleringen = findSignaleringen(parameters);
        signaleringen.forEach(this::deleteSignalering);
    }

    public Signalering readSignalering(final long id) {
        return entityManager.find(Signalering.class, id);
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

    private Predicate[] getPredicates(final SignaleringZoekParameters parameters, final CriteriaBuilder builder,
            final Root<Signalering> root) {
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

    public ZonedDateTime latestSignalering(final SignaleringZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZonedDateTime> query = builder.createQuery(ZonedDateTime.class);
        final Root<Signalering> root = query.from(Signalering.class);


        query.select(root.get("tijdstip"))
                .where(builder.and(getPredicates(parameters, builder, root)))
                .orderBy(builder.desc(root.get("tijdstip")));

        final List<ZonedDateTime> resultList = entityManager.createQuery(query).getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
