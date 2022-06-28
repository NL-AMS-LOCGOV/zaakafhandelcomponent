/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.converter;

import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocumentListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.documenten.model.OntkoppeldDocumentListParameters;

public class RESTOntkoppeldDocumentListParametersConverter extends RESTListParametersConverter<OntkoppeldDocumentListParameters> {

    public RESTOntkoppeldDocumentListParametersConverter() {
        super();
    }

    public OntkoppeldDocumentListParameters convert(final RESTOntkoppeldDocumentListParameters restParameters) {
        final OntkoppeldDocumentListParameters parameters = super.convert(restParameters);
        parameters.setIdentificatie(restParameters.identificatie);
        parameters.setTitel(restParameters.titel);
        parameters.setCreatiedatum(restParameters.creatiedatum);
        parameters.setOntkoppeldDoor(restParameters.ontkoppeldDoor);
        parameters.setOntkoppeldOp(restParameters.ontkoppeldOp);
        parameters.setZaakId(restParameters.zaakId);
        return parameters;
    }

    @Override
    public OntkoppeldDocumentListParameters getListParameters() {
        return new OntkoppeldDocumentListParameters();
    }
}
