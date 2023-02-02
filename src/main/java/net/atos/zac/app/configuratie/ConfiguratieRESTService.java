/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.configuratie;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.configuratie.converter.RESTTaalConverter;
import net.atos.zac.app.configuratie.model.RESTTaal;
import net.atos.zac.configuratie.ConfiguratieService;

/**
 *
 */
@Path("configuratie")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ConfiguratieRESTService {

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private RESTTaalConverter taalConverter;

    @GET
    @Path("talen")
    public List<RESTTaal> listTalen() {
        return taalConverter.convert(configuratieService.listTalen());
    }

    @GET
    @Path("talen/default")
    public RESTTaal readDefaultTaal() {
        return configuratieService.findDefaultTaal().map(taalConverter::convert).orElse(null);
    }

    @GET
    @Path("maxFileSizeMB")
    public long readMaxFileSizeMB() {
        return configuratieService.readMaxFileSizeMB();
    }

    @GET
    @Path("additionalAllowedFileTypes")
    public List<String> readAdditionalAllowedFileTypes() {
        return configuratieService.readAdditionalAllowedFileTypes();
    }

    @GET
    @Path("gemeente")
    public String readGemeenteNaam() {
        return configuratieService.readGemeenteNaam();
    }
}
