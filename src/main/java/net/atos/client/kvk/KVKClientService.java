/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.kvk;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.kvk.basisprofiel.model.Vestiging;
import net.atos.client.kvk.exception.VestigingNotFoundException;
import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.zoeken.model.Resultaat;

@ApplicationScoped
public class KVKClientService {

    private static final Logger LOG = Logger.getLogger(KVKClientService.class.getName());

    @Inject
    @RestClient
    private ZoekenClient zoekenClient;

    @Inject
    @RestClient
    private BasisprofielClient basisprofielClient;

    public Resultaat zoeken(final KVKZoekenParameters parameters) {
        return zoekenClient.getResults(parameters);
    }

    public Vestiging findHoofdvestiging(final String kvkNummer) {
        try {
            return basisprofielClient.getHoofdvestiging(kvkNummer, false);
        } catch (final VestigingNotFoundException e) {
        } catch (final TimeoutException | ProcessingException e) {
            LOG.severe(() -> String.format("Error while calling KVK Basisprofiel provider: %s", e.getMessage()));
        }
        return null;
    }
}
