/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.ontkoppeldedocumenten.converter.RESTListParametersConverter;
import net.atos.zac.app.ontkoppeldedocumenten.converter.RESTOntkoppeldDocumentConverter;
import net.atos.zac.app.ontkoppeldedocumenten.model.RESTListParameters;
import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocument;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;

@Singleton
@Path("ontkoppeldedocumenten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OntkoppeldeDocumentenRESTService {

    @Inject
    private OntkoppeldeDocumentenService service;

    @Inject
    private RESTOntkoppeldDocumentConverter converter;

    @GET
    @Path("")
    public List<RESTOntkoppeldDocument> list(@BeanParam final RESTListParameters listParameters) {
        return converter.convert(service.list(RESTListParametersConverter.convert(listParameters)));
    }

}
