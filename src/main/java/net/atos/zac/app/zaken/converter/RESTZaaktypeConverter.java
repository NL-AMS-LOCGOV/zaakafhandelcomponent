/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.admin.converter.RESTZaakafhandelParametersConverter;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

public class RESTZaaktypeConverter {

    @Inject
    private RESTZaakafhandelParametersConverter zaakafhandelParametersConverter;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

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
        restZaaktype.opschortingMogelijk = zaaktype.getOpschortingEnAanhoudingMogelijk();
        restZaaktype.verlengingMogelijk = zaaktype.getVerlengingMogelijk();
        if (restZaaktype.verlengingMogelijk) {
            LocalDateTime start = LocalDateTime.now();
            restZaaktype.verlengingstermijn = Long.valueOf(start.until(start.plus(zaaktype.getVerlengingstermijn()), ChronoUnit.DAYS)).intValue();
        }

        if (zaaktype.getReferentieproces() != null) {
            restZaaktype.referentieproces = zaaktype.getReferentieproces().getNaam();
        }
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(restZaaktype.uuid);
        restZaaktype.zaakafhandelparameters = zaakafhandelParametersConverter.convertZaakafhandelParameters(zaakafhandelParameters, true);
        return restZaaktype;
    }
}
