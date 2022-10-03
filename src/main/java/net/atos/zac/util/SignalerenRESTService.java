/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.SecurityUtil;
import net.atos.zac.event.EventingService;
import net.atos.zac.util.event.JobEvent;
import net.atos.zac.util.event.JobId;

@Path("signaleren")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignalerenRESTService {

    @Inject
    private EventingService eventingService;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @GET
    public String zaakSignaleringenVerzenden() {
        SecurityUtil.setFunctioneelGebruiker(httpSession.get());
        eventingService.send(new JobEvent(JobId.SIGNALERINGEN_JOB));
        return String.format("%s: gestart...", JobId.SIGNALERINGEN_JOB.getName());
    }
}
