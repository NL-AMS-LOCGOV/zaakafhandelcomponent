/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten.converter;

import net.atos.zac.app.inboxdocumenten.model.RESTInboxDocumentListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.documenten.model.InboxDocumentListParameters;
import net.atos.zac.zoeken.model.DatumRange;

public class RESTInboxDocumentListParametersConverter extends RESTListParametersConverter<InboxDocumentListParameters> {

    public RESTInboxDocumentListParametersConverter() {
        super();
    }

    public InboxDocumentListParameters convert(final RESTInboxDocumentListParameters restParameters) {
        final InboxDocumentListParameters parameters = super.convert(restParameters);
        parameters.setIdentificatie(restParameters.identificatie);
        parameters.setTitel(restParameters.titel);
        if (restParameters.creatiedatum != null && restParameters.creatiedatum.hasValue()) {
            parameters.setCreatiedatum(new DatumRange(restParameters.creatiedatum.van, restParameters.creatiedatum.tot));
        }

        return parameters;
    }

    @Override
    public InboxDocumentListParameters getListParameters() {
        return new InboxDocumentListParameters();
    }
}
