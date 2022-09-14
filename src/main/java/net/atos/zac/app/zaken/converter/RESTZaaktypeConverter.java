/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.util.UriUtil;

public class RESTZaaktypeConverter {

    public RESTZaaktype convert(final Zaaktype zaaktype) {
        final RESTZaaktype restZaaktype = new RESTZaaktype();
        restZaaktype.uuid = UriUtil.uuidFromURI(zaaktype.getUrl());
        restZaaktype.identificatie = zaaktype.getIdentificatie();
        restZaaktype.doel = zaaktype.getDoel();
        restZaaktype.omschrijving = zaaktype.getOmschrijving();
        restZaaktype.servicenorm = zaaktype.isServicenormBeschikbaar();
        restZaaktype.versiedatum = zaaktype.getVersiedatum();
        restZaaktype.nuGeldig = zaaktype.isNuGeldig();
        restZaaktype.beginGeldigheid = zaaktype.getBeginGeldigheid();
        restZaaktype.eindeGeldigheid = zaaktype.getEindeGeldigheid();
        restZaaktype.vertrouwelijkheidaanduiding = zaaktype.getVertrouwelijkheidaanduiding();
        if (zaaktype.getReferentieproces() != null) {
            restZaaktype.referentieproces = zaaktype.getReferentieproces().getNaam();
        }
        return restZaaktype;
    }
}
