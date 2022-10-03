/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.util.JsonbUtil.JSONB;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.mail.model.Attachment;
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
    private DRCClientService drcClientService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    private final ClientOptions clientOptions = ClientOptions.builder().apiKey(MAILJET_API_KEY).apiSecretKey(MAILJET_API_SECRET_KEY).build();

    private final MailjetClient mailjetClient = new MailjetClient(clientOptions);

    public boolean sendMail(final Ontvanger ontvanger, final String onderwerp, final String body,
            final List<Attachment> bijlagen) {
        final EMail eMail = new EMail(body, new Verstuurder(), List.of(ontvanger), onderwerp, bijlagen);
        final MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .setBody(JSONB.toJson(new EMails(List.of(eMail))));
        try {
            final int status = mailjetClient.post(request).getStatus();
            if (status < 300) {
                return true;
            }
            LOG.log(Level.WARNING, String.format("Failed to send mail with subject '%s' (http result %d).", onderwerp, status));
        } catch (MailjetException e) {
            LOG.log(Level.SEVERE, String.format("Failed to send mail with subject '%s'.", onderwerp), e);
        }
        return false;
    }

    public boolean sendMail(final String ontvanger, final String onderwerp, final String body,
            final String bijlagen, final boolean createDocumentFromMail, final Zaak zaak) {

        final List<Attachment> attachments = getBijlagen(bijlagen);

        final boolean sent = sendMail(new Ontvanger(ontvanger), onderwerp, body, attachments);
        if (sent && createDocumentFromMail) {
            createAndSaveDocumentFromMail(body, onderwerp, zaak);
        }
        return sent;
    }

    private void createAndSaveDocumentFromMail(final String body, final String onderwerp, final Zaak zaak) {
        final EnkelvoudigInformatieobjectWithInhoud data = createDocumentInformatieObject(zaak, onderwerp, body);
        zgwApiService.createZaakInformatieobjectForZaak(zaak, data, onderwerp, onderwerp,
                                                        OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
    }

    private EnkelvoudigInformatieobjectWithInhoud createDocumentInformatieObject(final Zaak zaak, final String onderwerp, final String body) {
        final Informatieobjecttype eMailObjectType = getEmailInformatieObjectType(zaak);
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = new EnkelvoudigInformatieobjectWithInhoud();
        enkelvoudigInformatieobjectWithInhoud.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        enkelvoudigInformatieobjectWithInhoud.setCreatiedatum(LocalDate.now());
        enkelvoudigInformatieobjectWithInhoud.setTitel(onderwerp);
        enkelvoudigInformatieobjectWithInhoud.setAuteur(loggedInUserInstance.get().getFullName());
        enkelvoudigInformatieobjectWithInhoud.setTaal(ConfiguratieService.TAAL_NEDERLANDS);
        enkelvoudigInformatieobjectWithInhoud.setInformatieobjecttype(eMailObjectType.getUrl());
        enkelvoudigInformatieobjectWithInhoud.setInhoud(body.getBytes(StandardCharsets.UTF_8));
        enkelvoudigInformatieobjectWithInhoud.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.OPENBAAR);
        enkelvoudigInformatieobjectWithInhoud.setFormaat("text/plain");
        enkelvoudigInformatieobjectWithInhoud.setBestandsnaam(String.format("%s.txt", onderwerp));
        enkelvoudigInformatieobjectWithInhoud.setStatus(InformatieobjectStatus.DEFINITIEF);
        enkelvoudigInformatieobjectWithInhoud.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.OPENBAAR);
        enkelvoudigInformatieobjectWithInhoud.setVerzenddatum(LocalDate.now());
        return enkelvoudigInformatieobjectWithInhoud;
    }

    private Informatieobjecttype getEmailInformatieObjectType(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        return zaaktype.getInformatieobjecttypen().stream()
                .map(ztcClientService::readInformatieobjecttype)
                .filter(infoObject -> infoObject.getOmschrijving().equals(ConfiguratieService.INFORMATIEOBJECTTYPE_OMSCHRIJVING_EMAIL)).findFirst()
                .orElseThrow();
    }

    private List<Attachment> getBijlagen(final String bijlagenString) {
        final List<UUID> bijlagen = new ArrayList<>();
        if (StringUtils.isNotEmpty(bijlagenString)) {
            Arrays.stream(bijlagenString.split(";")).forEach(uuidString -> bijlagen.add(UUIDUtil.uuid(uuidString)));
        } else {
            return Collections.emptyList();
        }

        final List<Attachment> attachments = new ArrayList<>();
        bijlagen.forEach(uuid -> {
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
            final ByteArrayInputStream byteArrayInputStream = drcClientService.downloadEnkelvoudigInformatieobject(uuid);
            final Attachment attachment = new Attachment(enkelvoudigInformatieobject.getFormaat(),
                                                         enkelvoudigInformatieobject.getBestandsnaam(),
                                                         new String(Base64.getEncoder()
                                                                            .encode(byteArrayInputStream.readAllBytes())));
            attachments.add(attachment);
        });

        return attachments;
    }
}
