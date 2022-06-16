/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;
import java.util.Set;

import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.app.admin.model.RESTUserEventListenerParameter;
import net.atos.zac.zaaksturing.model.UserEventListenerParameters;

public class RESTUserEventListenerParametersConverter {

    public List<RESTUserEventListenerParameter> convertUserEventListenerParametersCollection(
            final Set<UserEventListenerParameters> userEventListenerParametersCollection, final List<RESTPlanItemDefinition> userEventListenerDefinitions) {
        return userEventListenerDefinitions.stream()
                .map(userEventListenerDefinition -> convertUserEventListenerDefinition(userEventListenerDefinition, userEventListenerParametersCollection))
                .toList();
    }

    private RESTUserEventListenerParameter convertUserEventListenerDefinition(final RESTPlanItemDefinition userEventListenerDefinition,
            final Set<UserEventListenerParameters> userEventListenerParametersCollection) {
        final RESTUserEventListenerParameter restUserEventListenerParameter = new RESTUserEventListenerParameter();
        restUserEventListenerParameter.id = userEventListenerDefinition.id;
        restUserEventListenerParameter.naam = userEventListenerDefinition.naam;
        userEventListenerParametersCollection.stream()
                .filter(userEventListenerParameters -> userEventListenerParameters.getPlanItemDefinitionID().equals(userEventListenerDefinition.id))
                .findAny()
                .ifPresent(userEventListenerParameters -> restUserEventListenerParameter.toelichting = userEventListenerParameters.getToelichting());
        return restUserEventListenerParameter;
    }

    public List<UserEventListenerParameters> convertRESTUserEventListenerParameters(
            final List<RESTUserEventListenerParameter> restUserEventListenerParameters) {
        return restUserEventListenerParameters.stream()
                .map(this::convertRESTUserEventListenerParameter)
                .toList();
    }

    private UserEventListenerParameters convertRESTUserEventListenerParameter(final RESTUserEventListenerParameter restUserEventListenerParameter) {
        final UserEventListenerParameters userEventListenerParameters = new UserEventListenerParameters();
        userEventListenerParameters.setPlanItemDefinitionID(restUserEventListenerParameter.id);
        userEventListenerParameters.setToelichting(restUserEventListenerParameter.toelichting);
        return userEventListenerParameters;
    }
}
