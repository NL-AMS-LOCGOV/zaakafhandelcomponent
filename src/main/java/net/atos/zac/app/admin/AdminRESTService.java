/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.admin.model.RESTCaseModel;
import net.atos.zac.app.admin.model.RESTFormulierDefinitieVerwijzing;
import net.atos.zac.app.admin.model.RESTPlanItemDefinitie;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.planitems.model.PlanItemType;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.util.ConfigurationService;

@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminRESTService {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    /**
     * Retrieve all ZAAKTYPEs
     *
     * @return LIST of ZAAKTYPEs
     */
    @GET
    @Path("zaaktype")
    public List<RESTZaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configurationService.readDefaultCatalogusURI()).stream()
                .map(zaaktypeConverter::convert)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all CASE_MODELs that can be linked to a ZAAKTYPE
     *
     * @return LIST of CASE_MODELs
     */
    @GET
    @Path("caseModel")
    public List<RESTCaseModel> listCaseModel() {
        //todo ZaakafhandelParameterBeheerService - zaakafhandelcomponent #167
        return List.of(new RESTCaseModel("MOR Anoniem", "mor_anoniem"),
                       new RESTCaseModel("Melding klein evenement", "melding-klein-evenement"));
    }

    /**
     * Retrieving a CASE_MODEL including it's PLAN_ITEM_DEFINITIEs
     *
     * @param key id of the CASE_MODEL
     * @return CASE_MODEL including it's PLAN_ITEM_DEFINITIEs
     */
    @GET
    @Path("caseModel/{key}")
    public RESTCaseModel readCaseModel(@PathParam("key") String key) {
        //todo ZaakafhandelParameterBeheerService - zaakafhandelcomponent #167
        RESTCaseModel caseModel = new RESTCaseModel(key, key);
        caseModel.planItemDefinities = List.of(new RESTPlanItemDefinitie(String.format("Taak 1 (%s)", key), "task1_" + key, PlanItemType.HUMAN_TASK),
                                               new RESTPlanItemDefinitie(String.format("Taak 2 (%s)", key), "task2_" + key, PlanItemType.HUMAN_TASK));
        return caseModel;
    }

    /**
     * Retrieve all ZAAKAFHANDELPARAMETERS for overview
     *
     * @return LIST of ZAAKAFHANDELPARAMETERS
     */
    @GET
    @Path("parameters")
    public List<RESTZaakafhandelParameters> listZaakafhandelParameters() {
        //todo ZaakafhandelParameters opslaan #168
        List<RESTZaakafhandelParameters> parametersList = new ArrayList<>();
        List<RESTZaaktype> restZaaktypes = listZaaktypes();
        for (RESTZaaktype restZaaktype : restZaaktypes) {
            final RESTZaakafhandelParameters params = new RESTZaakafhandelParameters();
            params.zaaktype = restZaaktype;
            params.caseModel = new RESTCaseModel(restZaaktype.referentieproces, restZaaktype.referentieproces);
            parametersList.add(params);
        }
        return parametersList;
    }


    /**
     * Retrieve the ZAAKAFHANDELPARAMETERS for a ZAAKTYPE
     *
     * @return ZAAKAFHANDELPARAMETERS for a ZAAKTYPE by uuid of the ZAAKTYPE
     */
    @GET
    @Path("parameters/{zaaktypeUuid}")
    public RESTZaakafhandelParameters readZaakafhandelParameters(@PathParam("zaaktypeUuid") final UUID zaaktypeUuid) {
        //todo ZaakafhandelParameters opslaan #168
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaaktypeUuid);
        final RESTZaakafhandelParameters params = new RESTZaakafhandelParameters();
        params.zaaktype = zaaktypeConverter.convert(zaaktype);
        params.caseModel = new RESTCaseModel(zaaktype.getReferentieproces().getNaam(), zaaktype.getReferentieproces().getNaam());
        params.planItemParameters = new ArrayList<>();
        return params;
    }

    /**
     * Saves the ZAAKAFHANDELPARAMETERS
     *
     * @param params ZAAKAFHANDELPARAMETERS
     */
    @PUT
    @Path("parameters")
    public void updateZaakafhandelparameters(RESTZaakafhandelParameters params) {
        //todo ZaakafhandelParameters opslaan #168
    }

    /**
     * Retrieve all FORMULIER_VERWIJZINGen that can be linked to a HUMAN_TASK_PLAN_ITEM
     *
     * @return lijst of FORMULIER_VERWIJZINGen
     */
    @GET
    @Path("formulierDefinities")
    public List<RESTFormulierDefinitieVerwijzing> listFormulierDefinities() {
        //todo ZaakafhandelParameterBeheerService - zaakafhandelcomponent #167
        return List.of(new RESTFormulierDefinitieVerwijzing("Formulier 1", "form1"),
                       new RESTFormulierDefinitieVerwijzing("Formulier 2", "form2"));
    }
}
