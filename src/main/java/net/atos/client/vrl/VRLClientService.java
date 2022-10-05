/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.vrl;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.vrl.exception.CommunicatiekanaalNotFoundException;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.vrl.model.CommunicatiekanaalList200Response;

@ApplicationScoped
public class VRLClientService {

    @RestClient
    @Inject
    private CommunicatiekanalenClient communicatiekanalenClient;

    public List<CommunicatieKanaal> listCommunicatiekanalen() {
        final CommunicatiekanaalList200Response results = communicatiekanalenClient.communicatiekanaalList(null);
        if (results.getNext() == null) {
            return results.getResults();
        } else {
            throw new IllegalStateException(String.format("Number of '%s' instances do not fit on a single page.", CommunicatieKanaal.class.getName()));
        }
    }

    public CommunicatieKanaal findCommunicatiekanaal(final UUID communicatiekanaalUUID) {
        try {
            return communicatiekanalenClient.communicatiekanaalRead(communicatiekanaalUUID);
        } catch (final CommunicatiekanaalNotFoundException e) {
            return null;
        }
    }

    public CommunicatieKanaal findCommunicatiekanaal(final String communicatiekanaalNaam) {
        return listCommunicatiekanalen().stream()
                .filter(communicatieKanaal -> communicatieKanaal.getNaam().equals(communicatiekanaalNaam))
                .findAny()
                .orElse(null);
    }
}
