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
import net.atos.zac.app.zaken.converter.RESTResultaattypeConverter;
import net.atos.zac.app.zaken.model.RESTZaakStatusmailOptie;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

public class RESTZaakafhandelParametersConverter {

    @Inject
    private RESTCaseDefinitionConverter caseDefinitionConverter;

    @Inject
    private RESTGroupConverter groupConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private RESTZaaktypeOverzichtConverter restZaaktypeOverzichtConverter;

    @Inject
    private RESTResultaattypeConverter resultaattypeConverter;

    @Inject
    private RESTZaakbeeindigParameterConverter zaakbeeindigParameterConverter;

    @Inject
    private RESTUserEventListenerParametersConverter userEventListenerParametersConverter;

    @Inject
    private RESTHumanTaskParametersConverter humanTaskParametersConverter;

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakafhandelParameters convertZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters, final boolean inclusiefRelaties) {
        final RESTZaakafhandelParameters restZaakafhandelParameters = new RESTZaakafhandelParameters();
        restZaakafhandelParameters.id = zaakafhandelParameters.getId();
        restZaakafhandelParameters.zaaktype = restZaaktypeOverzichtConverter.convert(ztcClientService.readZaaktype(zaakafhandelParameters.getZaakTypeUUID()));
        restZaakafhandelParameters.defaultGroepId = zaakafhandelParameters.getGroepID();
        restZaakafhandelParameters.defaultBehandelaarId = zaakafhandelParameters.getGebruikersnaamMedewerker();
        restZaakafhandelParameters.einddatumGeplandWaarschuwing = zaakafhandelParameters.getEinddatumGeplandWaarschuwing();
        restZaakafhandelParameters.uiterlijkeEinddatumAfdoeningWaarschuwing = zaakafhandelParameters.getUiterlijkeEinddatumAfdoeningWaarschuwing();
        restZaakafhandelParameters.creatiedatum = zaakafhandelParameters.getCreatiedatum();
        restZaakafhandelParameters.valide = zaakafhandelParameters.isValide();

        if (zaakafhandelParameters.getCaseDefinitionID() != null) {
            restZaakafhandelParameters.caseDefinition =
                    caseDefinitionConverter.convertToRESTCaseDefinition(zaakafhandelParameters.getCaseDefinitionID(), inclusiefRelaties);
        }
        if (inclusiefRelaties && restZaakafhandelParameters.caseDefinition != null) {
            if (zaakafhandelParameters.getNietOntvankelijkResultaattype() != null) {
                restZaakafhandelParameters.zaakNietOntvankelijkResultaattype = resultaattypeConverter.convertResultaattype(
                        ztcClientService.readResultaattype(zaakafhandelParameters.getNietOntvankelijkResultaattype()));
            }
            restZaakafhandelParameters.humanTaskParameters = humanTaskParametersConverter.convertHumanTaskParametersCollection(
                    zaakafhandelParameters.getHumanTaskParametersCollection(), restZaakafhandelParameters.caseDefinition.humanTaskDefinitions);
            restZaakafhandelParameters.userEventListenerParameters = userEventListenerParametersConverter.convertUserEventListenerParametersCollection(
                    zaakafhandelParameters.getUserEventListenerParametersCollection(), restZaakafhandelParameters.caseDefinition.userEventListenerDefinitions);
            restZaakafhandelParameters.zaakbeeindigParameters = zaakbeeindigParameterConverter.convertZaakbeeindigParameters(
                    zaakafhandelParameters.getZaakbeeindigParameters());
        }
        if (zaakafhandelParameters.getIntakeMail() != null) {
            restZaakafhandelParameters.intakeMail = RESTZaakStatusmailOptie.valueOf(zaakafhandelParameters.getIntakeMail());
        }
        if(zaakafhandelParameters.getAfrondenMail() != null) {
            restZaakafhandelParameters.afrondenMail = RESTZaakStatusmailOptie.valueOf(zaakafhandelParameters.getAfrondenMail());
        }
        restZaakafhandelParameters.productaanvraagtype = zaakafhandelParameters.getProductaanvraagtype();

        return restZaakafhandelParameters;
    }

    public ZaakafhandelParameters convertRESTZaakafhandelParameters(final RESTZaakafhandelParameters restZaakafhandelParameters) {
        final ZaakafhandelParameters zaakafhandelParameters = new ZaakafhandelParameters();
        zaakafhandelParameters.setId(restZaakafhandelParameters.id);
        zaakafhandelParameters.setZaakTypeUUID(restZaakafhandelParameters.zaaktype.uuid);
        zaakafhandelParameters.setZaaktypeOmschrijving(restZaakafhandelParameters.zaaktype.omschrijving);
        zaakafhandelParameters.setCaseDefinitionID(restZaakafhandelParameters.caseDefinition.key);
        zaakafhandelParameters.setGroepID(restZaakafhandelParameters.defaultGroepId);
        zaakafhandelParameters.setUiterlijkeEinddatumAfdoeningWaarschuwing(restZaakafhandelParameters.uiterlijkeEinddatumAfdoeningWaarschuwing);
        zaakafhandelParameters.setNietOntvankelijkResultaattype(restZaakafhandelParameters.zaakNietOntvankelijkResultaattype.id);
        zaakafhandelParameters.setIntakeMail(restZaakafhandelParameters.intakeMail.name());
        zaakafhandelParameters.setAfrondenMail(restZaakafhandelParameters.afrondenMail.name());
        zaakafhandelParameters.setProductaanvraagtype(restZaakafhandelParameters.productaanvraagtype);
        zaakafhandelParameters.setGebruikersnaamMedewerker(restZaakafhandelParameters.defaultBehandelaarId);
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
