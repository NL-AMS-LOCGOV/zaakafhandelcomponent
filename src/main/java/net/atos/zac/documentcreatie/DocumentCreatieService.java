/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie;

import static java.lang.String.format;
import static net.atos.zac.configuratie.ConfiguratieService.BRON_ORGANISATIE;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.sd.SmartDocumentsClient;
import net.atos.client.sd.model.SmartDocument;
import net.atos.client.sd.model.WizardResponse;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.documentcreatie.converter.DataConverter;
import net.atos.zac.documentcreatie.model.Data;
import net.atos.zac.documentcreatie.model.DocumentCreatieGegevens;
import net.atos.zac.documentcreatie.model.Registratie;
import net.atos.zac.documentcreatie.model.WizardRequest;

@ApplicationScoped
public class DocumentCreatieService {

    @Inject
    @RestClient
    private SmartDocumentsClient smartDocumentsClient;

    @Inject
    @ConfigProperty(name = "SD_CLIENT_MP_REST_URL")
    private String smartDocumentsURL;

    @Inject
    @ConfigProperty(name = "SD_AUTHENTICATION")
    private String authenticationHeader;

    @Inject
    private DataConverter dataConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private ZTCClientService ztcClientService;

    /**
     * Creeer een document met de wizard van SmartDocuments.
     *
     * @param documentCreatieGegevens Gegevens op basis van welke het document wordt gecreeerd.
     * @return De redirect URL naar de SmartDocuments Wizard
     */
    public URI creeerDocumentAttendedSD(final DocumentCreatieGegevens documentCreatieGegevens) {
        final LoggedInUser loggedInUser = loggedInUserInstance.get();
        final Registratie registratie = createRegistratie(documentCreatieGegevens);
        final Data data = dataConverter.createData(documentCreatieGegevens, loggedInUser);
        final WizardRequest wizardRequest = new WizardRequest(new SmartDocument(), registratie, data);
        final WizardResponse wizardResponse = smartDocumentsClient.wizardDeposit(authenticationHeader, loggedInUser.getId(), wizardRequest);
        return URI.create(format("%s/smartdocuments/wizard/?ticket=%s", smartDocumentsURL, wizardResponse.ticket));
    }

    private Registratie createRegistratie(final DocumentCreatieGegevens documentCreatieGegevens) {
        final Registratie registratie = new Registratie();
        registratie.bronorganisatie = BRON_ORGANISATIE;
        registratie.informatieobjecttype = ztcClientService.readInformatieobjecttype(documentCreatieGegevens.getInformatieobjecttype()).getUrl();
        registratie.titel = documentCreatieGegevens.getTitel();
        registratie.informatieobjectStatus = documentCreatieGegevens.getInformatieobjectStatus();
        return registratie;
    }
}
