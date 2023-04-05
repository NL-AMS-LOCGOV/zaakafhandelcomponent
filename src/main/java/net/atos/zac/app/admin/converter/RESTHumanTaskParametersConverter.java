/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import net.atos.zac.app.admin.model.RESTHumanTaskParameters;
import net.atos.zac.app.admin.model.RESTHumanTaskReferentieTabel;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.app.planitems.model.DefaultHumanTaskFormulierKoppeling;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;

public class RESTHumanTaskParametersConverter {

    @Inject
    private RESTHumanTaskReferentieTabelConverter restHumanTaskReferentieTabelConverter;

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
        restHumanTaskParameters.formulierDefinitieId = humanTaskParameters.getFormulierDefinitieID();
        restHumanTaskParameters.doorlooptijd = humanTaskParameters.getDoorlooptijd();
        restHumanTaskParameters.referentieTabellen = convertReferentieTabellen(humanTaskParameters,
                                                                               humanTaskDefinition);
        return restHumanTaskParameters;
    }

    private List<RESTHumanTaskReferentieTabel> convertReferentieTabellen(final HumanTaskParameters humanTaskParameters,
            final RESTPlanItemDefinition humanTaskDefinition) {
        final List<RESTHumanTaskReferentieTabel> referentieTabellen = restHumanTaskReferentieTabelConverter.convert(
                humanTaskParameters.getReferentieTabellen());
        DefaultHumanTaskFormulierKoppeling.readFormulierVeldDefinities(humanTaskDefinition.id).stream()
                .filter(veldDefinitie -> referentieTabellen.stream()
                        .noneMatch(referentieTabel -> veldDefinitie.name().equals(referentieTabel.veld)))
                .map(restHumanTaskReferentieTabelConverter::convertDefault)
                .forEach(referentieTabellen::add);
        return referentieTabellen;
    }

    private RESTHumanTaskParameters convertToRESTHumanTaskParameters(final RESTPlanItemDefinition humanTaskDefinition) {
        final RESTHumanTaskParameters restHumanTaskParameters = new RESTHumanTaskParameters();
        restHumanTaskParameters.planItemDefinition = humanTaskDefinition;
        restHumanTaskParameters.actief = false;
        restHumanTaskParameters.formulierDefinitieId = DefaultHumanTaskFormulierKoppeling.readFormulierDefinitie(
                humanTaskDefinition.id).name();
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
        humanTaskParameters.setFormulierDefinitieID(restHumanTaskParameters.formulierDefinitieId);
        humanTaskParameters.setReferentieTabellen(
                restHumanTaskReferentieTabelConverter.convert(restHumanTaskParameters.referentieTabellen));
        return humanTaskParameters;
    }
}
