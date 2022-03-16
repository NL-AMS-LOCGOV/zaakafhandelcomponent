/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.signaleringen;

import java.time.ZonedDateTime;
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

import org.flowable.task.api.Task;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;

@Path("signaleringen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class SignaleringenRestService {

    @Inject
    private SignaleringenService signaleringenService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private FlowableService flowableService;

    @Inject
    private RESTZaakOverzichtConverter restZaakOverzichtConverter;

    @Inject
    private RESTTaakConverter restTaakConverter;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    @GET
    @Path("/medewerker/latestsignalering")
    public ZonedDateTime latestSignaleringenMedewerker() {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters();
        parameters.target(ingelogdeMedewerker.get());
        return signaleringenService.latestSignalering(parameters);
    }

    @GET
    @Path("/zaken/{type}")
    public List<RESTZaakOverzicht> listZakenSignalering(@PathParam("type") final String signaleringsType) {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters();
        parameters.type(SignaleringType.Type.ZAAK_OP_NAAM);
        parameters.target(ingelogdeMedewerker.get());

        final List<Zaak> zaken = signaleringenService.findSignaleringen(parameters)
                .stream()
                .map(signalering ->
                             zrcClientService.readZaak(
                                     UUID.fromString(signalering.getSubject())))
                .toList();
        return zaken.stream().map(zaak -> restZaakOverzichtConverter.convert(zaak)).toList();
    }

    @GET
    @Path("/taken/{type}")
    public List<RESTTaak> listTakenSignalering(@PathParam("type") final String signaleringsType) {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters();
        parameters.type(SignaleringType.Type.TAAK_OP_NAAM);
        parameters.target(ingelogdeMedewerker.get());

        final List<Task> taken = signaleringenService.findSignaleringen(parameters)
                .stream()
                .map(signalering -> flowableService.readTask(signalering.getSubject()))
                .toList();
        return taken.stream().map(taak -> restTaakConverter.convertTaskInfo(taak)).toList();
    }
}
