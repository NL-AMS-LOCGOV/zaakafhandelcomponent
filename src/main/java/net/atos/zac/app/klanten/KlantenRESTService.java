/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten;

import static net.atos.zac.app.klanten.converter.RESTPersoonConverter.FIELDS_PERSOON_OVERZICHT;

import java.util.List;

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
import net.atos.zac.app.klanten.converter.RESTBedrijfConverter;
import net.atos.zac.app.klanten.converter.RESTPersoonConverter;
import net.atos.zac.app.klanten.model.bedrijven.RESTBedrijf;
import net.atos.zac.app.klanten.model.bedrijven.RESTListBedrijvenParameters;
import net.atos.zac.app.klanten.model.personen.RESTListPersonenParameters;
import net.atos.zac.app.klanten.model.personen.RESTPersoon;
import net.atos.zac.app.klanten.model.personen.RESTPersoonOverzicht;

@Path("klanten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class KlantenRESTService {

    @Inject
    private BRPClientService brpClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private RESTPersoonConverter persoonConverter;

    @Inject
    private RESTBedrijfConverter bedrijfConverter;

    @GET
    @Path("persoonoverzicht/{bsn}")
    public RESTPersoonOverzicht readPersoonOverzicht(@PathParam("bsn") final String bsn) {
        final IngeschrevenPersoonHal persoon = brpClientService.findPersoon(bsn, FIELDS_PERSOON_OVERZICHT);
        return persoonConverter.convertToPersoonOverzicht(persoon);
    }

    @PUT
    @Path("personen")
    public List<RESTPersoon> listPersonen(final RESTListPersonenParameters restListPersonenParameters) {
        final ListPersonenParameters listPersonenParameters = persoonConverter.convert(restListPersonenParameters);
        final IngeschrevenPersoonHalCollectie ingeschrevenPersoonHalCollectie = brpClientService.listPersonen(listPersonenParameters);
        return persoonConverter.convert(ingeschrevenPersoonHalCollectie.getEmbedded().getIngeschrevenpersonen());
    }

    @PUT
    @Path("bedrijven")
    public List<RESTBedrijf> listBedrijven(final RESTListBedrijvenParameters restParameters) {
        KVKZoekenParameters zoekenParameters = bedrijfConverter.convert(restParameters);
        Resultaat resultaat = kvkClientService.zoeken(zoekenParameters);
        return bedrijfConverter.convert(resultaat);
    }
}
