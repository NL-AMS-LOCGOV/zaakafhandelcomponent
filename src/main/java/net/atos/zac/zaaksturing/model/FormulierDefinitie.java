/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum FormulierDefinitie {
    DEFAULT_TAAKFORMULIER,
    AANVULLENDE_INFORMATIE,
    ADVIES(FormulierVeldDefinitie.ADVIES),
    EXTERN_ADVIES_VASTLEGGEN,
    EXTERN_ADVIES_MAIL,
    GOEDKEUREN,
    DOCUMENT_VERZENDEN_POST;

    private final Set<FormulierVeldDefinitie> veldDefinities;

    FormulierDefinitie(final FormulierVeldDefinitie... veldDefinities) {
        final EnumSet<FormulierVeldDefinitie> formulierVeldDefinities = EnumSet.noneOf(FormulierVeldDefinitie.class);
        Collections.addAll(formulierVeldDefinities, veldDefinities);
        this.veldDefinities = Collections.unmodifiableSet(formulierVeldDefinities);
    }

    public Set<FormulierVeldDefinitie> getVeldDefinities() {
        return veldDefinities;
    }
}
