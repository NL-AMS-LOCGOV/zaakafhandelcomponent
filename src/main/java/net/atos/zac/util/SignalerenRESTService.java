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

import net.atos.zac.signalering.SignaleringenJob;

@Path("signaleren")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignalerenRESTService {
    @Inject
    private SignaleringenJob signaleringenJob;

    @GET
    @Path("zaak/verlopend")
    public String zaakSignaleringenVerzenden() {
        signaleringenJob.zaakSignaleringenVerzenden();
        return String.format("%s: gestart...", SignaleringenJob.ZAAK_SIGNALERINGEN_VERZENDEN);
    }

    @GET
    @Path("taak/verlopen")
    public String taakSignaleringenVerzenden() {
        return "TODO";
    }
}
