/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie;

import static java.lang.String.format;
import static net.atos.zac.configuratie.ConfiguratieService.BRON_ORGANISATIE;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.sd.SmartDocumentsClient;
import net.atos.client.sd.exception.BadRequestException;
import net.atos.client.sd.model.SmartDocument;
import net.atos.client.sd.model.WizardResponse;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.documentcreatie.converter.DataConverter;
import net.atos.zac.documentcreatie.model.Data;
import net.atos.zac.documentcreatie.model.DocumentCreatieGegevens;
import net.atos.zac.documentcreatie.model.DocumentCreatieResponse;
import net.atos.zac.documentcreatie.model.Registratie;
import net.atos.zac.documentcreatie.model.WizardRequest;

@ApplicationScoped
public class DocumentCreatieService {

    private static final String HTTPS = "https";

    @Inject
    @RestClient
    private SmartDocumentsClient smartDocumentsClient;

    @Inject
    @ConfigProperty(name = "SD_CLIENT_MP_REST_URL")
    private String smartDocumentsURL;

    @Inject
    @ConfigProperty(name = "SD_AUTHENTICATION")
    private String authenticationToken;

    @Inject
    private DataConverter dataConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    /**
     * Creeer een document met de wizard van SmartDocuments.
     *
     * @param documentCreatieGegevens Gegevens op basis van welke het document wordt gecreeerd.
     * @return De redirect URL naar de SmartDocuments Wizard
     */
    public DocumentCreatieResponse creeerDocumentAttendedSD(final DocumentCreatieGegevens documentCreatieGegevens) {
        final LoggedInUser loggedInUser = loggedInUserInstance.get();
        final Registratie registratie = createRegistratie(documentCreatieGegevens);
        final Data data = dataConverter.createData(documentCreatieGegevens, loggedInUser);
        final WizardRequest wizardRequest = new WizardRequest(new SmartDocument(), registratie, data);
        try {
            final WizardResponse wizardResponse = smartDocumentsClient.wizardDeposit(format("Basic %s", authenticationToken), loggedInUser.getId(),
                                                                                     wizardRequest);
            return new DocumentCreatieResponse(
                    UriBuilder.fromUri(smartDocumentsURL).path("smartdocuments/wizard").queryParam("ticket", wizardResponse.ticket).build());
        } catch (final BadRequestException badRequestException) {
            return new DocumentCreatieResponse("Aanmaken van een document is helaas niet mogelijk. (ben je als user geregistreerd in SmartDocuments?)");
        }
    }

    private Registratie createRegistratie(final DocumentCreatieGegevens documentCreatieGegevens) {
        final Registratie registratie = new Registratie();
        registratie.bronorganisatie = BRON_ORGANISATIE;
        registratie.zaak = zrcClientService.createUrlExternToZaak(documentCreatieGegevens.getZaakUUID());
        registratie.informatieobjecttype = ztcClientService.createUrlExternToInformatieobjecttype(documentCreatieGegevens.getInformatieobjecttype());
        registratie.titel = documentCreatieGegevens.getTitel();
        registratie.informatieobjectStatus = documentCreatieGegevens.getInformatieobjectStatus();
        return registratie;
    }
}
