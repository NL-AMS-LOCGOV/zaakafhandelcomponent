/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import net.atos.client.zgw.drc.model.Ondertekening;
import net.atos.zac.app.informatieobjecten.model.RESTOndertekening;

public class RESTOndertekeningConverter {

    public RESTOndertekening convert(final Ondertekening ondertekening) {
        final RESTOndertekening restOndertekening = new RESTOndertekening();
        restOndertekening.soort = ondertekening.getSoort().toValue();
        restOndertekening.datum = ondertekening.getDatum();
        return restOndertekening;
    }
}
