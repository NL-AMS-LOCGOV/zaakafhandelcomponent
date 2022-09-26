/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.shared.exception.FoutmeldingException;
import net.atos.zac.zaaksturing.model.HumanTaskReferentieTabel;
import net.atos.zac.zaaksturing.model.ReferentieTabel;

@ApplicationScoped
@Transactional
public class ReferentieTabelBeheerService {

    private static final String UNIQUE_CONSTRAINT = "Er bestaat al een referentietabel met de code \"%s\".";

    private static final String FOREIGN_KEY_CONSTRAINT = "Deze referentietabel wordt gebruikt (voor: %s) en kan niet verwijderd worden.";

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private ReferentieTabelService referentieTabelService;

    public ReferentieTabel newReferentieTabel() {
        final ReferentieTabel nieuw = new ReferentieTabel();
        nieuw.setCode(getUniqueCode(1, referentieTabelService.listReferentieTabellen()));
        nieuw.setNaam("Nieuwe referentietabel");
        return nieuw;
    }

    private String getUniqueCode(final int i, final List<ReferentieTabel> list) {
        final String code = "TABEL" + (1 < i ? i : "");
        if (list.stream()
                .anyMatch(referentieTabel -> code.equals(referentieTabel.getCode()))) {
            return getUniqueCode(i + 1, list);
        }
        return code;
    }

    public ReferentieTabel createReferentieTabel(final ReferentieTabel referentieTabel) {
        return updateReferentieTabel(referentieTabel);
    }

    public ReferentieTabel updateReferentieTabel(final ReferentieTabel referentieTabel) {
        valideerObject(referentieTabel);
        final ReferentieTabel existing = referentieTabelService.findReferentieTabel(referentieTabel.getCode());
        if (existing != null && !existing.getId().equals(referentieTabel.getId())) {
            throw new FoutmeldingException(String.format(UNIQUE_CONSTRAINT, referentieTabel.getCode()));
        }
        return entityManager.merge(referentieTabel);
    }

    public void deleteReferentieTabel(final long id) {
        final ReferentieTabel tabel = entityManager.find(ReferentieTabel.class, id);
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<HumanTaskReferentieTabel> query = builder.createQuery(HumanTaskReferentieTabel.class);
        final Root<HumanTaskReferentieTabel> root = query.from(HumanTaskReferentieTabel.class);
        query.select(root).where(builder.equal(root.get("tabel").get("id"), tabel.getId()));
        final List<HumanTaskReferentieTabel> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            throw new FoutmeldingException(String.format(FOREIGN_KEY_CONSTRAINT, resultList.stream()
                    .map(HumanTaskReferentieTabel::getVeld)
                    .distinct()
                    .collect(Collectors.joining(", "))
            ));
        }
        entityManager.remove(tabel);
    }
}
