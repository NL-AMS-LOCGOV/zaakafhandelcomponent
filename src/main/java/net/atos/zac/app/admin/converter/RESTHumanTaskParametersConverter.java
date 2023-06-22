/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.Collection;
import java.util.List;

import net.atos.zac.app.admin.model.RESTHumanTaskParameters;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;

public class RESTHumanTaskParametersConverter {

    public List<RESTHumanTaskParameters> convertHumanTaskParametersCollection(
            final Collection<HumanTaskParameters> humanTaskParametersCollection,
            final List<RESTPlanItemDefinition> humanTaskDefinitions) {
        return humanTaskDefinitions.stream()
                .map(humanTaskDefinition -> convertHumanTaskDefinition(humanTaskDefinition,
                                                                       humanTaskParametersCollection))
                .toList();
    }

    private RESTHumanTaskParameters convertHumanTaskDefinition(final RESTPlanItemDefinition humanTaskDefinition,
            final Collection<HumanTaskParameters> humanTaskParametersCollection) {
        return humanTaskParametersCollection.stream()
                .filter(humanTaskParameters -> humanTaskParameters.getPlanItemDefinitionID()
                        .equals(humanTaskDefinition.id))
                .findAny()
                .map(humanTaskParameters -> convertToRESTHumanTaskParameters(humanTaskParameters, humanTaskDefinition))
                .orElseGet(() -> convertToRESTHumanTaskParameters(humanTaskDefinition));
    }

    private RESTHumanTaskParameters convertToRESTHumanTaskParameters(final HumanTaskParameters humanTaskParameters,
            final RESTPlanItemDefinition humanTaskDefinition) {
        final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
        restHumanTaskParameters.id = humanTaskParameters.getId();
        restHumanTaskParameters.actief = humanTaskParameters.isActief();
        restHumanTaskParameters.defaultGroepId = humanTaskParameters.getGroepID();
        restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
        restHumanTaskParameters.startformulierDefinitieId = humanTaskParameters.getStartformulierDefinitieID();
        restHumanTaskParameters.afhandelformulierDefinitieId = humanTaskParameters.getAfhandelformulierDefinitieID();
        restHumanTaskParameters.doorlooptijd = humanTaskParameters.getDoorlooptijd();
        return restHumanTaskParameters;
    }

    private RESTHumanTaskParameters convertToRESTHumanTaskParameters(final RESTPlanItemDefinition humanTaskDefinition) {
        final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
        restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
        restHumanTaskParameters.actief = false;
        return restHumanTaskParameters;
    }

    public List<HumanTaskParameters> convertRESTHumanTaskParameters(
            final List<RESTHumanTaskParameters> restHumanTaskParametersList) {
        return restHumanTaskParametersList.stream()
                .map(this::convertRESTHumanTaskParameters)
                .toList();
    }

    private HumanTaskParameters convertRESTHumanTaskParameters(final RESTHumanTaskParameters restHumanTaskParameters) {
        HumanTaskParameters humanTaskParameters = new HumanTaskParameters();
        humanTaskParameters.setId(restHumanTaskParameters.id);
        humanTaskParameters.setActief(restHumanTaskParameters.actief);
        humanTaskParameters.setDoorlooptijd(restHumanTaskParameters.doorlooptijd);
        humanTaskParameters.setPlanItemDefinitionID(restHumanTaskParameters.planItemDefinition.id);
        humanTaskParameters.setGroepID(restHumanTaskParameters.defaultGroepId);
        humanTaskParameters.setStartformulierDefinitieID(restHumanTaskParameters.startformulierDefinitieId);
        humanTaskParameters.setAfhandelformulierDefinitieID(restHumanTaskParameters.afhandelformulierDefinitieId);
        return humanTaskParameters;
    }
}
