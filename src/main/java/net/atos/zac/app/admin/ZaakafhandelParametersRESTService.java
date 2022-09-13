/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import static net.atos.zac.policy.PolicyService.assertActie;

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
import net.atos.zac.app.admin.converter.RESTReferentieWaardeConverter;
import net.atos.zac.app.admin.converter.RESTZaakafhandelParametersConverter;
import net.atos.zac.app.admin.converter.RESTZaakbeeindigRedenConverter;
import net.atos.zac.app.admin.model.RESTCaseDefinition;
import net.atos.zac.app.admin.model.RESTReferentieTabelWaarde;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.admin.model.RESTZaakbeeindigReden;
import net.atos.zac.app.planitems.model.HumanTaskFormulierKoppeling;
import net.atos.zac.app.zaken.converter.RESTResultaattypeConverter;
import net.atos.zac.app.zaken.model.RESTResultaattype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CaseService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.FormulierVeldDefinitie;
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
    private RESTResultaattypeConverter resultaattypeConverter;

    @Inject
    private RESTZaakbeeindigRedenConverter zaakbeeindigRedenConverter;

    @Inject
    private RESTReferentieWaardeConverter restReferentieWaardeConverter;

    @Inject
    private PolicyService policyService;

    /**
     * Retrieve all CASE_DEFINITIONs that can be linked to a ZAAKTYPE
     *
     * @return LIST of CASE_DEFINITIONs
     */
    @GET
    @Path("caseDefinition")
    public List<RESTCaseDefinition> listCaseDefinitions() {
        assertActie(policyService.readAppActies().getBeheren());
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
        assertActie(policyService.readAppActies().getBeheren());
        return caseDefinitionConverter.convertToRESTCaseDefinition(caseDefinitionKey);
    }

    /**
     * Retrieve all ZAAKAFHANDELPARAMETERS for overview
     *
     * @return LIST of ZAAKAFHANDELPARAMETERS
     */
    @GET
    public List<RESTZaakafhandelParameters> listZaakafhandelParameters() {
        assertActie(policyService.readAppActies().getBeheren());
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
        assertActie(policyService.readAppActies().getBeheren());
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaakTypeUUID);
        return zaakafhandelParametersConverter.convertZaakafhandelParameters(zaakafhandelParameters, true);
    }

    /**
     * Retrieve the referentietabelwaarden for a VELD of a FORMULIER for a ZAAKTYPE
     *
     * @return list of values or empty list if not found
     */
    @GET
    @Path("{zaaktypeUUID}/{formulierDefinitie}/{veldDefinitie}")
    public List<RESTReferentieTabelWaarde> findZaakafhandelParametersReferentieTabelWaarden(
            @PathParam("zaaktypeUUID") final UUID zaakTypeUUID,
            @PathParam("formulierDefinitie") final FormulierDefinitie formulierDefinitie,
            @PathParam("veldDefinitie") final FormulierVeldDefinitie veldDefinitie) {
        assertActie(policyService.readAppActies().getTaken());
        return zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaakTypeUUID).getHumanTaskParametersCollection().stream()
                .filter(parameters -> HumanTaskFormulierKoppeling.readFormulierDefinitie(parameters.getPlanItemDefinitionID()) == formulierDefinitie)
                .flatMap(parameters -> parameters.getReferentieTabellen().stream())
                .filter(humanTaskReferentieTabel -> humanTaskReferentieTabel.getVeld().equals(veldDefinitie.name()))
                .flatMap(humanTaskReferentieTabel -> humanTaskReferentieTabel.getTabel().getWaarden().stream())
                .map(restReferentieWaardeConverter::convert)
                .toList();
    }

    /**
     * Saves the ZAAKAFHANDELPARAMETERS
     *
     * @param restZaakafhandelParameters ZAAKAFHANDELPARAMETERS
     */
    @PUT
    public RESTZaakafhandelParameters updateZaakafhandelparameters(final RESTZaakafhandelParameters restZaakafhandelParameters) {
        assertActie(policyService.readAppActies().getBeheren());
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
        assertActie(policyService.readAppActies().getBeheren());
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
        AppActies appActies = policyService.readAppActies();
        assertActie(appActies.getBeheren() || appActies.getZaken());
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
    @Path("resultaattypes/{zaaktypeUUID}")
    public List<RESTResultaattype> listResultaattypes(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        assertActie(policyService.readAppActies().getBeheren());
        return resultaattypeConverter.convertResultaattypes(ztcClientService.readResultaattypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()));
    }

    private List<Zaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI());
    }
}
