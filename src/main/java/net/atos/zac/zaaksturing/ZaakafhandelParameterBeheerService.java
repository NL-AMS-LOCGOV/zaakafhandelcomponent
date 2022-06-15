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
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.UserEventListenerParameters;
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
        zaakafhandelParameters.getHumanTaskParameters().forEach(ValidationUtil::valideerObject);
        zaakafhandelParameters.getUserEventListenerParameters().forEach(ValidationUtil::valideerObject);
        entityManager.persist(zaakafhandelParameters);
        return zaakafhandelParameters;
    }

    public ZaakafhandelParameters readZaakafhandelParameters(final UUID zaaktypeUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakafhandelParameters> query = builder.createQuery(ZaakafhandelParameters.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.select(root).where(builder.equal(root.get(ZAAKTYPE_UUID), zaaktypeUUID));
        final List<ZaakafhandelParameters> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            final ZaakafhandelParameters zaakafhandelParameters = new ZaakafhandelParameters();
            zaakafhandelParameters.setZaakTypeUUID(zaaktypeUUID);
            return zaakafhandelParameters;
        }
    }

    public ZaakafhandelParameters updateZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        valideerObject(zaakafhandelParameters);
        zaakafhandelParameters.getHumanTaskParameters().forEach(ValidationUtil::valideerObject);
        return entityManager.merge(zaakafhandelParameters);
    }

    public List<ZaakafhandelParameters> listZaakafhandelParameters() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakafhandelParameters> query = builder.createQuery(ZaakafhandelParameters.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

    public HumanTaskParameters readHumanTaskParameters(final UUID zaaktypeUUID, final String planitemDefinitionID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<HumanTaskParameters> query = builder.createQuery(HumanTaskParameters.class);
        final Root<HumanTaskParameters> queryRoot = query.from(HumanTaskParameters.class);

        final Join<HumanTaskParameters, ZaakafhandelParameters> zapJoin = queryRoot.join("zaakafhandelParameters", JoinType.INNER);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(zapJoin.get(ZAAKTYPE_UUID), zaaktypeUUID));
        predicates.add(builder.equal(queryRoot.get("planItemDefinitionID"), planitemDefinitionID));
        query.where(predicates.toArray(new Predicate[0]));
        final List<HumanTaskParameters> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            throw new RuntimeException(
                    String.format("No HumanTaskParameters found for zaaktypeUUID: '%s' and planitemDefinitionID: '%s'", zaaktypeUUID.toString(),
                                  planitemDefinitionID));
        }
    }

    public UserEventListenerParameters readUserEventListenerParameters(final UUID zaaktypeUUID, final String planitemDefinitionID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UserEventListenerParameters> query = builder.createQuery(UserEventListenerParameters.class);
        final Root<UserEventListenerParameters> queryRoot = query.from(UserEventListenerParameters.class);

        final Join<UserEventListenerParameters, ZaakafhandelParameters> zapJoin = queryRoot.join("zaakafhandelParameters", JoinType.INNER);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(zapJoin.get(ZAAKTYPE_UUID), zaaktypeUUID));
        predicates.add(builder.equal(queryRoot.get("planItemDefinitionID"), planitemDefinitionID));
        query.where(predicates.toArray(new Predicate[0]));
        final List<UserEventListenerParameters> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            throw new RuntimeException(
                    String.format("No UserEventListenerParameters found for zaaktypeUUID: '%s' and planitemDefinitionID: '%s'", zaaktypeUUID.toString(),
                                  planitemDefinitionID));
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
                .findAny().orElseThrow(() -> new RuntimeException(
                        String.format("No ZaakbeeindigParameter found for zaaktypeUUID: '%s' and zaakbeeindigRedenId: '%d'", zaaktypeUUID.toString(),
                                      zaakbeeindigRedenId)));
    }
}
