/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.converter;

import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocumentListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.documenten.model.OntkoppeldDocumentListParameters;
import net.atos.zac.zoeken.model.DatumRange;

public class RESTOntkoppeldDocumentListParametersConverter extends RESTListParametersConverter<OntkoppeldDocumentListParameters, RESTOntkoppeldDocumentListParameters> {

    @Override
    protected void doConvert(final OntkoppeldDocumentListParameters listParameters, final RESTOntkoppeldDocumentListParameters restListParameters) {
        listParameters.setReden(restListParameters.reden);
        listParameters.setTitel(restListParameters.titel);

        if (restListParameters.creatiedatum != null && restListParameters.creatiedatum.hasValue()) {
            listParameters.setCreatiedatum(new DatumRange(restListParameters.creatiedatum.van, restListParameters.creatiedatum.tot));
        }

        if (restListParameters.ontkoppeldDoor != null) {
            listParameters.setOntkoppeldDoor(restListParameters.ontkoppeldDoor.id);
        }

        if (restListParameters.ontkoppeldOp != null && restListParameters.ontkoppeldOp.hasValue()) {
            listParameters.setOntkoppeldOp(new DatumRange(restListParameters.ontkoppeldOp.van, restListParameters.ontkoppeldOp.tot));
        }

        listParameters.setZaakID(restListParameters.zaakID);
    }

    @Override
    protected OntkoppeldDocumentListParameters getListParameters() {
        return new OntkoppeldDocumentListParameters();
    }
}
