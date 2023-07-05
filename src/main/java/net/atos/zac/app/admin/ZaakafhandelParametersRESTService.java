/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import static net.atos.zac.policy.PolicyService.assertPolicy;
import static net.atos.zac.zaaksturing.model.ReferentieTabel.Systeem.AFZENDER;

import java.util.List;
import java.util.UUID;

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
import net.atos.zac.app.admin.converter.RESTReplyToConverter;
import net.atos.zac.app.admin.converter.RESTZaakafhandelParametersConverter;
import net.atos.zac.app.admin.converter.RESTZaakbeeindigRedenConverter;
import net.atos.zac.app.admin.model.RESTCaseDefinition;
import net.atos.zac.app.admin.model.RESTFormulierDefinitie;
import net.atos.zac.app.admin.model.RESTReplyTo;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.admin.model.RESTZaakbeeindigReden;
import net.atos.zac.app.zaken.converter.RESTResultaattypeConverter;
import net.atos.zac.app.zaken.model.RESTResultaattype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CMMNService;
import net.atos.zac.formulieren.FormulierDefinitieService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ReferentieTabelService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
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
    private CMMNService cmmnService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private ReferentieTabelService referentieTabelService;

    @Inject
    private RESTZaakafhandelParametersConverter zaakafhandelParametersConverter;

    @Inject
    private RESTCaseDefinitionConverter caseDefinitionConverter;

    @Inject
    private RESTResultaattypeConverter resultaattypeConverter;

    @Inject
    private RESTZaakbeeindigRedenConverter zaakbeeindigRedenConverter;

    @Inject
    private RESTReplyToConverter restReplyToConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private FormulierDefinitieService formulierDefinitieService;

    /**
     * Retrieve all CASE_DEFINITIONs that can be linked to a ZAAKTYPE
     *
     * @return LIST of CASE_DEFINITIONs
     */
    @GET
    @Path("caseDefinition")
    public List<RESTCaseDefinition> listCaseDefinitions() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        final List<CaseDefinition> caseDefinitions = cmmnService.listCaseDefinitions();
        return caseDefinitions.stream()
                .map(caseDefinition -> caseDefinitionConverter.convertToRESTCaseDefinition(caseDefinition, true))
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
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return caseDefinitionConverter.convertToRESTCaseDefinition(caseDefinitionKey, true);
    }

    /**
     * Retrieve all ZAAKAFHANDELPARAMETERS for overview
     *
     * @return LIST of ZAAKAFHANDELPARAMETERS
     */
    @GET
    public List<RESTZaakafhandelParameters> listZaakafhandelParameters() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return listZaaktypes().stream()
                .map(zaaktype -> UriUtil.uuidFromURI(zaaktype.getUrl()))
                .map(zaakafhandelParameterService::readZaakafhandelParameters)
                .map(zaakafhandelParameters -> zaakafhandelParametersConverter.convertZaakafhandelParameters(
                        zaakafhandelParameters, false))
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
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                zaakTypeUUID);
        return zaakafhandelParametersConverter.convertZaakafhandelParameters(zaakafhandelParameters, true);
    }

    /**
     * Saves the ZAAKAFHANDELPARAMETERS
     *
     * @param restZaakafhandelParameters ZAAKAFHANDELPARAMETERS
     */
    @PUT
    public RESTZaakafhandelParameters updateZaakafhandelparameters(
            final RESTZaakafhandelParameters restZaakafhandelParameters) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParametersConverter.convertRESTZaakafhandelParameters(
                restZaakafhandelParameters);
        if (zaakafhandelParameters.getId() == null) {
            zaakafhandelParameters = zaakafhandelParameterBeheerService.createZaakafhandelParameters(
                    zaakafhandelParameters);
        } else {
            zaakafhandelParameters = zaakafhandelParameterBeheerService.updateZaakafhandelParameters(
                    zaakafhandelParameters);
            zaakafhandelParameterService.cacheRemoveZaakafhandelParameters(zaakafhandelParameters.getZaakTypeUUID());
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
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return zaakbeeindigRedenConverter.convertZaakbeeindigRedenen(
                zaakafhandelParameterBeheerService.listZaakbeeindigRedenen());
    }

    /**
     * Retrieve zaakbeeindig redenen for a zaaktype
     *
     * @return list of zaakbeeindig-redenen
     */
    @GET
    @Path("zaakbeeindigRedenen/{zaaktypeUUID}")
    public List<RESTZaakbeeindigReden> listZaakbeeindigRedenenForZaaktype(
            @PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        final List<ZaakbeeindigReden> zaakbeeindigRedenen = zaakafhandelParameterService.readZaakafhandelParameters(
                        zaaktypeUUID)
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
    @Path("resultaattypes/{zaaktypeUUID}")
    public List<RESTResultaattype> listResultaattypes(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return resultaattypeConverter.convertResultaattypes(
                ztcClientService.readResultaattypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()));
    }

    private List<Zaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI());
    }

    /**
     * Retrieve all FORMULIER_DEFINITIEs that can be linked to a HUMAN_TASK_PLAN_ITEM
     *
     * @return lijst of FORMULIER_DEFINITIEs
     */
    @GET
    @Path("formulierDefinities")
    public List<RESTFormulierDefinitie> listFormulierDefinities() {

        return formulierDefinitieService.listFormulierDefinities()
                .stream()
                .map(fd -> new RESTFormulierDefinitie(fd.getId(), fd.getNaam(), fd.getSysteemnaam()))
                .toList();
    }

    /**
     * Retrieve all possible replytos
     *
     * @return sorted list of replytos
     */
    @GET
    @Path("replyTo")
    public List<RESTReplyTo> listReplyTos() {
        return restReplyToConverter.convertReplyTos(
                referentieTabelService.readReferentieTabel(AFZENDER.name()).getWaarden());
    }
}
