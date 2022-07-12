/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobjectActies;
import net.atos.zac.policy.output.EnkelvoudigInformatieobjectActies;

public class RESTEnkelvoudigInformatieobjectActiesConverter {

    public RESTEnkelvoudigInformatieobjectActies convert(final EnkelvoudigInformatieobjectActies acties) {
        final RESTEnkelvoudigInformatieobjectActies restActies = new RESTEnkelvoudigInformatieobjectActies();
        restActies.lezen = acties.getLezen();
        restActies.bewerken = acties.getBewerken();
        restActies.koppelen = acties.getKoppelen();
        restActies.downloaden = acties.getDownloaden();
        restActies.ontgrendelen = acties.getOntgrendelen();
        restActies.vergrendelen = acties.getVergrendelen();
        restActies.verwijderen = acties.getVerwijderen();
        restActies.toevoegenNieuweVersie = acties.getToevoegenNieuweVersie();
        return restActies;
    }
}
