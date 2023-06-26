/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.formulieren;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.formulieren.converter.RESTFormulierDefinitieConverter;
import net.atos.zac.app.formulieren.model.RESTFormulierDefinitie;
import net.atos.zac.app.formulieren.model.RuntimeContext;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.formulieren.FormulierDefinitieService;
import net.atos.zac.formulieren.FormulierRuntimeService;
import net.atos.zac.formulieren.model.FormulierDefinitie;

@Singleton
@Path("formulierRuntime")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FormulierRuntimeRESTService {

    @Inject
    private FormulierDefinitieService service;

    @Inject
    private RESTFormulierDefinitieConverter converter;

    @Inject
    private FormulierRuntimeService runtimeService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private TakenService takenService;

    @Inject
    private TaakVariabelenService taakVariabelenService;


    @PUT
    public RESTFormulierDefinitie run(final RuntimeContext context) {
        final FormulierDefinitie formulierDefinitie = service.readFormulierDefinitie(context.formulierSysteemnaam);
        runtimeService.resolveDefaultwaarden(formulierDefinitie);
        if (context.taak != null) {
            runtimeService.resolveDefaultwaarden(formulierDefinitie, context.taak);
            runtimeService.resolveDefaultwaarden(formulierDefinitie, context.taak.taakdata);
        } else if (context.taakID != null) {
            final TaskInfo task = takenService.readTask(context.taakID);
            runtimeService.resolveDefaultwaarden(formulierDefinitie, taakVariabelenService.readTaakdata(task));
            runtimeService.resolveDefaultwaarden(formulierDefinitie, task);
        }
        if (context.zaak != null) {
            runtimeService.resolveDefaultwaarden(formulierDefinitie, context.zaak);
        } else if (context.zaakUUID != null) {
            final Zaak zaak = zrcClientService.readZaak(context.zaakUUID);
            runtimeService.resolveDefaultwaarden(formulierDefinitie, zaak);
        }
        return converter.convert(formulierDefinitie, true);
    }
}
