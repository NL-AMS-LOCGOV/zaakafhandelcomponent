/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.productaanvragen.converter;

import net.atos.zac.aanvraag.model.InboxProductaanvraagListParameters;
import net.atos.zac.app.productaanvragen.model.RESTInboxProductaanvraagListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.zoeken.model.DatumRange;

public class RESTInboxProductaanvraagListParametersConverter extends RESTListParametersConverter<InboxProductaanvraagListParameters, RESTInboxProductaanvraagListParameters> {

    @Override
    protected void doConvert(final InboxProductaanvraagListParameters listParameters, final RESTInboxProductaanvraagListParameters restListParameters) {
        listParameters.setType(restListParameters.type);
        listParameters.setInitiatorID(restListParameters.initiatorID);

        if (restListParameters.ontvangstdatum != null && restListParameters.ontvangstdatum.hasValue()) {
            listParameters.setOntvangstdatum(new DatumRange(restListParameters.ontvangstdatum.van, restListParameters.ontvangstdatum.tot));
        }
    }

    @Override
    protected InboxProductaanvraagListParameters getListParameters() {
        return new InboxProductaanvraagListParameters();
    }
}
