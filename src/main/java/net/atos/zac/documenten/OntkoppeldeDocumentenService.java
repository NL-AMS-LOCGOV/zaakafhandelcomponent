/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documenten;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.DateTimeUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.documenten.model.OntkoppeldDocument;
import net.atos.zac.documenten.model.OntkoppeldDocumentListParameters;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.util.UriUtil;


@ApplicationScoped
@Transactional
public class OntkoppeldeDocumentenService {

    private static final String LIKE = "%%%s%%";

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public OntkoppeldDocument create(final EnkelvoudigInformatieobject informatieobject, final Zaak zaak, final String reden) {
        final OntkoppeldDocument ontkoppeldDocument = new OntkoppeldDocument();
        ontkoppeldDocument.setDocumentID(informatieobject.getIdentificatie());
        ontkoppeldDocument.setDocumentUUID(UriUtil.uuidFromURI(informatieobject.getUrl()));
        ontkoppeldDocument.setCreatiedatum(informatieobject.getCreatiedatum().atStartOfDay(ZoneId.systemDefault()));
        ontkoppeldDocument.setTitel(informatieobject.getTitel());
        ontkoppeldDocument.setBestandsnaam(informatieobject.getBestandsnaam());
        ontkoppeldDocument.setOntkoppeldOp(ZonedDateTime.now());
        ontkoppeldDocument.setOntkoppeldDoor(loggedInUserInstance.get().getId());
        ontkoppeldDocument.setZaakID(zaak.getIdentificatie());
        ontkoppeldDocument.setReden(reden);
        valideerObject(ontkoppeldDocument);
        entityManager.persist(ontkoppeldDocument);
        return ontkoppeldDocument;
    }

    public List<OntkoppeldDocument> list(final OntkoppeldDocumentListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<OntkoppeldDocument> query = builder.createQuery(OntkoppeldDocument.class);
        final Root<OntkoppeldDocument> root = query.from(OntkoppeldDocument.class);
        if (listParameters.getSorting() != null) {
            if (listParameters.getSorting().getDirection() == SorteerRichting.ASCENDING) {
                query.orderBy(builder.asc(root.get(listParameters.getSorting().getField())));
            } else {
                query.orderBy(builder.desc(root.get(listParameters.getSorting().getField())));
            }
        }
        query.where(getWhere(listParameters, root));
        final TypedQuery<OntkoppeldDocument> emQuery = entityManager.createQuery(query);
        if (listParameters.getPaging() != null) {
            emQuery.setFirstResult(listParameters.getPaging().getFirstResult());
            emQuery.setMaxResults(listParameters.getPaging().getMaxResults());
        }
        return emQuery.getResultList();
    }

    public OntkoppeldDocument read(final UUID enkelvoudiginformatieobjectUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<OntkoppeldDocument> query = builder.createQuery(OntkoppeldDocument.class);
        final Root<OntkoppeldDocument> root = query.from(OntkoppeldDocument.class);
        query.select(root).where(builder.equal(root.get("documentUUID"), enkelvoudiginformatieobjectUUID));
        return entityManager.createQuery(query).getSingleResult();
    }

    public OntkoppeldDocument find(final long id) {
        return entityManager.find(OntkoppeldDocument.class, id);
    }

    public int count(final OntkoppeldDocumentListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<OntkoppeldDocument> root = query.from(OntkoppeldDocument.class);
        query.select(builder.count(root));
        query.where(getWhere(listParameters, root));
        final Long result = entityManager.createQuery(query).getSingleResult();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    public void delete(final Long id) {
        final OntkoppeldDocument ontkoppeldDocument = find(id);
        if (ontkoppeldDocument != null) {
            entityManager.remove(ontkoppeldDocument);
        }
    }

    private Predicate getWhere(final OntkoppeldDocumentListParameters listParameters, final Root<OntkoppeldDocument> root) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(listParameters.getZaakID())) {
            predicates.add(builder.like(root.get(OntkoppeldDocument.ZAAK_ID), LIKE.formatted(listParameters.getZaakID())));
        }
        if (StringUtils.isNotBlank(listParameters.getTitel())) {
            String titel = LIKE.formatted(listParameters.getTitel().toLowerCase().replace(" ", "%"));
            predicates.add(builder.like(builder.lower(root.get(OntkoppeldDocument.TITEL)), titel));
        }
        if (StringUtils.isNotBlank(listParameters.getReden())) {
            String reden = LIKE.formatted(listParameters.getReden().toLowerCase().replace(" ", "%"));
            predicates.add(builder.like(builder.lower(root.get(OntkoppeldDocument.REDEN)), reden));
        }
        if (listParameters.getCreatiedatum() != null) {
            if (listParameters.getCreatiedatum().van() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get(OntkoppeldDocument.CREATIEDATUM),
                                                            DateTimeUtil.convertToDateTime(listParameters.getCreatiedatum().van())));
            }
            if (listParameters.getCreatiedatum().tot() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get(OntkoppeldDocument.CREATIEDATUM),
                                                         DateTimeUtil.convertToDateTime(listParameters.getCreatiedatum().tot())));
            }
        }
        if (listParameters.getOntkoppeldOp() != null) {
            if (listParameters.getOntkoppeldOp().van() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get(OntkoppeldDocument.ONTKOPPELD_OP),
                                                            DateTimeUtil.convertToDateTime(listParameters.getOntkoppeldOp().van())));
            }
            if (listParameters.getOntkoppeldOp().tot() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get(OntkoppeldDocument.ONTKOPPELD_OP),
                                                         DateTimeUtil.convertToDateTime(listParameters.getOntkoppeldOp().tot())));
            }
        }
        if (StringUtils.isNotBlank(listParameters.getOntkoppeldDoor())) {
            predicates.add(builder.equal(root.get(OntkoppeldDocument.ONTKOPPELD_DOOR), listParameters.getOntkoppeldDoor()));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
