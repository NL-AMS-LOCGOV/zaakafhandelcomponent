/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * API Vestigingsprofiel
 * Documentatie voor API Vestigingsprofiel.
 * <p>
 * The version of the OpenAPI document: 1.3
 */
package net.atos.client.kvk;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.kvk.exception.KvKClientNoResultExceptionMapper;
import net.atos.client.kvk.exception.RuntimeExceptionMapper;
import net.atos.client.kvk.vestigingsprofiel.model.Vestiging;


@RegisterRestClient(configKey = "KVK-API-Client")
@RegisterProviders({
        @RegisterProvider(RuntimeExceptionMapper.class),
        @RegisterProvider(KvKClientNoResultExceptionMapper.class)
})
@Produces({"application/hal+json"})
@Path("api/v1/vestigingsprofielen/{vestigingsnummer}")
@Timeout(unit = ChronoUnit.SECONDS, value = 5)
public interface VestigingsprofielClient {

    /**
     * Voor een vestiging zoeken naar basisinformatie.
     * <p>
     * Er worden max. 1000 resultaten getoond.
     */
    @GET
    Vestiging getVestiging(@PathParam("vestigingsnummer") final String vestigingsnummer);

    /**
     * Voor een vestiging asynchroon zoeken naar basisinformatie.
     * <p>
     * Er worden max. 1000 resultaten getoond.
     */
    @GET
    CompletionStage<Vestiging> getVestigingAsync(@PathParam("vestigingsnummer") final String vestigingsnummer);
}
