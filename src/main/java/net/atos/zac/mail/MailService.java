/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.util.JsonbUtil.JSONB;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.ConfigProvider;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.mail.model.EMail;
import net.atos.zac.mail.model.EMails;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.mail.model.Verstuurder;

@ApplicationScoped
public class MailService {

    private static final String MAILJET_API_KEY = ConfigProvider.getConfig().getValue("mailjet.api.key", String.class);
    private static final String MAILJET_API_SECRET_KEY = ConfigProvider.getConfig().getValue("mailjet.api.secret.key",
                                                                                       String.class);

    private static final Logger LOG = Logger.getLogger(MailService.class.getName());

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    private final ClientOptions clientOptions = ClientOptions.builder().apiKey(MAILJET_API_KEY).apiSecretKey(MAILJET_API_SECRET_KEY).build();
    private final MailjetClient mailjetClient = new MailjetClient(clientOptions);

    public void sendMail(final String ontvanger, final String onderwerp, final String body,
            final boolean createDocumentFromMail, final UUID zaakUuid) {
        final EMail eMail = new EMail(body, new Verstuurder(), List.of(new Ontvanger(ontvanger)), onderwerp);

        final MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .setBody(JSONB.toJson(new EMails(List.of(eMail))));

        MailjetResponse response = null;
        try {
            response = mailjetClient.post(request);
        } catch (MailjetException e) {
            LOG.log(Level.SEVERE, String.format("Failed to send mail with subject '%s' on case '%s'.",
                                                onderwerp, zaakUuid), e);
        }

        if (createDocumentFromMail && response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
            createAndSaveDocumentFromMail(body, onderwerp, zaakUuid);
        }
    }

    private void createAndSaveDocumentFromMail(final String body, final String onderwerp, final UUID zaakUuid) {
        final Zaak zaak = zrcClientService.readZaak(zaakUuid);
        final EnkelvoudigInformatieobjectWithInhoud data = createDocumentInformatieObject(zaak, onderwerp, body);
        zgwApiService.createZaakInformatieobjectForZaak(zaak, data, onderwerp, onderwerp, OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
    }

    private EnkelvoudigInformatieobjectWithInhoud createDocumentInformatieObject(final Zaak zaak,
            final String onderwerp, final String body) {
        final Informatieobjecttype eMailObjectType = getEmailInformatieObjectType(zaak);
        final EnkelvoudigInformatieobjectWithInhoud data = new EnkelvoudigInformatieobjectWithInhoud(
                ConfiguratieService.BRON_ORGANISATIE, LocalDate.now(), onderwerp, ingelogdeMedewerker.get().getNaam(),
                ConfiguratieService.TAAL_NEDERLANDS, eMailObjectType.getUrl(), Base64.getEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8)));
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.OPENBAAR);
        data.setFormaat("text/plain");
        data.setBestandsnaam(String.format("%s.txt", onderwerp));
        data.setStatus(InformatieobjectStatus.DEFINITIEF);
        data.setIndicatieGebruiksrecht(false);
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.OPENBAAR);

        return data;
    }

    private Informatieobjecttype getEmailInformatieObjectType(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        return zaaktype.getInformatieobjecttypen().stream()
                .map(ztcClientService::readInformatieobjecttype)
                .filter(infoObject -> infoObject.getOmschrijving().equals("e-mail")).findFirst().orElseThrow();
    }
}
