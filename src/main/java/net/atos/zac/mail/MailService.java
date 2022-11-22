/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ADRESINITIATOR;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.FATALEDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.INITIATOR;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.OMSCHRIJVINGZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.REGISTRATIEDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.STARTDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.STREEFDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOEGEWEZENGEBRUIKERZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOEGEWEZENGROEPZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOELICHTINGZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKNUMMER;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKSTATUS;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKTYPE;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKURL;
import static net.atos.zac.util.JsonbUtil.JSONB;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    private final ClientOptions clientOptions = ClientOptions.builder().apiKey(MAILJET_API_KEY)
            .apiSecretKey(MAILJET_API_SECRET_KEY).build();

    private final MailjetClient mailjetClient = new MailjetClient(clientOptions);

    public void sendMail(final Ontvanger ontvanger, final String onderwerp, final String body,
            final String bijlagen, final boolean createDocumentFromMail, final Zaak zaak, final TaskInfo taskInfo) {

        final String resolvedOnderwerp = resolveVariabelen(onderwerp, zaak, taskInfo).replaceAll(HTML_TAG_REGEX,
                                                                                                 StringUtils.EMPTY);
        final String resolvedBody = resolveVariabelen(body, zaak, taskInfo);
        final List<Attachment> attachments = getBijlagen(bijlagen);

        final EMail eMail = new EMail(resolvedBody, new Verstuurder(), List.of(ontvanger), resolvedOnderwerp,
                                      attachments);
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

    private EnkelvoudigInformatieobjectWithInhoud createDocumentInformatieObject(final Zaak zaak,
            final String onderwerp, final String body) {
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
                .filter(infoObject -> infoObject.getOmschrijving()
                        .equals(ConfiguratieService.INFORMATIEOBJECTTYPE_OMSCHRIJVING_EMAIL)).findFirst()
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
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                    uuid);
            final ByteArrayInputStream byteArrayInputStream = drcClientService.downloadEnkelvoudigInformatieobject(
                    uuid);
            final Attachment attachment = new Attachment(enkelvoudigInformatieobject.getFormaat(),
                                                         enkelvoudigInformatieobject.getBestandsnaam(),
                                                         new String(Base64.getEncoder()
                                                                            .encode(byteArrayInputStream.readAllBytes())));
            attachments.add(attachment);
        });

        return attachments;
    }

    private String resolveVariabelen(final String tekst, Zaak zaak, final TaskInfo taskInfo) {
        String resolvedTekst = tekst;

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

    private String resolveZaakVariabelen(final String tekst, final Zaak zaak) {
        String resolvedTekst = tekst;
        resolvedTekst = replaceVariabele(resolvedTekst, ZAAKNUMMER, zaak.getIdentificatie());
        resolvedTekst = replaceVariabele(resolvedTekst, ZAAKURL,
                                         configuratieService.zaakTonenUrl(zaak.getIdentificatie()).toString());
        resolvedTekst = replaceVariabele(resolvedTekst, OMSCHRIJVINGZAAK, zaak.getOmschrijving());
        resolvedTekst = replaceVariabele(resolvedTekst, TOELICHTINGZAAK, zaak.getToelichting());
        resolvedTekst = replaceVariabele(resolvedTekst, REGISTRATIEDATUM, zaak.getRegistratiedatum().toString());
        resolvedTekst = replaceVariabele(resolvedTekst, STARTDATUM, zaak.getStartdatum().toString());

        if (zaak.getEinddatumGepland() != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, STREEFDATUM, zaak.getEinddatumGepland().toString());
        }

        if (zaak.getUiterlijkeEinddatumAfdoening() != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, FATALEDATUM,
                                             zaak.getUiterlijkeEinddatumAfdoening().toString());
        }

        if (resolvedTekst.contains(ZAAKSTATUS.getVariabele())) {
            final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
            final Statustype statustype = status != null ? ztcClientService.readStatustype(
                    status.getStatustype()) : null;
            if (statustype != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAKSTATUS, statustype.getOmschrijving());
            }
        }

        if (resolvedTekst.contains(ZAAKTYPE.getVariabele())) {
            final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAKTYPE, zaaktype.getOmschrijving());
        }

        if (resolvedTekst.contains(INITIATOR.getVariabele()) || resolvedTekst.contains(ADRESINITIATOR.getVariabele())) {
            final Optional<Rol<?>> initiator = zgwApiService.findInitiatorForZaak(zaak);
            if (initiator.isPresent()) {
                resolvedTekst = replaceInitiatorVariabelen(resolvedTekst, initiator.get());
            }
        }

        if (resolvedTekst.contains(TOEGEWEZENGROEPZAAK.getVariabele())) {
            final Optional<RolOrganisatorischeEenheid> groep = zgwApiService.findGroepForZaak(zaak);
            if (groep.isPresent()) {
                resolvedTekst = replaceVariabele(resolvedTekst, TOEGEWEZENGROEPZAAK, groep.get().getNaam());
            }
        }

        if (resolvedTekst.contains(TOEGEWEZENGEBRUIKERZAAK.getVariabele())) {
            final Optional<RolMedewerker> behandelaar = zgwApiService.findBehandelaarForZaak(zaak);
            if (behandelaar.isPresent()) {
                resolvedTekst = replaceVariabele(resolvedTekst, TOEGEWEZENGEBRUIKERZAAK, behandelaar.get().getNaam());
            }
        }

        return resolvedTekst;
    }

    private String resolveTaakVariabelen(final String tekst, final TaskInfo taskInfo) {
        String resolvedTekst = tekst;

        if (resolvedTekst.contains(MailTemplateVariabelen.TOEGEWEZENGROEPTAAK.getVariabele())) {
            final String groepId = taskInfo.getIdentityLinks().stream()
                    .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                    .findAny()
                    .map(IdentityLinkInfo::getGroupId)
                    .orElse(null);
            final Group group = groepId != null ? identityService.readGroup(groepId) : null;
            if (group != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOEGEWEZENGROEPTAAK,
                                                 group.getName());
            }
        }

        if (resolvedTekst.contains(MailTemplateVariabelen.TOEGEWEZENGEBRUIKERTAAK.getVariabele())) {
            final User user = taskInfo.getAssignee() != null ? identityService.readUser(taskInfo.getAssignee()) : null;
            if (user != null) {
                resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TOEGEWEZENGEBRUIKERTAAK,
                                                 user.getFullName());
            }
        }

        resolvedTekst = replaceVariabele(resolvedTekst, MailTemplateVariabelen.TAAKURL,
                                         configuratieService.taakTonenUrl(taskInfo.getId()).toString());

        return resolvedTekst;
    }

    private String replaceInitiatorVariabelen(final String resolvedTekst, final Rol<?> initiator) {
        return switch (initiator.getBetrokkeneType()) {
            case NATUURLIJK_PERSOON ->
                    brpClientService.findPersoon(initiator.getIdentificatienummer(), DataConverter.FIELDS_PERSOON)
                            .map(persoon -> replaceInitiatorVariabelenPersoon(resolvedTekst, persoon))
                            .orElse(resolvedTekst);
            case VESTIGING -> kvkClientService.findVestiging(initiator.getIdentificatienummer())
                    .map(vestiging -> replaceInitiatorVariabelenResultaatItem(resolvedTekst, vestiging))
                    .orElse(resolvedTekst);
            case NIET_NATUURLIJK_PERSOON -> kvkClientService.findRechtspersoon(initiator.getIdentificatienummer())
                    .map(natuurlijkPersoon -> replaceInitiatorVariabelenResultaatItem(resolvedTekst,
                                                                                      natuurlijkPersoon))
                    .orElse(resolvedTekst);
            default -> resolvedTekst;
        };
    }

    private String replaceInitiatorVariabelenPersoon(final String resolvedTekst, final IngeschrevenPersoonHal persoon) {
        final var naam = "%s %s".formatted(persoon.getNaam().getVoornamen(), persoon.getNaam().getGeslachtsnaam());
        return replaceInitiatorVariabelen(resolvedTekst, naam, getAdres(persoon));
    }

    private String getAdres(final IngeschrevenPersoonHal persoon) {
        if (persoon.getVerblijfplaats() == null) {
            return "Onbekend";
        }
        final var verblijfplaats = persoon.getVerblijfplaats();
        if (isNotBlank(verblijfplaats.getStraat())) {
            return "%s %s%s%s, %s %s".formatted(verblijfplaats.getStraat(),
                                                verblijfplaats.getHuisnummer(),
                                                emptyIfBlank(verblijfplaats.getHuisletter()),
                                                emptyIfBlank(verblijfplaats.getHuisnummertoevoeging()),
                                                verblijfplaats.getPostcode(),
                                                verblijfplaats.getWoonplaats());
        } else {
            return "%s, %s".formatted(persoon.getVerblijfplaats().getAdresregel1(),
                                      persoon.getVerblijfplaats().getAdresregel2());
        }
    }

    private String replaceInitiatorVariabelenResultaatItem(final String resolvedTekst,
            final ResultaatItem resultaatItem) {
        return replaceInitiatorVariabelen(resolvedTekst, resultaatItem.getHandelsnaam(), getAdres(resultaatItem));
    }

    private String getAdres(final ResultaatItem resultaatItem) {
        return "%s %s%s, %s %s".formatted(resultaatItem.getStraatnaam(),
                                          resultaatItem.getHuisnummer(),
                                          emptyIfBlank(resultaatItem.getHuisnummerToevoeging()),
                                          resultaatItem.getPostcode(),
                                          resultaatItem.getPlaats());
    }

    private String replaceInitiatorVariabelen(final String resolvedTekst, final String naam, final String adres) {
        return replaceVariabele(replaceVariabele(resolvedTekst, INITIATOR, naam), ADRESINITIATOR, adres);
    }

    private String replaceVariabele(final String target, final MailTemplateVariabelen variabele,
            final String waarde) {
        return StringUtils.replace(target, variabele.getVariabele(), waarde);
    }

    private String emptyIfBlank(final String value) {
        return isNotBlank(value) ? value : StringUtils.EMPTY;
    }
}
