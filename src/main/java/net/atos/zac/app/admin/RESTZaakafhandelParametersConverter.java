/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.admin.model.RESTFormulierDefinition;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.app.admin.model.RESTPlanItemParameters;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.zaaksturing.model.FormulierDefinition;
import net.atos.zac.zaaksturing.model.PlanItemParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

public class RESTZaakafhandelParametersConverter {

    @Inject
    private RESTCaseDefinitionConverter caseDefinitionConverter;

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakafhandelParameters convert(final ZaakafhandelParameters parameters, boolean inclusiefPlanitems) {
        final RESTZaakafhandelParameters restParameters = new RESTZaakafhandelParameters();
        restParameters.id = parameters.getId();
        restParameters.zaaktype = zaaktypeConverter.convert(ztcClientService.readZaaktype(parameters.getZaakTypeUUID()));
        restParameters.defaultGroep = groepConverter.convertGroupId(parameters.getGroepID());
        restParameters.defaultBehandelaar = medewerkerConverter.convertGebruikersnaam(parameters.getGebruikersnaamMedewerker());
        restParameters.caseDefinition = caseDefinitionConverter.convertToRest(parameters.getCaseDefinitionID());
        if (inclusiefPlanitems && restParameters.caseDefinition != null) {
            final List<RESTPlanItemParameters> list = new ArrayList<>();
            //remove non-existent plan-item-definition params and add new plan-item-definition
            for (RESTPlanItemDefinition pDef : restParameters.caseDefinition.planItemDefinitions) {
                boolean found = false;
                final RESTPlanItemParameters restPlanParameters = new RESTPlanItemParameters();
                for (PlanItemParameters pParam : parameters.getPlanItemParametersCollection()) {
                    if (pParam.getPlanItemDefinitionID().equals(pDef.id)) {
                        found = true;
                        restPlanParameters.id = pParam.getId();
                        restPlanParameters.defaultGroep = groepConverter.convertGroupId(pParam.getGroepID());
                        restPlanParameters.formulierDefinitie = new RESTFormulierDefinition(FormulierDefinition.valueOf(pParam.getFormulierDefinitionID()));
                        restPlanParameters.planItemDefinition = pDef;
                        restPlanParameters.doorlooptijd = pParam.getDoorlooptijd();
                        break;
                    }
                }
                if (!found) {
                    restPlanParameters.planItemDefinition = pDef;
                }
                list.add(restPlanParameters);
            }
            restParameters.planItemParameters = list;
        }
        return restParameters;
    }

    public ZaakafhandelParameters convert(final RESTZaakafhandelParameters restParameters) {
        final ZaakafhandelParameters parameters = new ZaakafhandelParameters();
        parameters.setId(restParameters.id);
        parameters.setZaakTypeUUID(UUID.fromString(restParameters.zaaktype.uuid));
        parameters.setCaseDefinitionID(restParameters.caseDefinition.key);
        if (restParameters.defaultBehandelaar != null) {
            parameters.setGebruikersnaamMedewerker(restParameters.defaultBehandelaar.gebruikersnaam);
        }
        parameters.setGroepID(restParameters.defaultGroep.id);
        final List<PlanItemParameters> list = new ArrayList<>();
        restParameters.planItemParameters.forEach(restPlanItemParameters -> {
            PlanItemParameters planItemParameters = new PlanItemParameters();
            planItemParameters.setId(restPlanItemParameters.id);
            planItemParameters.setZaakafhandelParameters(parameters);
            planItemParameters.setDoorlooptijd(restPlanItemParameters.doorlooptijd);
            planItemParameters.setPlanItemDefinitionID(restPlanItemParameters.planItemDefinition.id);
            planItemParameters.setFormulierDefinitionID(restPlanItemParameters.formulierDefinitie.id);
            if (restPlanItemParameters.defaultGroep != null) {
                planItemParameters.setGroepID(restPlanItemParameters.defaultGroep.id);
            }
            list.add(planItemParameters);
        });
        parameters.setPlanItemParametersCollection(list);
        return parameters;
    }
}
