/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.zac.app.zaken.model.RESTCommunicatiekanaal;
import net.atos.zac.util.UriUtil;

public class RESTCommunicatiekanaalConverter {

    public RESTCommunicatiekanaal convert(final CommunicatieKanaal communicatieKanaal) {
        final RESTCommunicatiekanaal restCommunicatiekanaal = new RESTCommunicatiekanaal();
        restCommunicatiekanaal.uuid = UriUtil.uuidFromURI(communicatieKanaal.getUrl());
        restCommunicatiekanaal.naam = communicatieKanaal.getNaam();
        return restCommunicatiekanaal;
    }
}
