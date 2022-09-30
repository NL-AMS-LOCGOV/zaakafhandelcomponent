/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import net.atos.zac.app.informatieobjecten.model.RESTDocumentActies;
import net.atos.zac.policy.output.DocumentActies;

public class RESTEnkelvoudigInformatieobjectActiesConverter {

    public RESTDocumentActies convert(final DocumentActies acties) {
        final RESTDocumentActies restActies = new RESTDocumentActies();
        restActies.lezen = acties.getLezen();
        restActies.wijzigen = acties.getWijzigen();
        restActies.koppelen = acties.getKoppelen();
        restActies.downloaden = acties.getDownloaden();
        restActies.ontgrendelen = acties.getOntgrendelen();
        restActies.vergrendelen = acties.getVergrendelen();
        restActies.verwijderen = acties.getVerwijderen();
        restActies.toevoegenNieuweVersie = acties.getToevoegenNieuweVersie();
        restActies.ondertekenen = acties.getOndertekenen();
        return restActies;
    }
}
