package net.atos.zac.documenten;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.documenten.model.OntkoppeldDocument;
import net.atos.zac.shared.model.ListParameters;
import net.atos.zac.shared.model.SortDirection;

@ApplicationScoped
@Transactional
public class OntkoppeldeDocumentenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public OntkoppeldDocument create(final OntkoppeldDocument document) {
        valideerObject(document);
        entityManager.persist(document);
        return document;
    }

    public List<OntkoppeldDocument> list(final ListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<OntkoppeldDocument> query = builder.createQuery(OntkoppeldDocument.class);
        final Root<OntkoppeldDocument> root = query.from(OntkoppeldDocument.class);
        if (listParameters.getSorting() != null) {
            if (listParameters.getSorting().getDirection() == SortDirection.ASCENDING) {
                query.orderBy(builder.asc(root.get(listParameters.getSorting().getField())));
            } else {
                query.orderBy(builder.asc(root.get(listParameters.getSorting().getField())));
            }
        }
        final TypedQuery<OntkoppeldDocument> emQuery = entityManager.createQuery(query);
        if (listParameters.getPaging() != null) {
            emQuery.setFirstResult(listParameters.getPaging().getFirstResult());
            emQuery.setMaxResults(listParameters.getPaging().getMaxResults());
        }
        return emQuery.getResultList();
    }

    public OntkoppeldDocument update(final OntkoppeldDocument ontkoppeldDocument) {
        valideerObject(ontkoppeldDocument);
        return entityManager.merge(ontkoppeldDocument);
    }

    public void delete(final Long id) {
        final OntkoppeldDocument ontkoppeldDocument = entityManager.find(OntkoppeldDocument.class, id);
        if (ontkoppeldDocument != null) {
            entityManager.remove(ontkoppeldDocument);
        }
    }
}
