/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.formulieren;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.formulieren.model.FormulierDefinitie;

@ApplicationScoped
@Transactional
public class FormulierDefinitieService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public List<FormulierDefinitie> listFormulierDefinities() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<FormulierDefinitie> query = builder.createQuery(FormulierDefinitie.class);
        final Root<FormulierDefinitie> root = query.from(FormulierDefinitie.class);
        query.orderBy(builder.asc(root.get("naam")));
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

    public FormulierDefinitie readFormulierDefinitie(final long id) {
        final FormulierDefinitie formulierDefinitie = entityManager.find(FormulierDefinitie.class, id);
        if (formulierDefinitie != null) {
            return formulierDefinitie;
        } else {
            throw new RuntimeException("%s with id=%d not found".formatted(FormulierDefinitie.class.getSimpleName(), id));
        }
    }

    public FormulierDefinitie readFormulierDefinitie(final String systeemnaam) {
        return findFormulierDefinitie(systeemnaam)
                .orElseThrow(() -> new RuntimeException(
                        "%s with code='%s' not found".formatted(FormulierDefinitie.class.getSimpleName(), systeemnaam)));
    }

    public Optional<FormulierDefinitie> findFormulierDefinitie(final String systeemnaam) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<FormulierDefinitie> query = builder.createQuery(FormulierDefinitie.class);
        final Root<FormulierDefinitie> root = query.from(FormulierDefinitie.class);
        query.select(root).where(builder.equal(root.get("systeemnaam"), systeemnaam));
        final List<FormulierDefinitie> resultList = entityManager.createQuery(query).getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    public FormulierDefinitie createFormulierDefinitie(final FormulierDefinitie formulierDefinitie) {
        final ZonedDateTime now = ZonedDateTime.now();
        formulierDefinitie.setCreatiedatum(now);
        formulierDefinitie.setWijzigingsdatum(now);
        valideerObject(formulierDefinitie);
        findFormulierDefinitie(formulierDefinitie.getSysteemnaam()).ifPresent(e -> {
            if (!e.getId().equals(formulierDefinitie.getId())) {
                throw new RuntimeException("Er bestaat al een formulier definitie met systeemnaam '%s'".formatted(
                        formulierDefinitie.getSysteemnaam()));
            }
        });
        return entityManager.merge(formulierDefinitie);
    }

    public FormulierDefinitie updateFormulierDefinitie(final FormulierDefinitie formulierDefinitie) {
        final FormulierDefinitie bestaandeDefinitie = readFormulierDefinitie(formulierDefinitie.getId());
        formulierDefinitie.setSysteemnaam(bestaandeDefinitie.getSysteemnaam());
        formulierDefinitie.setCreatiedatum(bestaandeDefinitie.getCreatiedatum());
        formulierDefinitie.setWijzigingsdatum(ZonedDateTime.now());
        valideerObject(formulierDefinitie);
        return entityManager.merge(formulierDefinitie);
    }

    public void deleteFormulierDefinitie(final long id) {
        final FormulierDefinitie formulierDefinitie = entityManager.find(FormulierDefinitie.class, id);
        // controleren op gebruik
        entityManager.remove(formulierDefinitie);
    }
}
