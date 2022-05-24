/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documenten;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.documenten.model.InboxDocument;
import net.atos.zac.shared.model.ListParameters;
import net.atos.zac.shared.model.SorteerRichting;

@ApplicationScoped
@Transactional
public class InboxDocumentenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    public InboxDocument create(final UUID enkelvoudiginformatieobejctUUID) {
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(enkelvoudiginformatieobejctUUID);
        final InboxDocument inboxDocument = new InboxDocument();
        inboxDocument.setEnkelvoudiginformatieobjectID(informatieobject.getIdentificatie());
        inboxDocument.setEnkelvoudiginformatieobjectUUID(enkelvoudiginformatieobejctUUID);
        inboxDocument.setCreatiedatum(informatieobject.getCreatiedatum().atStartOfDay(ZoneId.systemDefault()));
        inboxDocument.setTitel(informatieobject.getTitel());
        inboxDocument.setBestandsnaam(informatieobject.getBestandsnaam());
        entityManager.persist(inboxDocument);
        return inboxDocument;
    }

    public List<InboxDocument> list(final ListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<InboxDocument> query = builder.createQuery(InboxDocument.class);
        final Root<InboxDocument> root = query.from(InboxDocument.class);
        if (listParameters.getSorting() != null) {
            if (listParameters.getSorting().getDirection() == SorteerRichting.ASCENDING) {
                query.orderBy(builder.asc(root.get(listParameters.getSorting().getField())));
            } else {
                query.orderBy(builder.desc(root.get(listParameters.getSorting().getField())));
            }
        }
        final TypedQuery<InboxDocument> emQuery = entityManager.createQuery(query);
        if (listParameters.getPaging() != null) {
            emQuery.setFirstResult(listParameters.getPaging().getFirstResult());
            emQuery.setMaxResults(listParameters.getPaging().getMaxResults());
        }
        return emQuery.getResultList();
    }

    public InboxDocument find(final long id) {
        return entityManager.find(InboxDocument.class, id);
    }

    public InboxDocument find(final UUID enkelvoudiginformatieobjectUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<InboxDocument> query = builder.createQuery(InboxDocument.class);
        final Root<InboxDocument> root = query.from(InboxDocument.class);
        query.select(root).where(builder.equal(root.get("enkelvoudiginformatieobjectUUID"), enkelvoudiginformatieobjectUUID));
        final List<InboxDocument> resultList = entityManager.createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public int count() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<InboxDocument> root = query.from(InboxDocument.class);
        query.select(builder.count(root));
        final Long result = entityManager.createQuery(query).getSingleResult();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    public void delete(final Long id) {
        final InboxDocument inboxDocument = find(id);
        if (inboxDocument != null) {
            entityManager.remove(inboxDocument);
        }
    }

    public void delete(final UUID zaakinformatieobjectUUID) {
        final ZaakInformatieobject zaakInformatieobject = zrcClientService.readZaakinformatieobject(zaakinformatieobjectUUID);
        final UUID enkelvoudiginformatieobjectUUID = URIUtil.parseUUIDFromResourceURI(zaakInformatieobject.getInformatieobject());
        final InboxDocument inboxDocument = find(enkelvoudiginformatieobjectUUID);
        if (inboxDocument != null) {
            entityManager.remove(inboxDocument);
        }
    }
}
