/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.flowable.cmmn.api.repository.CaseDefinition;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.admin.converter.RESTCaseDefinitionConverter;
import net.atos.zac.app.admin.converter.RESTZaakResultaattypeConverter;
import net.atos.zac.app.admin.converter.RESTZaakafhandelParametersConverter;
import net.atos.zac.app.admin.converter.RESTZaakbeeindigRedenConverter;
import net.atos.zac.app.admin.model.RESTCaseDefinition;
import net.atos.zac.app.admin.model.RESTZaakResultaattype;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.admin.model.RESTZaakbeeindigReden;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CaseService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;
import net.atos.zac.zaaksturing.model.ZaakbeeindigReden;

@Singleton
@Path("zaakafhandelParameters")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ZaakafhandelParametersRESTService {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private CaseService caseService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private RESTZaakafhandelParametersConverter zaakafhandelParametersConverter;

    @Inject
    private RESTCaseDefinitionConverter caseDefinitionConverter;

    @Inject
    private RESTZaakResultaattypeConverter zaakResultaattypeConverter;

    @Inject
    private RESTZaakbeeindigRedenConverter zaakbeeindigRedenConverter;

    /**
     * Retrieve all CASE_DEFINITIONs that can be linked to a ZAAKTYPE
     *
     * @return LIST of CASE_DEFINITIONs
     */
    @GET
    @Path("caseDefinition")
    public List<RESTCaseDefinition> listCaseDefinitions() {
        final List<CaseDefinition> caseDefinitions = caseService.listCaseDefinitions();
        return caseDefinitions.stream()
                .map(caseDefinition -> new RESTCaseDefinition(caseDefinition.getName(), caseDefinition.getKey()))
                .toList();
    }

    /**
     * Retrieving a CASE_DEFINITION including it's PLAN_ITEM_DEFINITIONs
     *
     * @param caseDefinitionKey id of the CASE_DEFINITION
     * @return CASE_DEFINITION including it's PLAN_ITEM_DEFINITIONs
     */
    @GET
    @Path("caseDefinition/{key}")
    public RESTCaseDefinition readCaseDefinition(@PathParam("key") String caseDefinitionKey) {
        return caseDefinitionConverter.convertToRESTCaseDefinition(caseDefinitionKey);
    }

    /**
     * Retrieve all ZAAKAFHANDELPARAMETERS for overview
     *
     * @return LIST of ZAAKAFHANDELPARAMETERS
     */
    @GET
    public List<RESTZaakafhandelParameters> listZaakafhandelParameters() {
        return listZaaktypes().stream()
                .map(zaaktype -> UriUtil.uuidFromURI(zaaktype.getUrl()))
                .map(zaakafhandelParameterBeheerService::readZaakafhandelParameters)
                .map(zaakafhandelParameters -> zaakafhandelParametersConverter.convertZaakafhandelParameters(zaakafhandelParameters, false))
                .toList();
    }


    /**
     * Retrieve the ZAAKAFHANDELPARAMETERS for a ZAAKTYPE
     *
     * @return ZAAKAFHANDELPARAMETERS for a ZAAKTYPE by uuid of the ZAAKTYPE
     */
    @GET
    @Path("{zaaktypeUUID}")
    public RESTZaakafhandelParameters readZaakafhandelParameters(@PathParam("zaaktypeUUID") final UUID zaakTypeUUID) {
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaakTypeUUID);
        return zaakafhandelParametersConverter.convertZaakafhandelParameters(zaakafhandelParameters, true);
    }

    /**
     * Saves the ZAAKAFHANDELPARAMETERS
     *
     * @param restZaakafhandelParameters ZAAKAFHANDELPARAMETERS
     */
    @PUT
    public RESTZaakafhandelParameters updateZaakafhandelparameters(final RESTZaakafhandelParameters restZaakafhandelParameters) {
        ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParametersConverter.convertRESTZaakafhandelParameters(restZaakafhandelParameters);
        if (zaakafhandelParameters.getId() == null) {
            zaakafhandelParameters = zaakafhandelParameterBeheerService.createZaakafhandelParameters(zaakafhandelParameters);
        } else {
            zaakafhandelParameters = zaakafhandelParameterBeheerService.updateZaakafhandelParameters(zaakafhandelParameters);
        }
        return zaakafhandelParametersConverter.convertZaakafhandelParameters(zaakafhandelParameters, true);
    }

    /**
     * Retrieve all possible zaakbeeindig-redenen
     *
     * @return list of zaakbeeindig-redenen
     */
    @GET
    @Path("zaakbeeindigRedenen")
    public List<RESTZaakbeeindigReden> listZaakbeeindigRedenen() {
        return zaakbeeindigRedenConverter.convertZaakbeeindigRedenen(zaakafhandelParameterBeheerService.listZaakbeeindigRedenen());
    }

    /**
     * Retrieve zaakbeeindig redenen for a zaaktype
     *
     * @return list of zaakbeeindig-redenen
     */
    @GET
    @Path("zaakbeeindigRedenen/{zaaktypeUUID}")
    public List<RESTZaakbeeindigReden> listZaakbeeindigRedenenForZaaktype(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        final List<ZaakbeeindigReden> zaakbeeindigRedenen = zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaaktypeUUID)
                .getZaakbeeindigParameters().stream()
                .map(ZaakbeeindigParameter::getZaakbeeindigReden)
                .toList();
        return zaakbeeindigRedenConverter.convertZaakbeeindigRedenen(zaakbeeindigRedenen);
    }

    /**
     * Retrieve all resultaattypes for a zaaktype
     *
     * @param zaaktypeUUID the id of the zaaktype
     * @return list of resultaattypes
     */
    @GET
    @Path("resultaten/{zaaktypeUUID}")
    public List<RESTZaakResultaattype> listZaakResultaten(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        return zaakResultaattypeConverter.convertResultaattypes(ztcClientService.readResultaattypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()));
    }

    /**
     * Retrieve all FORMULIER_VERWIJZINGen that can be linked to a HUMAN_TASK_PLAN_ITEM
     *
     * @return lijst of FORMULIER_VERWIJZINGen
     */
    @GET
    @Path("formulierDefinities")
    public List<String> listFormulierDefinities() {
        return Arrays.stream(FormulierDefinitie.values()).map(Objects::toString).collect(Collectors.toList());
    }

    private List<Zaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI());
    }
}
