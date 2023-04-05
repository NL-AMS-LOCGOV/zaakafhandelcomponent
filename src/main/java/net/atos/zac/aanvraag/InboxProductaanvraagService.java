/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.shared.util.DateTimeUtil;
import net.atos.zac.aanvraag.model.InboxProductaanvraag;
import net.atos.zac.aanvraag.model.InboxProductaanvraagListParameters;
import net.atos.zac.aanvraag.model.InboxProductaanvraagResultaat;
import net.atos.zac.shared.model.SorteerRichting;

@ApplicationScoped
@Transactional
public class InboxProductaanvraagService {

    private static final String LIKE = "%%%s%%";

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public InboxProductaanvraag create(final InboxProductaanvraag inboxProductaanvraag) {
        entityManager.persist(inboxProductaanvraag);
        return inboxProductaanvraag;
    }

    public InboxProductaanvraagResultaat list(final InboxProductaanvraagListParameters listParameters) {
        return new InboxProductaanvraagResultaat(query(listParameters), count(listParameters), listTypes(listParameters));
    }

    private List<String> listTypes(final InboxProductaanvraagListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> query = builder.createQuery(String.class);
        final Root<InboxProductaanvraag> root = query.from(InboxProductaanvraag.class);
        query.select(root.get(InboxProductaanvraag.TYPE)).distinct(true);
        query.where(getWhere(listParameters, root));
        return entityManager.createQuery(query).getResultList();
    }

    private List<InboxProductaanvraag> query(final InboxProductaanvraagListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<InboxProductaanvraag> query = builder.createQuery(InboxProductaanvraag.class);
        final Root<InboxProductaanvraag> root = query.from(InboxProductaanvraag.class);
        if (listParameters.getSorting() != null) {
            if (listParameters.getSorting().getDirection() == SorteerRichting.ASCENDING) {
                query.orderBy(builder.asc(root.get(listParameters.getSorting().getField())));
            } else {
                query.orderBy(builder.desc(root.get(listParameters.getSorting().getField())));
            }
        }
        query.where(getWhere(listParameters, root));
        final TypedQuery<InboxProductaanvraag> emQuery = entityManager.createQuery(query);
        if (listParameters.getPaging() != null) {
            emQuery.setFirstResult(listParameters.getPaging().getFirstResult());
            emQuery.setMaxResults(listParameters.getPaging().getMaxResults());
        }
        return emQuery.getResultList();
    }

    private int count(final InboxProductaanvraagListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<InboxProductaanvraag> root = query.from(InboxProductaanvraag.class);
        query.select(builder.count(root));
        query.where(getWhere(listParameters, root));
        final Long result = entityManager.createQuery(query).getSingleResult();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    private Predicate getWhere(final InboxProductaanvraagListParameters listParameters, final Root<InboxProductaanvraag> root) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(listParameters.getInitiatorID())) {
            predicates.add(
                    builder.like(root.get(InboxProductaanvraag.INITIATOR), LIKE.formatted(listParameters.getInitiatorID())));
        }

        if (StringUtils.isNotBlank(listParameters.getType())) {
            predicates.add(builder.equal(root.get(InboxProductaanvraag.TYPE), listParameters.getType()));
        }

        if (listParameters.getOntvangstdatum() != null) {
            if (listParameters.getOntvangstdatum().van() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get(InboxProductaanvraag.ONTVANGSTDATUM),
                                                            DateTimeUtil.convertToDateTime(listParameters.getOntvangstdatum().van())));
            }
            if (listParameters.getOntvangstdatum().tot() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get(InboxProductaanvraag.ONTVANGSTDATUM),
                                                         DateTimeUtil.convertToDateTime(listParameters.getOntvangstdatum().tot()).plusDays(1)
                                                                 .minusSeconds(1)));
            }
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }

}
