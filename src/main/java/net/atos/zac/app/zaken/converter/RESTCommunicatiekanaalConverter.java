/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.util.List;
import java.util.UUID;

import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.zac.app.zaken.model.RESTCommunicatiekanaal;
import net.atos.zac.util.UriUtil;

public class RESTCommunicatiekanaalConverter {

    private static final String COMMUNICATIE_KANAAL_ONBEKEND = "<onbekend>";

    public List<RESTCommunicatiekanaal> convert(final List<CommunicatieKanaal> communicatieKanalen) {
        return communicatieKanalen.stream()
                .map(this::convert)
                .toList();
    }

    public RESTCommunicatiekanaal convert(final CommunicatieKanaal communicatieKanaal, final UUID communicatiekanaalUUID) {
        final RESTCommunicatiekanaal restCommunicatiekanaal = new RESTCommunicatiekanaal();
        restCommunicatiekanaal.uuid = communicatiekanaalUUID;
        restCommunicatiekanaal.naam = communicatieKanaal != null ? communicatieKanaal.getNaam() : COMMUNICATIE_KANAAL_ONBEKEND;
        return restCommunicatiekanaal;
    }

    private RESTCommunicatiekanaal convert(final CommunicatieKanaal communicatieKanaal) {
        final RESTCommunicatiekanaal restCommunicatiekanaal = new RESTCommunicatiekanaal();
        restCommunicatiekanaal.uuid = UriUtil.uuidFromURI(communicatieKanaal.getUrl());
        restCommunicatiekanaal.naam = communicatieKanaal.getNaam();
        return restCommunicatiekanaal;
    }
}
