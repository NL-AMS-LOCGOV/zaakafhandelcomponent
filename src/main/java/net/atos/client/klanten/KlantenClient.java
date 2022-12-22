/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.klanten;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.klanten.exception.RuntimeExceptionMapper;
import net.atos.client.klanten.model.AuditTrail;
import net.atos.client.klanten.model.Klant;
import net.atos.client.klanten.model.KlantList200Response;
import net.atos.client.klanten.model.KlantListParameters;
import net.atos.client.klanten.util.KlantenClientHeadersFactory;

/**
 * Klanten API
 * <p>
 * Een API om zowel klanten te registreren als op te vragen.
 * Een klant is een natuurlijk persoon, niet-natuurlijk persoon (bedrijf) of vestiging waarbij het gaat om niet geverifieerde gegevens.
 */

@RegisterRestClient(configKey = "Klanten-API-Client")
@RegisterClientHeaders(KlantenClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(RuntimeExceptionMapper.class)
})
@Path("api/v1/klanten")
public interface KlantenClient {

    /**
     * Alle audit trail regels behorend bij de KLANT.
     */
    @GET
    @Path("/{klant_uuid}/audittrail")
    @Produces({"application/json", "application/problem+json"})
    public List<AuditTrail> audittrailList(@PathParam("klant_uuid") UUID klantUuid) throws ProcessingException;

    /**
     * Een specifieke audit trail regel opvragen.
     */
    @GET
    @Path("/{klant_uuid}/audittrail/{uuid}")
    @Produces({"application/json", "application/problem+json"})
    public AuditTrail audittrailRead(@PathParam("klant_uuid") UUID klantUuid, @PathParam("uuid") UUID uuid,
            @HeaderParam("If-None-Match") String ifNoneMatch) throws ProcessingException;

    /**
     * Maak een KLANT aan.
     */
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json", "application/problem+json"})
    public Klant klantCreate(@HeaderParam("Content-Type") String contentType, Klant klant,
            @HeaderParam("X-NLX-Logrecord-ID") String xNLXLogrecordID,
            @HeaderParam("X-Audit-Toelichting") String xAuditToelichting) throws ProcessingException;

    /**
     * Verwijder een KLANT.
     */
    @DELETE
    @Path("/{uuid}")
    @Produces({"application/problem+json"})
    public void klantDelete(@PathParam("uuid") UUID uuid, @HeaderParam("X-NLX-Logrecord-ID") String xNLXLogrecordID,
            @HeaderParam("X-Audit-Toelichting") String xAuditToelichting) throws ProcessingException;

    /**
     * Alle KLANTen opvragen.
     */
    @GET
    @Produces({"application/json", "application/problem+json"})
    public KlantList200Response klantList(
            @BeanParam final KlantListParameters listParameters) throws ProcessingException;

    /**
     * Alle KLANTen asynchroon opvragen.
     */
    @GET
    @Produces({"application/json", "application/problem+json"})
    public CompletionStage<KlantList200Response> klantListAsync(
            @BeanParam final KlantListParameters listParameters) throws ProcessingException;

    /**
     * Werk een KLANT deels bij.
     */
    @PATCH
    @Path("/{uuid}")
    @Consumes({"application/json"})
    @Produces({"application/json", "application/problem+json"})
    public Klant klantPartialUpdate(@PathParam("uuid") UUID uuid, @HeaderParam("Content-Type") String contentType,
            Klant klant, @HeaderParam("X-NLX-Logrecord-ID") String xNLXLogrecordID,
            @HeaderParam("X-Audit-Toelichting") String xAuditToelichting) throws ProcessingException;

    /**
     * Een specifiek KLANT opvragen.
     */
    @GET
    @Path("/{uuid}")
    @Produces({"application/json", "application/problem+json"})
    public Klant klantRead(@PathParam("uuid") UUID uuid,
            @HeaderParam("If-None-Match") String ifNoneMatch) throws ProcessingException;

    /**
     * Werk een KLANT in zijn geheel bij.
     */
    @PUT
    @Path("/{uuid}")
    @Consumes({"application/json"})
    @Produces({"application/json", "application/problem+json"})
    public Klant klantUpdate(@PathParam("uuid") UUID uuid, @HeaderParam("Content-Type") String contentType, Klant klant,
            @HeaderParam("X-NLX-Logrecord-ID") String xNLXLogrecordID,
            @HeaderParam("X-Audit-Toelichting") String xAuditToelichting) throws ProcessingException;
}
