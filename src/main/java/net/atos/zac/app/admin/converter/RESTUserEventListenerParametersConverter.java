/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import net.atos.zac.app.admin.model.RESTUserEventListenerParameter;
import net.atos.zac.zaaksturing.model.UserEventListenerParameters;

import java.util.Collection;
import java.util.List;

public class RESTUserEventListenerParametersConverter {

    public RESTUserEventListenerParameter convertToRest(final UserEventListenerParameters userEventListenerParameters) {
        final RESTUserEventListenerParameter restUserEventListenerParameter = new RESTUserEventListenerParameter();
        restUserEventListenerParameter.id = userEventListenerParameters.getPlanItemDefinitionID();
        restUserEventListenerParameter.toelichting = userEventListenerParameters.getToelichting();
        return restUserEventListenerParameter;
    }

    public List<RESTUserEventListenerParameter> convertToRest(final Collection<UserEventListenerParameters> userEventListenerParameters) {
        return userEventListenerParameters.stream()
                .map(this::convertToRest)
                .toList();
    }

    public UserEventListenerParameters convertToDomain(final RESTUserEventListenerParameter restUserEventListenerParameter) {
        final UserEventListenerParameters userEventListenerParameters = new UserEventListenerParameters();
        userEventListenerParameters.setPlanItemDefinitionID(restUserEventListenerParameter.id);
        userEventListenerParameters.setToelichting(restUserEventListenerParameter.toelichting);
        return userEventListenerParameters;
    }

    public List<UserEventListenerParameters> convertToDomain(Collection<RESTUserEventListenerParameter> restUserEventListenerParameters) {
        return restUserEventListenerParameters.stream()
                .map(this::convertToDomain)
                .toList();
    }
}
