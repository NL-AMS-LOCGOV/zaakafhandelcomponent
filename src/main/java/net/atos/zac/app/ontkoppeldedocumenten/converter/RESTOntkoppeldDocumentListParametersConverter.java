/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.converter;

import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocumentListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.documenten.model.OntkoppeldDocumentListParameters;

public class RESTOntkoppeldDocumentListParametersConverter extends RESTListParametersConverter<OntkoppeldDocumentListParameters, RESTOntkoppeldDocumentListParameters> {

    @Override
    protected void doConvert(final OntkoppeldDocumentListParameters listParameters, final RESTOntkoppeldDocumentListParameters restListParameters) {
        listParameters.setIdentificatie(restListParameters.identificatie);
        listParameters.setTitel(restListParameters.titel);
        listParameters.setCreatiedatum(restListParameters.creatiedatum);
        listParameters.setOntkoppeldDoor(restListParameters.ontkoppeldDoor);
        listParameters.setOntkoppeldOp(restListParameters.ontkoppeldOp);
        listParameters.setZaakId(restListParameters.zaakId);
    }

    @Override
    protected OntkoppeldDocumentListParameters getListParameters() {
        return new OntkoppeldDocumentListParameters();
    }
}
