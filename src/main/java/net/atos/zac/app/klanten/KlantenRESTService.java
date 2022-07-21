/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten;

import static net.atos.zac.app.klanten.converter.RESTPersoonConverter.FIELDS_PERSOON;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.brp.BRPClientService;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.IngeschrevenPersoonHalCollectie;
import net.atos.client.brp.model.ListPersonenParameters;
import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.zoeken.model.Resultaat;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.klanten.converter.RESTBedrijfConverter;
import net.atos.zac.app.klanten.converter.RESTPersoonConverter;
import net.atos.zac.app.klanten.converter.RESTRoltypeConverter;
import net.atos.zac.app.klanten.model.bedrijven.RESTBedrijf;
import net.atos.zac.app.klanten.model.bedrijven.RESTListBedrijvenParameters;
import net.atos.zac.app.klanten.model.klant.RESTRoltype;
import net.atos.zac.app.klanten.model.personen.RESTListPersonenParameters;
import net.atos.zac.app.klanten.model.personen.RESTPersoon;
import net.atos.zac.app.shared.RESTResultaat;

@Path("klanten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class KlantenRESTService {

    private static final Logger LOG = Logger.getLogger(KlantenRESTService.class.getName());

    public static final Set<AardVanRol> betrokkenen;

    static {
        betrokkenen = EnumSet.allOf(AardVanRol.class);
        betrokkenen.remove(AardVanRol.INITIATOR);
        betrokkenen.remove(AardVanRol.BEHANDELAAR);
    }

    @Inject
    private BRPClientService brpClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private RESTPersoonConverter persoonConverter;

    @Inject
    private RESTBedrijfConverter bedrijfConverter;

    @Inject
    private RESTRoltypeConverter roltypeConverter;

    @GET
    @Path("persoon/{bsn}")
    public RESTPersoon readPersoon(@PathParam("bsn") final String bsn) {
        final IngeschrevenPersoonHal persoon = brpClientService.findPersoon(bsn, FIELDS_PERSOON);
        return persoonConverter.convertToPersoon(persoon);
    }

    @GET
    @Path("bedrijf/{vestigingsnummer}")
    public RESTBedrijf readBedrijf(@PathParam("vestigingsnummer") final String vestigingsnummer) {
        return bedrijfConverter.convert(kvkClientService.findVestiging(vestigingsnummer));
    }

    @PUT
    @Path("personen")
    public RESTResultaat<RESTPersoon> listPersonen(final RESTListPersonenParameters restListPersonenParameters) {
        try {
            final ListPersonenParameters listPersonenParameters = persoonConverter.convert(restListPersonenParameters);
            final IngeschrevenPersoonHalCollectie ingeschrevenPersoonHalCollectie = brpClientService.listPersonen(listPersonenParameters);
            return new RESTResultaat<>(persoonConverter.convert(ingeschrevenPersoonHalCollectie.getEmbedded().getIngeschrevenpersonen()));
        } catch (final RuntimeException e) {
            LOG.severe(() -> String.format("Error while calling listPersonen: %s", e.getMessage()));
            return new RESTResultaat<>(e.getMessage());
        }
    }

    @PUT
    @Path("bedrijven")
    public RESTResultaat<RESTBedrijf> listBedrijven(final RESTListBedrijvenParameters restParameters) {
        try {
            KVKZoekenParameters zoekenParameters = bedrijfConverter.convert(restParameters);
            Resultaat resultaat = kvkClientService.find(zoekenParameters);
            return new RESTResultaat<>(bedrijfConverter.convert(resultaat));
        } catch (final RuntimeException e) {
            LOG.severe(() -> String.format("Error while calling listBedrijven: %s", e.getMessage()));
            return new RESTResultaat<>(e.getMessage());
        }
    }

    @GET
    @Path("roltype/{zaaktypeUuid}/betrokkene")
    public List<RESTRoltype> listBetrokkeneRoltypen(@PathParam("zaaktypeUuid") final UUID zaaktype) {
        return roltypeConverter.convert(
                ztcClientService.listRoltypen(ztcClientService.readZaaktype(zaaktype).getUrl()).stream()
                        .filter(roltype -> betrokkenen.contains(roltype.getOmschrijvingGeneriek()))
                        .sorted(Comparator.comparing(Roltype::getOmschrijving)));
    }
}
