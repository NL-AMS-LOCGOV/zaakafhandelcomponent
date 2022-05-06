/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.admin.model.RESTHumanTaskParameters;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.app.admin.model.RESTUserEventListenerParameter;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

public class RESTZaakafhandelParametersConverter {

    @Inject
    private RESTCaseDefinitionConverter caseDefinitionConverter;

    @Inject
    private RESTGroupConverter groupConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    @Inject
    private RESTZaakbeeindigParameterConverter zaakbeeindigParameterConverter;

    @Inject
    private RESTUserEventListenerParametersConverter restUserEventListenerParametersConverter;

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakafhandelParameters convert(final ZaakafhandelParameters parameters, boolean inclusiefPlanitems) {
        final RESTZaakafhandelParameters restParameters = new RESTZaakafhandelParameters();
        restParameters.id = parameters.getId();
        restParameters.zaaktype = zaaktypeConverter.convert(ztcClientService.readZaaktype(parameters.getZaakTypeUUID()));
        restParameters.defaultGroep = groupConverter.convertGroupId(parameters.getGroepID());
        restParameters.defaultBehandelaar = userConverter.convertUserId(parameters.getGebruikersnaamMedewerker());
        restParameters.caseDefinition = caseDefinitionConverter.convertToRest(parameters.getCaseDefinitionID());
        if (inclusiefPlanitems && restParameters.caseDefinition != null) {
            final List<RESTHumanTaskParameters> list = new ArrayList<>();
            //remove non-existent plan-item-definition params and add new plan-item-definition
            for (RESTPlanItemDefinition pDef : restParameters.caseDefinition.planItemDefinitions) {
                boolean found = false;
                final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
                for (HumanTaskParameters htParam : parameters.getHumanTaskParametersCollection()) {
                    if (htParam.getPlanItemDefinitionID().equals(pDef.id)) {
                        found = true;
                        restHumanTaskParameters.id = htParam.getId();
                        restHumanTaskParameters.defaultGroep = groupConverter.convertGroupId(htParam.getGroepID());
                        restHumanTaskParameters.formulierDefinitie = FormulierDefinitie.valueOf(htParam.getFormulierDefinitieID());
                        restHumanTaskParameters.planItemDefinition = pDef;
                        restHumanTaskParameters.doorlooptijd = htParam.getDoorlooptijd();
                        break;
                    }
                }
                if (!found) {
                    restHumanTaskParameters.planItemDefinition = pDef;
                }
                list.add(restHumanTaskParameters);
            }
            restParameters.humanTaskParameters = list;

            final List<RESTUserEventListenerParameter> actieParameters = new ArrayList<>();
            restParameters.caseDefinition.userEventListenerDefinitions.forEach(planItem -> {
                final RESTUserEventListenerParameter restUserEventListenerParameter = new RESTUserEventListenerParameter();
                restUserEventListenerParameter.id = planItem.id;
                restUserEventListenerParameter.naam = planItem.naam;
                parameters.getUserEventListenerParameters().stream()
                        .filter(uelParameter -> uelParameter.getPlanItemDefinitionID().equals(planItem.id)).forEach(
                                uelParameter -> restUserEventListenerParameter.toelichting = uelParameter.getToelichting());
                actieParameters.add(restUserEventListenerParameter);
            });
            restParameters.userEventListenerParameters = actieParameters;
        }
        restParameters.zaakbeeindigParameters = zaakbeeindigParameterConverter.convertToRest(parameters.getZaakbeeindigParameters());
        return restParameters;
    }

    public ZaakafhandelParameters convert(final RESTZaakafhandelParameters restParameters) {
        final ZaakafhandelParameters parameters = new ZaakafhandelParameters();
        parameters.setId(restParameters.id);
        parameters.setZaakTypeUUID(UUID.fromString(restParameters.zaaktype.uuid));
        parameters.setCaseDefinitionID(restParameters.caseDefinition.key);
        if (restParameters.defaultBehandelaar != null) {
            parameters.setGebruikersnaamMedewerker(restParameters.defaultBehandelaar.id);
        }
        parameters.setGroepID(restParameters.defaultGroep.id);
        final List<HumanTaskParameters> list = new ArrayList<>();
        restParameters.humanTaskParameters.forEach(restHumanTaskParameters -> {
            HumanTaskParameters humanTaskParameters = new HumanTaskParameters();
            humanTaskParameters.setId(restHumanTaskParameters.id);
            humanTaskParameters.setZaakafhandelParameters(parameters);
            humanTaskParameters.setDoorlooptijd(restHumanTaskParameters.doorlooptijd);
            humanTaskParameters.setPlanItemDefinitionID(restHumanTaskParameters.planItemDefinition.id);
            humanTaskParameters.setFormulierDefinitieID(restHumanTaskParameters.formulierDefinitie.toString());
            if (restHumanTaskParameters.defaultGroep != null) {
                humanTaskParameters.setGroepID(restHumanTaskParameters.defaultGroep.id);
            }
            list.add(humanTaskParameters);
        });
        parameters.setHumanTaskParametersCollection(list);
        parameters.setZaakbeeindigParameters(zaakbeeindigParameterConverter.convertToDomain(restParameters.zaakbeeindigParameters));
        parameters.setUserEventListenerParameters(restUserEventListenerParametersConverter
                                                          .convertToDomain(restParameters.userEventListenerParameters));
        return parameters;
    }
}
