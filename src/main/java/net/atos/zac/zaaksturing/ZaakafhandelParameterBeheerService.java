/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.util.ValidationUtil.valideerObject;
import static net.atos.zac.zaaksturing.model.ZaakafhandelParameters.ZAAKTYPE_UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.util.ValidationUtil;
import net.atos.zac.zaaksturing.model.PlanItemParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;
import net.atos.zac.zaaksturing.model.ZaakbeeindigReden;

@ApplicationScoped
@Transactional
public class ZaakafhandelParameterBeheerService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public ZaakafhandelParameters createZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        valideerObject(zaakafhandelParameters);
        zaakafhandelParameters.getPlanItemParametersCollection().forEach(ValidationUtil::valideerObject);
        entityManager.persist(zaakafhandelParameters);
        return zaakafhandelParameters;
    }

    public ZaakafhandelParameters readZaakafhandelParameters(final UUID zaakTypeUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakafhandelParameters> query = builder.createQuery(ZaakafhandelParameters.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.select(root).where(builder.equal(root.get(ZAAKTYPE_UUID), zaakTypeUUID));
        final List<ZaakafhandelParameters> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        final ZaakafhandelParameters zaakafhandelParameters = new ZaakafhandelParameters();
        zaakafhandelParameters.setZaakTypeUUID(zaakTypeUUID);
        return zaakafhandelParameters;
    }

    public ZaakafhandelParameters updateZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        valideerObject(zaakafhandelParameters);
        zaakafhandelParameters.getPlanItemParametersCollection().forEach(ValidationUtil::valideerObject);
        return entityManager.merge(zaakafhandelParameters);
    }

    public PlanItemParameters readPlanItemParameters(final UUID zaakTypeUUID, final String planitemDefinitionID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<PlanItemParameters> query = builder.createQuery(PlanItemParameters.class);
        final Root<PlanItemParameters> queryRoot = query.from(PlanItemParameters.class);

        final Join<PlanItemParameters, ZaakafhandelParameters> zapJoin = queryRoot.join("zaakafhandelParameters", JoinType.INNER);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(zapJoin.get(ZAAKTYPE_UUID), zaakTypeUUID));
        predicates.add(builder.equal(queryRoot.get("planItemDefinitionID"), planitemDefinitionID));
        query.where(predicates.toArray(new Predicate[0]));
        final List<PlanItemParameters> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    public List<ZaakbeeindigReden> listZaakbeeindigRedenen() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakbeeindigReden> query = builder.createQuery(ZaakbeeindigReden.class);
        final Root<ZaakbeeindigReden> root = query.from(ZaakbeeindigReden.class);
        query.orderBy(builder.asc(root.get("naam")));
        final TypedQuery<ZaakbeeindigReden> emQuery = entityManager.createQuery(query);
        return emQuery.getResultList();
    }

    public ZaakbeeindigParameter readZaakbeeindigParameter(final UUID zaaktypeUUID, final Long zaakbeeindigRedenId) {
        return readZaakafhandelParameters(zaaktypeUUID).getZaakbeeindigParameters().stream()
                .filter(zaakbeeindigParameter -> zaakbeeindigParameter.getZaakbeeindigReden().getId().equals(zaakbeeindigRedenId))
                .findAny().orElseThrow();
    }
}
