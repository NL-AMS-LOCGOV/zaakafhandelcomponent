/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import net.atos.zac.zaaksturing.model.ReferentieTabel;

@ApplicationScoped
@Transactional
public class ReferentieTabelBeheerService {

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
        final String code = "TABEL" + i;
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
        return entityManager.merge(referentieTabel);
    }

    public void deleteReferentieTabel(final long id) {
        entityManager.remove(
                entityManager.find(ReferentieTabel.class, id));
    }
}
