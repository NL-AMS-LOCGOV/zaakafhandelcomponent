/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import net.atos.zac.app.admin.model.RESTHumanTaskParameters;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;

public class RESTHumanTaskParametersConverter {

    @Inject
    private RESTGroupConverter groupConverter;

    public List<RESTHumanTaskParameters> convertHumanTaskParametersCollection(final Collection<HumanTaskParameters> humanTaskParametersCollection,
            final List<RESTPlanItemDefinition> humanTaskDefinitions) {
        return humanTaskDefinitions.stream()
                .map(humanTaskDefinition -> convertHumanTaskDefinition(humanTaskDefinition, humanTaskParametersCollection))
                .toList();
    }

    private RESTHumanTaskParameters convertHumanTaskDefinition(final RESTPlanItemDefinition humanTaskDefinition,
            final Collection<HumanTaskParameters> humanTaskParametersCollection) {
        return humanTaskParametersCollection.stream()
                .filter(humanTaskParameters -> humanTaskParameters.getPlanItemDefinitionID().equals(humanTaskDefinition.id))
                .findAny().map(humanTaskParameters -> convertToRESTHumanTaskParameters(humanTaskParameters, humanTaskDefinition))
                .orElse(convertToRESTHumanTaskParameters(humanTaskDefinition));
    }

    private RESTHumanTaskParameters convertToRESTHumanTaskParameters(final HumanTaskParameters humanTaskParameters,
            final RESTPlanItemDefinition humanTaskDefinition) {
        final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
        restHumanTaskParameters.id = humanTaskParameters.getId();
        restHumanTaskParameters.defaultGroep = groupConverter.convertGroupId(humanTaskParameters.getGroepID());
        restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
        restHumanTaskParameters.doorlooptijd = humanTaskParameters.getDoorlooptijd();
        return restHumanTaskParameters;
    }

    private RESTHumanTaskParameters convertToRESTHumanTaskParameters(final RESTPlanItemDefinition humanTaskDefinition) {
        final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
        restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
        return restHumanTaskParameters;
    }

    public List<HumanTaskParameters> convertRESTHumanTaskParameters(final List<RESTHumanTaskParameters> restHumanTaskParametersList) {
        return restHumanTaskParametersList.stream()
                .map(this::convertRESTHumanTaskParameters)
                .toList();
    }

    private HumanTaskParameters convertRESTHumanTaskParameters(final RESTHumanTaskParameters restHumanTaskParameters) {
        HumanTaskParameters humanTaskParameters = new HumanTaskParameters();
        humanTaskParameters.setId(restHumanTaskParameters.id);
        humanTaskParameters.setDoorlooptijd(restHumanTaskParameters.doorlooptijd);
        humanTaskParameters.setPlanItemDefinitionID(restHumanTaskParameters.planItemDefinition.id);
        if (restHumanTaskParameters.defaultGroep != null) {
            humanTaskParameters.setGroepID(restHumanTaskParameters.defaultGroep.id);
        }
        return humanTaskParameters;
    }
}
