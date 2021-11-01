/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.rechten;

import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.authentication.Medewerker;

public class TaakRechten {

    private TaakRechten() {}


    //TODO ESUITEDEV-25820 alle rechten checks vervangen door een solrTaak ipv behandelaarId en groepId strings

    public static boolean isToekennenToegestaan(final Medewerker ingelogdeMedewerker, final String behandelaarId, final String groepId) {
        return ingelogdeMedewerker.getGebruikersnaam().equals(behandelaarId) || ingelogdeMedewerker.isInGroup(groepId);
    }

    public static boolean isVrijgevenToegestaan(final Medewerker ingelogdeMedewerker, final String behandelaarId, final String groepId,
            final TaakStatus status) {
        return ingelogdeMedewerker.getGebruikersnaam().equals(behandelaarId) && status == TaakStatus.TOEGEKEND && ingelogdeMedewerker.isInGroup(groepId);
    }

    public static boolean isKenToeAanMijToegestaan(final Medewerker ingelogdeMedewerker, final String behandelaarId,
            final String groepId, final TaakStatus status) {
        return !ingelogdeMedewerker.getGebruikersnaam().equals(behandelaarId) && ingelogdeMedewerker.isInGroup(groepId) && status != TaakStatus.AFGEROND;
    }

    public static boolean isBehandelenToegestaan(final Medewerker ingelogdeMedewerker, final String behandelaarId, final String groepId,
            final TaakStatus status) {
        return ingelogdeMedewerker.getGebruikersnaam().equals(behandelaarId) && ingelogdeMedewerker.isInGroup(groepId) && status == TaakStatus.TOEGEKEND;
    }

}
