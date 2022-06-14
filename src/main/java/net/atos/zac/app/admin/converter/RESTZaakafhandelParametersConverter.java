/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.admin.model.RESTHumanTaskParameters;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.app.admin.model.RESTUserEventListenerParameter;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.UserEventListenerParameters;
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
    RESTZaakResultaattypeConverter restZaakResultaattypeConverter;

    @Inject
    private RESTZaakbeeindigParameterConverter zaakbeeindigParameterConverter;

    @Inject
    private RESTUserEventListenerParametersConverter restUserEventListenerParametersConverter;

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakafhandelParameters convert(final ZaakafhandelParameters zaakafhandelParameters, boolean inclusiefPlanitems) {
        final RESTZaakafhandelParameters restZaakafhandelParameters = new RESTZaakafhandelParameters();
        restZaakafhandelParameters.id = zaakafhandelParameters.getId();
        restZaakafhandelParameters.zaaktype = zaaktypeConverter.convert(ztcClientService.readZaaktype(zaakafhandelParameters.getZaakTypeUUID()));
        restZaakafhandelParameters.defaultGroep = groupConverter.convertGroupId(zaakafhandelParameters.getGroepID());
        restZaakafhandelParameters.defaultBehandelaar = userConverter.convertUserId(zaakafhandelParameters.getGebruikersnaamMedewerker());
        restZaakafhandelParameters.einddatumGeplandWaarschuwing = zaakafhandelParameters.getEinddatumGeplandWaarschuwing();
        restZaakafhandelParameters.uiterlijkeEinddatumAfdoeningWaarschuwing = zaakafhandelParameters.getUiterlijkeEinddatumAfdoeningWaarschuwing();
        if (zaakafhandelParameters.getNietOntvankelijkResultaattype() != null) {
            restZaakafhandelParameters.zaakNietOntvankelijkResultaat = restZaakResultaattypeConverter.convertToRest(
                    ztcClientService.readResultaattype(zaakafhandelParameters.getNietOntvankelijkResultaattype()));
        }
        restZaakafhandelParameters.caseDefinition = caseDefinitionConverter.convertToRest(zaakafhandelParameters.getCaseDefinitionID());
        if (inclusiefPlanitems && restZaakafhandelParameters.caseDefinition != null) {
            restZaakafhandelParameters.humanTaskParameters = convertToHumanTaskParameters(restZaakafhandelParameters.caseDefinition.humanTaskDefinitions,
                                                                                          zaakafhandelParameters.getHumanTaskParameters());
            restZaakafhandelParameters.userEventListenerParameters = convertToUserEventListenerParameeters(
                    restZaakafhandelParameters.caseDefinition.userEventListenerDefinitions, zaakafhandelParameters.getUserEventListenerParameters());
        }
        restZaakafhandelParameters.zaakbeeindigParameters = zaakbeeindigParameterConverter.convertToRest(zaakafhandelParameters.getZaakbeeindigParameters());
        return restZaakafhandelParameters;
    }

    private List<RESTHumanTaskParameters> convertToHumanTaskParameters(final List<RESTPlanItemDefinition> humanTaskDefinitions,
            final Collection<HumanTaskParameters> humanTaskParametersCollection) {
        final List<RESTHumanTaskParameters> restHumanTaskParametersList = new ArrayList<>();
        //remove non-existent plan-item-definition params and add new plan-item-definition
        for (final RESTPlanItemDefinition humanTaskDefinition : humanTaskDefinitions) {
            boolean found = false;
            final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
            for (final HumanTaskParameters humanTaskParameters : humanTaskParametersCollection) {
                if (humanTaskParameters.getPlanItemDefinitionID().equals(humanTaskDefinition.id)) {
                    restHumanTaskParameters.id = humanTaskParameters.getId();
                    restHumanTaskParameters.defaultGroep = groupConverter.convertGroupId(humanTaskParameters.getGroepID());
                    restHumanTaskParameters.formulierDefinitie = FormulierDefinitie.valueOf(humanTaskParameters.getFormulierDefinitieID());
                    restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
                    restHumanTaskParameters.doorlooptijd = humanTaskParameters.getDoorlooptijd();
                    found = true;
                    break;
                }
            }
            if (!found) {
                restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
            }
        }
        return restHumanTaskParametersList;
    }

    private List<RESTUserEventListenerParameter> convertToUserEventListenerParameeters(final List<RESTPlanItemDefinition> userEventListenerDefinitions,
            final Collection<UserEventListenerParameters> userEventListenerParametersCollection) {
        final List<RESTUserEventListenerParameter> restUserEventListenerParameters = new ArrayList<>();
        userEventListenerDefinitions.forEach(userEventListenerDefinition -> {
            final RESTUserEventListenerParameter restUserEventListenerParameter = new RESTUserEventListenerParameter();
            restUserEventListenerParameter.id = userEventListenerDefinition.id;
            restUserEventListenerParameter.naam = userEventListenerDefinition.naam;
            userEventListenerParametersCollection.stream()
                    .filter(userEventListenerParameters -> userEventListenerParameters.getPlanItemDefinitionID().equals(userEventListenerDefinition.id))
                    .forEach(userEventListenerParameters -> restUserEventListenerParameter.toelichting = userEventListenerParameters.getToelichting());
            restUserEventListenerParameters.add(restUserEventListenerParameter);
        });
        return restUserEventListenerParameters;
    }

    public ZaakafhandelParameters convert(final RESTZaakafhandelParameters restParameters) {
        final ZaakafhandelParameters parameters = new ZaakafhandelParameters();
        parameters.setId(restParameters.id);
        parameters.setZaakTypeUUID(UUID.fromString(restParameters.zaaktype.uuid));
        final Zaaktype zaaktype = ztcClientService.readZaaktype(parameters.getZaakTypeUUID());
        parameters.setCaseDefinitionID(restParameters.caseDefinition.key);
        if (restParameters.defaultBehandelaar != null) {
            parameters.setGebruikersnaamMedewerker(restParameters.defaultBehandelaar.id);
        }
        parameters.setGroepID(restParameters.defaultGroep.id);
        if (zaaktype.isServicenormBeschikbaar()) {
            parameters.setEinddatumGeplandWaarschuwing(restParameters.einddatumGeplandWaarschuwing);
        }
        parameters.setUiterlijkeEinddatumAfdoeningWaarschuwing(restParameters.uiterlijkeEinddatumAfdoeningWaarschuwing);
        parameters.setNietOntvankelijkResultaattype(restParameters.zaakNietOntvankelijkResultaat.id);
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
        parameters.setHumanTaskParameters(list);
        parameters.setZaakbeeindigParameters(zaakbeeindigParameterConverter.convertToDomain(restParameters.zaakbeeindigParameters));
        parameters.setUserEventListenerParameters(restUserEventListenerParametersConverter
                                                          .convertToDomain(restParameters.userEventListenerParameters));
        return parameters;
    }
}
