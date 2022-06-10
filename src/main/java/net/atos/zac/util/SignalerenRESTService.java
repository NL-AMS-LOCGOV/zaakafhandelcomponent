/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.event.EventingService;
import net.atos.zac.util.event.JobEvent;
import net.atos.zac.util.event.JobId;

@Path("signaleren")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignalerenRESTService {
    @Inject
    private EventingService eventingService;

    @GET
    @Path("zaak/verlopend")
    public String zaakSignaleringenVerzenden() {
        eventingService.send(new JobEvent(JobId.ZAAK_SIGNALERINGEN_JOB));
        return String.format("%s: gestart...", JobId.ZAAK_SIGNALERINGEN_JOB.getName());
    }

    @GET
    @Path("taak/verlopen")
    public String taakSignaleringenVerzenden() {
        eventingService.send(new JobEvent(JobId.TAAK_SIGNALERINGEN_JOB));
        return String.format("%s: gestart...", JobId.TAAK_SIGNALERINGEN_JOB.getName());
    }
}
