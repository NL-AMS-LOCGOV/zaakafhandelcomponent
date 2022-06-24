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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjectConverter;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;
import net.atos.zac.app.signaleringen.converter.RESTSignaleringInstellingenConverter;
import net.atos.zac.app.signaleringen.model.RESTSignaleringInstellingen;
import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringInstellingenZoekParameters;
import net.atos.zac.signalering.model.SignaleringSubject;
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
    private TaskService taskService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private RESTZaakOverzichtConverter restZaakOverzichtConverter;

    @Inject
    private RESTTaakConverter restTaakConverter;

    @Inject
    private RESTInformatieobjectConverter restInformatieobjectConverter;

    @Inject
    private RESTSignaleringInstellingenConverter restSignaleringInstellingenConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @GET
    @Path("/latest")
    public ZonedDateTime latestSignaleringen() {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters(loggedInUserInstance.get());
        return signaleringenService.latestSignalering(parameters);
    }

    @GET
    @Path("/zaken/{type}")
    public List<RESTZaakOverzicht> listZakenSignaleringen(@PathParam("type") final SignaleringType.Type signaleringsType) {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters(loggedInUserInstance.get())
                .types(signaleringsType)
                .subjecttype(SignaleringSubject.ZAAK);
        return signaleringenService.findSignaleringen(parameters).stream()
                .map(signalering -> zrcClientService.readZaak(UUID.fromString(signalering.getSubject())))
                .map(restZaakOverzichtConverter::convert)
                .toList();
    }

    @GET
    @Path("/taken/{type}")
    public List<RESTTaak> listTakenSignaleringen(@PathParam("type") final SignaleringType.Type signaleringsType) {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters(loggedInUserInstance.get())
                .types(signaleringsType)
                .subjecttype(SignaleringSubject.TAAK);
        return signaleringenService.findSignaleringen(parameters).stream()
                .map(signalering -> taskService.readTask(signalering.getSubject()))
                .map(restTaakConverter::convertTask)
                .toList();
    }

    @GET
    @Path("/informatieobjecten/{type}")
    public List<RESTEnkelvoudigInformatieobject> listInformatieobjectenSignaleringen(@PathParam("type") final SignaleringType.Type signaleringsType) {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters(loggedInUserInstance.get())
                .types(signaleringsType)
                .subjecttype(SignaleringSubject.INFORMATIEOBJECT);
        return signaleringenService.findSignaleringen(parameters).stream()
                .map(signalering -> drcClientService.readEnkelvoudigInformatieobject(UUID.fromString(signalering.getSubject())))
                .map(restInformatieobjectConverter::convert)
                .toList();
    }

    @GET
    @Path("/instellingen")
    public List<RESTSignaleringInstellingen> listSignaleringInstellingen() {
        final SignaleringInstellingenZoekParameters parameters = new SignaleringInstellingenZoekParameters(loggedInUserInstance.get());
        return restSignaleringInstellingenConverter.convert(
                signaleringenService.listInstellingen(parameters));
    }

    @PUT
    @Path("/instellingen")
    public void updateSignaleringInstellingen(final RESTSignaleringInstellingen restInstellingen) {
        signaleringenService.createUpdateOrDeleteInstellingen(
                restSignaleringInstellingenConverter.convert(restInstellingen, loggedInUserInstance.get()));
    }

    @GET
    @Path("/typen/dashboard")
    public List<SignaleringType.Type> listDashboardSignaleringTypen() {
        final SignaleringInstellingenZoekParameters parameters = new SignaleringInstellingenZoekParameters(loggedInUserInstance.get())
                .dashboard();
        return signaleringenService.findInstellingen(parameters).stream()
                .map(instellingen -> instellingen.getType().getType())
                .toList();
    }
}
