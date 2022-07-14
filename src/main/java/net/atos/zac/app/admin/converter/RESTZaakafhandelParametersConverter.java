/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
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
    private RESTZaakResultaattypeConverter zaakResultaattypeConverter;

    @Inject
    private RESTZaakbeeindigParameterConverter zaakbeeindigParameterConverter;

    @Inject
    private RESTUserEventListenerParametersConverter userEventListenerParametersConverter;

    @Inject
    private RESTHumanTaskParametersConverter humanTaskParametersConverter;

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakafhandelParameters convertZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters, boolean inclusiefPlanitems) {
        final RESTZaakafhandelParameters restZaakafhandelParameters = new RESTZaakafhandelParameters();
        restZaakafhandelParameters.id = zaakafhandelParameters.getId();
        restZaakafhandelParameters.zaaktype = zaaktypeConverter.convert(ztcClientService.readZaaktype(zaakafhandelParameters.getZaakTypeUUID()));
        restZaakafhandelParameters.defaultGroep = groupConverter.convertGroupId(zaakafhandelParameters.getGroepID());
        restZaakafhandelParameters.defaultBehandelaar = userConverter.convertUserId(zaakafhandelParameters.getGebruikersnaamMedewerker());
        restZaakafhandelParameters.einddatumGeplandWaarschuwing = zaakafhandelParameters.getEinddatumGeplandWaarschuwing();
        restZaakafhandelParameters.uiterlijkeEinddatumAfdoeningWaarschuwing = zaakafhandelParameters.getUiterlijkeEinddatumAfdoeningWaarschuwing();
        restZaakafhandelParameters.creatiedatum = zaakafhandelParameters.getCreatiedatum();
        restZaakafhandelParameters.valide = zaakafhandelParameters.isValide();
        if (zaakafhandelParameters.getNietOntvankelijkResultaattype() != null) {
            restZaakafhandelParameters.zaakNietOntvankelijkResultaat = zaakResultaattypeConverter.convertResultaattype(
                    ztcClientService.readResultaattype(zaakafhandelParameters.getNietOntvankelijkResultaattype()));
        }
        if (zaakafhandelParameters.getCaseDefinitionID() != null) {
            restZaakafhandelParameters.caseDefinition = caseDefinitionConverter.convertToRESTCaseDefinition(zaakafhandelParameters.getCaseDefinitionID());
        }
        if (inclusiefPlanitems && restZaakafhandelParameters.caseDefinition != null) {
            restZaakafhandelParameters.humanTaskParameters = humanTaskParametersConverter.convertHumanTaskParametersCollection(
                    zaakafhandelParameters.getHumanTaskParametersCollection(), restZaakafhandelParameters.caseDefinition.humanTaskDefinitions);
            restZaakafhandelParameters.userEventListenerParameters = userEventListenerParametersConverter.convertUserEventListenerParametersCollection(
                    zaakafhandelParameters.getUserEventListenerParametersCollection(), restZaakafhandelParameters.caseDefinition.userEventListenerDefinitions);
        }
        restZaakafhandelParameters.zaakbeeindigParameters = zaakbeeindigParameterConverter.convertZaakbeeindigParameters(
                zaakafhandelParameters.getZaakbeeindigParameters());
        return restZaakafhandelParameters;
    }

    public ZaakafhandelParameters convertRESTZaakafhandelParameters(final RESTZaakafhandelParameters restZaakafhandelParameters) {
        final ZaakafhandelParameters zaakafhandelParameters = new ZaakafhandelParameters();
        zaakafhandelParameters.setId(restZaakafhandelParameters.id);
        zaakafhandelParameters.setZaakTypeUUID(restZaakafhandelParameters.zaaktype.uuid);
        zaakafhandelParameters.setZaaktypeOmschrijving(restZaakafhandelParameters.zaaktype.omschrijving);
        zaakafhandelParameters.setCaseDefinitionID(restZaakafhandelParameters.caseDefinition.key);
        zaakafhandelParameters.setGroepID(restZaakafhandelParameters.defaultGroep.id);
        zaakafhandelParameters.setUiterlijkeEinddatumAfdoeningWaarschuwing(restZaakafhandelParameters.uiterlijkeEinddatumAfdoeningWaarschuwing);
        zaakafhandelParameters.setNietOntvankelijkResultaattype(restZaakafhandelParameters.zaakNietOntvankelijkResultaat.id);
        if (restZaakafhandelParameters.defaultBehandelaar != null) {
            zaakafhandelParameters.setGebruikersnaamMedewerker(restZaakafhandelParameters.defaultBehandelaar.id);
        }
        if (restZaakafhandelParameters.einddatumGeplandWaarschuwing != null) {
            zaakafhandelParameters.setEinddatumGeplandWaarschuwing(restZaakafhandelParameters.einddatumGeplandWaarschuwing);
        }
        zaakafhandelParameters.setHumanTaskParametersCollection(
                humanTaskParametersConverter.convertRESTHumanTaskParameters(restZaakafhandelParameters.humanTaskParameters));
        zaakafhandelParameters.setUserEventListenerParametersCollection(
                userEventListenerParametersConverter.convertRESTUserEventListenerParameters(restZaakafhandelParameters.userEventListenerParameters));
        zaakafhandelParameters.setZaakbeeindigParameters(
                zaakbeeindigParameterConverter.convertRESTZaakbeeindigParameters(restZaakafhandelParameters.zaakbeeindigParameters));
        return zaakafhandelParameters;
    }
}
