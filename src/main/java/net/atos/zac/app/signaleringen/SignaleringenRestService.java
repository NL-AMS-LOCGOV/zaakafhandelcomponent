/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.signaleringen;

import java.util.List;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.signalering.SignaleringService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;

@Path("signaleringen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class SignaleringenRestService {

    @Inject
    private SignaleringService signaleringService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTZaakOverzichtConverter zaakOverzichtConverter;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    @GET
    @Path("/medewerker/amount")
    public int numberOfSignaleringenMedewerker() {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters();
        parameters.target(ingelogdeMedewerker.get());
        return signaleringService.countSignaleringen(parameters);
    }

    @GET
    @Path("/zaken/{type}")
    public List<RESTZaakOverzicht> listZakenSignalering(@PathParam("type") final String signaleringsType) {
//        final Signalering signalering = signaleringService.signaleringInstance(SignaleringType.Type.ZAAK_OP_NAAM);
//        signalering.setSubject(zaak);
//        signalering.setTarget(ingelogdeMedewerker.get());
//        signaleringService.createSignalering(signalering);

        final SignaleringZoekParameters parameters = new SignaleringZoekParameters();
        parameters.type(SignaleringType.Type.ZAAK_OP_NAAM);
        parameters.target(ingelogdeMedewerker.get());

        final List<Zaak> zaken = signaleringService.findSignaleringen(parameters)
                .stream()
                .map(signalering ->
                             zrcClientService.readZaak(
                                     UUID.fromString(signalering.getSubject())))
                .toList();
        return zaken.stream().map(zaak -> zaakOverzichtConverter.convert(zaak)).toList();
    }

}
