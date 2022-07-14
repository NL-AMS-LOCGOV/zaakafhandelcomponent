/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import net.atos.zac.app.taken.model.RESTTaakActies;
import net.atos.zac.policy.output.TaakActies;

public class RESTTaakActiesConverter {

    public RESTTaakActies convert(final TaakActies taakActies) {
        final RESTTaakActies restTaakActies = new RESTTaakActies();
        restTaakActies.lezen = taakActies.getLezen();
        restTaakActies.wijzigenToekenning = taakActies.getWijzigenToekenning();
        restTaakActies.wijzigenFormulier = taakActies.getWijzigenFormulier();
        restTaakActies.wijzigenOverig = taakActies.getWijzigenOverig();
        restTaakActies.creeerenDocument = taakActies.getCreeerenDocument();
        restTaakActies.toevoegenDocument = taakActies.getToevoegenDocument();
        return restTaakActies;
    }
}
