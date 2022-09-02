/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.zaaksturing.model.ReferentieTabel;

@ApplicationScoped
@Transactional
public class ReferentieTabelBeheerService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public List<ReferentieTabel> listReferentieTabellen() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ReferentieTabel> query = builder.createQuery(ReferentieTabel.class);
        final Root<ReferentieTabel> root = query.from(ReferentieTabel.class);
        query.orderBy(builder.asc(root.get("naam")));
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

    public ReferentieTabel newReferentieTabel() {
        final ReferentieTabel nieuw = new ReferentieTabel();
        nieuw.setCode(getUniqueCode(1, listReferentieTabellen()));
        nieuw.setNaam("Nieuwe referentietabel");
        return nieuw;
    }

    private String getUniqueCode(final int i, final List<ReferentieTabel> list) {
        final String code = "TABEL" + i;
        if (list.stream()
                .anyMatch(referentieTabel -> code.equals(referentieTabel.getCode()))) {
            return getUniqueCode(i + 1, list);
        }
        return code;
    }

    public ReferentieTabel readReferentieTabel(final long id) {
        final ReferentieTabel referentieTabel = entityManager.find(ReferentieTabel.class, id);
        if (referentieTabel != null) {
            return referentieTabel;
        } else {
            throw new RuntimeException(String.format("%s with id=%d not found", ReferentieTabel.class.getSimpleName(), id));
        }
    }

    public ReferentieTabel createReferentieTabel(final ReferentieTabel referentieTabel) {
        return updateReferentieTabel(referentieTabel);
    }

    public ReferentieTabel updateReferentieTabel(final ReferentieTabel referentieTabel) {
        valideerObject(referentieTabel);
        return entityManager.merge(referentieTabel);
    }

    public void deleteReferentieTabel(final long id) {
        entityManager.remove(
                entityManager.find(ReferentieTabel.class, id));
    }
}
