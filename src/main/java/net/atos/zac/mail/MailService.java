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
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;

import net.atos.client.brp.BRPClientService;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.documentcreatie.converter.DataConverter;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.mail.model.Attachment;
import net.atos.zac.mail.model.EMail;
import net.atos.zac.mail.model.EMails;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.mail.model.Verstuurder;
import net.atos.zac.mailtemplates.model.MailTemplateVariabelen;

@ApplicationScoped
public class MailService {

    private static final String MAILJET_API_KEY = ConfigProvider.getConfig().getValue("mailjet.api.key", String.class);

    private static final String MAILJET_API_SECRET_KEY = ConfigProvider.getConfig().getValue("mailjet.api.secret.key",
                                                                                             String.class);
    private static final String MAIL_VARIABLE = "{%s}";

    private static final String HTML_TAG_REGEX = "\\<[^>]*>";

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
    private BRPClientService brpClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private IdentityService identityService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    private final ClientOptions clientOptions = ClientOptions.builder().apiKey(MAILJET_API_KEY).apiSecretKey(MAILJET_API_SECRET_KEY).build();

    private final MailjetClient mailjetClient = new MailjetClient(clientOptions);

    public void sendMail(final Ontvanger ontvanger, final String onderwerp, final String body,
            final String bijlagen, final boolean createDocumentFromMail, final Zaak zaak, final TaskInfo taskInfo) {

        final String resolvedOnderwerp = resolveVariabelen(onderwerp, zaak, taskInfo).replaceAll(HTML_TAG_REGEX,
                                                                                                 StringUtils.EMPTY);
        final String resolvedBody = resolveVariabelen(body, zaak, taskInfo);
        final List<Attachment> attachments = getBijlagen(bijlagen);

        final EMail eMail = new EMail(resolvedBody, new Verstuurder(), List.of(ontvanger), resolvedOnderwerp, attachments);
        final MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .setBody(JSONB.toJson(new EMails(List.of(eMail))));
        try {
            final int status = mailjetClient.post(request).getStatus();
            if (status < 300) {
                if (createDocumentFromMail) {
                    createAndSaveDocumentFromMail(resolvedBody, resolvedOnderwerp, zaak);
                }
            } else {
                LOG.log(Level.WARNING,
                        String.format("Failed to send mail with subject '%s' (http result %d).", onderwerp, status));
            }
        } catch (MailjetException e) {
            LOG.log(Level.SEVERE, String.format("Failed to send mail with subject '%s'.", onderwerp), e);
        }
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

    private String resolveVariabelen(final String target, Zaak zaak, final TaskInfo taskInfo) {
        String resolvedTekst = target;

        if (zaak == null && taskInfo != null) {
            zaak = zrcClientService.readZaak(caseVariablesService.readZaakUUID(taskInfo.getScopeId()));
        }
        if (zaak != null) {
            resolvedTekst = resolveZaakVariabelen(resolvedTekst, zaak);
        }
        if (taskInfo != null) {
            resolvedTekst = resolveTaakVariabelen(resolvedTekst, taskInfo);
        }

        return resolvedTekst;
    }

    private String resolveZaakVariabelen(final String target, final Zaak zaak) {
        String resolvedTekst = target;
        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.ZAAKNUMMER))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.ZAAKNUMMER, zaak.getIdentificatie());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.ZAAKURL))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.ZAAKURL,
                                             configuratieService.zaakTonenUrl(zaak.getIdentificatie()).toString());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.OMSCHRIJVINGZAAK))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.OMSCHRIJVINGZAAK, zaak.getOmschrijving());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.TOELICHTINGZAAK))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOELICHTINGZAAK, zaak.getToelichting());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.REGISTRATIEDATUM))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.REGISTRATIEDATUM,
                                             zaak.getRegistratiedatum().toString());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.STARTDATUM))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.STARTDATUM,
                                             zaak.getStartdatum().toString());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.STREEFDATUM)) &&
                zaak.getEinddatumGepland() != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.STREEFDATUM,
                                             zaak.getEinddatumGepland().toString());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.FATALEDATUM)) &&
                zaak.getUiterlijkeEinddatumAfdoening() != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.FATALEDATUM,
                                             zaak.getUiterlijkeEinddatumAfdoening().toString());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.ZAAKSTATUS))) {
            final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
            final Statustype statustype = status != null ? ztcClientService.readStatustype(status.getStatustype()) : null;
            if (statustype != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.ZAAKSTATUS,
                                                 statustype.getOmschrijving());
            }
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.ZAAKTYPE))) {
            final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.ZAAKTYPE, zaaktype.getOmschrijving());
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.INITIATOR))) {
            final Rol<?> initiator = zgwApiService.findInitiatorForZaak(zaak);
            if (initiator != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.INITIATOR,
                                                 initiator.getNaam());
            }
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.ADRESINITIATOR))) {
            final Rol<?> initiator = zgwApiService.findInitiatorForZaak(zaak);
            if (initiator != null) {
                String initiatorAdres = null;
                if (initiator.getBetrokkeneType() == BetrokkeneType.NATUURLIJK_PERSOON) {
                    final IngeschrevenPersoonHal persoon =
                            brpClientService.findPersoon(initiator.getIdentificatienummer(), DataConverter.FIELDS_PERSOON);
                    if (persoon != null) {
                        initiatorAdres = String.format("%s %s", persoon.getVerblijfplaats().getAdresregel1(),
                                                       persoon.getVerblijfplaats().getAdresregel2());
                    }
                } else if (initiator.getBetrokkeneType() == BetrokkeneType.VESTIGING ||
                        initiator.getBetrokkeneType() == BetrokkeneType.NIET_NATUURLIJK_PERSOON) {
                    final ResultaatItem resultaatItem = initiator.getBetrokkeneType() == BetrokkeneType.VESTIGING ?
                            kvkClientService.findVestiging(initiator.getIdentificatienummer()) :
                            kvkClientService.findRechtspersoon(initiator.getIdentificatienummer());
                    if (resultaatItem != null) {
                        initiatorAdres = String.format("%s %s %s %s", resultaatItem.getStraatnaam(),
                                                       resultaatItem.getHuisnummer(), resultaatItem.getPostcode(),
                                                       resultaatItem.getPlaats());
                    }
                }
                if (initiatorAdres != null) {
                    resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.ADRESINITIATOR,
                                                     initiatorAdres);
                }
            }
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.TOEGEWEZENGROEPZAAK))) {
            final RolOrganisatorischeEenheid groep = zgwApiService.findGroepForZaak(zaak);
            if (groep != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOEGEWEZENGROEPZAAK,
                                                 groep.getNaam());
            }
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.TOEGEWEZENGEBRUIKERZAAK))) {
            final RolMedewerker behandelaar = zgwApiService.findBehandelaarForZaak(zaak);
            if (behandelaar != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOEGEWEZENGEBRUIKERZAAK,
                                                 behandelaar.getNaam());
            }
        }

        return resolvedTekst;
    }

    private String resolveTaakVariabelen(final String target, final TaskInfo taskInfo) {
        String resolvedTekst = target;

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.TOEGEWEZENGROEPTAAK))) {
            final String groepId = taskInfo.getIdentityLinks().stream()
                    .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                    .findAny()
                    .map(IdentityLinkInfo::getGroupId)
                    .orElse(null);
            final Group group = groepId != null ? identityService.readGroup(groepId) : null;
            if (group != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOEGEWEZENGROEPTAAK, group.getName());
            }
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.TOEGEWEZENGEBRUIKERTAAK))) {
            final User user = taskInfo.getAssignee() != null ? identityService.readUser(taskInfo.getAssignee()) : null;
            if (user != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOEGEWEZENGEBRUIKERTAAK,
                                                 user.getFullName());
            }
        }

        if (resolvedTekst.contains(getVariabele(MailTemplateVariabelen.TAAKURL))) {
            resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TAAKURL,
                                             configuratieService.taakTonenUrl(taskInfo.getId()).toString());
        }

        return resolvedTekst;
    }

    private String replaceVariabele(final String target, final MailTemplateVariabelen variabele, final String waarde) {
        return StringUtils.replace(target, getVariabele(variabele), waarde);
    }

    private String getVariabele(final MailTemplateVariabelen variabele) {
        return String.format(MAIL_VARIABLE, variabele);
    }
}
