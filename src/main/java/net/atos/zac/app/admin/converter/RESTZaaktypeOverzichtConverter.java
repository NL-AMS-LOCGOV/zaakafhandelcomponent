/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.admin.model.RESTZaaktypeOverzicht;

public class RESTZaaktypeOverzichtConverter {

    public RESTZaaktypeOverzicht convert(final Zaaktype zaaktype) {
        final RESTZaaktypeOverzicht restZaaktype = new RESTZaaktypeOverzicht();
        restZaaktype.uuid = zaaktype.getUUID();
        restZaaktype.identificatie = zaaktype.getIdentificatie();
        restZaaktype.doel = zaaktype.getDoel();
        restZaaktype.omschrijving = zaaktype.getOmschrijving();
        restZaaktype.servicenorm = zaaktype.isServicenormBeschikbaar();
        restZaaktype.versiedatum = zaaktype.getVersiedatum();
        restZaaktype.nuGeldig = zaaktype.isNuGeldig();
        restZaaktype.beginGeldigheid = zaaktype.getBeginGeldigheid();
        restZaaktype.eindeGeldigheid = zaaktype.getEindeGeldigheid();
        restZaaktype.vertrouwelijkheidaanduiding = zaaktype.getVertrouwelijkheidaanduiding();
        return restZaaktype;
    }
}
