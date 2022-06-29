/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten.converter;

import net.atos.zac.app.inboxdocumenten.model.RESTInboxDocumentListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.documenten.model.InboxDocumentListParameters;
import net.atos.zac.zoeken.model.DatumRange;

public class RESTInboxDocumentListParametersConverter extends RESTListParametersConverter<InboxDocumentListParameters, RESTInboxDocumentListParameters> {

    @Override
    protected void doConvert(final InboxDocumentListParameters listParameters, final RESTInboxDocumentListParameters restListParameters) {
        listParameters.setIdentificatie(restListParameters.identificatie);
        listParameters.setTitel(restListParameters.titel);
        if (restListParameters.creatiedatum != null && restListParameters.creatiedatum.hasValue()) {
            listParameters.setCreatiedatum(new DatumRange(restListParameters.creatiedatum.van, restListParameters.creatiedatum.tot));
        }
    }

    @Override
    protected InboxDocumentListParameters getListParameters() {
        return new InboxDocumentListParameters();
    }
}
