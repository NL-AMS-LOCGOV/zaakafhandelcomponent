/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.formulieren;

import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.client.klanten.KlantenClientService;
import net.atos.client.klanten.model.Klant;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.formulieren.model.RESTFormData;
import net.atos.zac.app.informatieobjecten.EnkelvoudigInformatieObjectUpdateService;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.formulieren.model.FormulierDefinitie;
import net.atos.zac.formulieren.model.FormulierVeldDefinitie;
import net.atos.zac.formulieren.model.FormulierVeldtype;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Bronnen;
import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.model.MailGegevens;
import net.atos.zac.util.ValidationUtil;
import net.atos.zac.zaaksturing.ReferentieTabelService;
import net.atos.zac.zaaksturing.model.ReferentieTabel;
import net.atos.zac.zaaksturing.model.ReferentieTabelWaarde;

public class FormulierRuntimeService {

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private IdentityService identityService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private EnkelvoudigInformatieObjectUpdateService enkelvoudigInformatieObjectUpdateService;

    @Inject
    private ReferentieTabelService referentieTabelService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private KlantenClientService klantenClientService;

    @Inject
    private MailService mailService;

    private static final String SEPARATOR = ";";

    public static final DateTimeFormatter DATUM_FORMAAT = DateTimeFormatter.ofPattern("dd-MM-yyy");

    public void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie) {
        formulierDefinitie.getVeldDefinities().forEach(this::resolveDefaultVeldWaardenZonderContext);
    }

    /**
     * @param dataElementen set ingevulde waarden van een voorgaande formulieren
     */
    public void resolveDefaultwaarden(FormulierDefinitie formulierDefinitie, Map<String, String> dataElementen) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> dataElementen.forEach((k, v) -> {
            if (k.equals(veldDefinitie.getDefaultWaarde())) {
                veldDefinitie.setDefaultWaarde(v);
            }
        }));
    }

    public void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie, RESTTaak taak) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "TAAK:STARTDATUM" ->
                            veldDefinitie.setDefaultWaarde(taak.creatiedatumTijd.format(DATUM_FORMAAT));
                    case "TAAK:FATALE_DATUM" -> veldDefinitie.setDefaultWaarde(taak.fataledatum.format(DATUM_FORMAAT));
                    case "TAAK:GROEP" -> veldDefinitie.setDefaultWaarde(taak.groep != null ? taak.groep.naam : null);
                    case "TAAK:BEHANDELAAR" ->
                            veldDefinitie.setDefaultWaarde(taak.behandelaar != null ? taak.behandelaar.naam : null);
                    default -> {
                    }
                }
            }
        });
    }

    public void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie, RESTZaak zaak) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "ZAAK:STARTDATUM" -> veldDefinitie.setDefaultWaarde(zaak.startdatum.format(DATUM_FORMAAT));
                    case "ZAAK:FATALE_DATUM" ->
                            veldDefinitie.setDefaultWaarde(zaak.uiterlijkeEinddatumAfdoening.format(DATUM_FORMAAT));
                    case "ZAAK:STREEFDATUM" ->
                            veldDefinitie.setDefaultWaarde(zaak.einddatumGepland.format(DATUM_FORMAAT));
                    case "ZAAK:GROEP" -> veldDefinitie.setDefaultWaarde(zaak.groep != null ? zaak.groep.naam : null);
                    case "ZAAK:BEHANDELAAR" ->
                            veldDefinitie.setDefaultWaarde(zaak.behandelaar != null ? zaak.behandelaar.naam : null);
                    default -> {
                    }
                }
            }
        });
    }


    public void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie, Zaak zaak) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "ZAAK:STARTDATUM" ->
                            veldDefinitie.setDefaultWaarde(zaak.getStartdatum().format(DATUM_FORMAAT));
                    case "ZAAK:FATALE_DATUM" -> veldDefinitie.setDefaultWaarde(
                            zaak.getUiterlijkeEinddatumAfdoening().format(DATUM_FORMAAT));
                    case "ZAAK:STREEFDATUM" ->
                            veldDefinitie.setDefaultWaarde(zaak.getEinddatumGepland().format(DATUM_FORMAAT));
                    case "ZAAK:GROEP" -> veldDefinitie.setDefaultWaarde(findGroep(zaak).getName()); //
                    case "ZAAK:BEHANDELAAR" -> veldDefinitie.setDefaultWaarde(findBehandelaar(zaak).getFullName());
                    default -> {
                    }
                }
            }
        });
    }

    public void resolveDefaultwaarden(FormulierDefinitie formulierDefinitie, TaskInfo task) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "TAAK:STARTDATUM}" -> veldDefinitie.setDefaultWaarde(
                            convertToZonedDateTime(task.getCreateTime()).format(DATUM_FORMAAT));
                    case "TAAK:FATALE_DATUM" ->
                            veldDefinitie.setDefaultWaarde(convertToLocalDate(task.getDueDate()).format(DATUM_FORMAAT));
                    case "TAAK:GROEP" -> {
                        String groupId = extractGroupId(task.getIdentityLinks());
                        veldDefinitie.setDefaultWaarde(
                                groupId != null ? identityService.readGroup(groupId).getName() : null);
                    }
                    case "TAAK:BEHANDELAAR" -> veldDefinitie.setDefaultWaarde(
                            task.getAssignee() != null ? identityService.readUser(task.getAssignee())
                                    .getFullName() : null);
                    default -> {
                    }
                }
            }
        });
    }

    public void verstuurMail(final FormulierDefinitie formulierDefinitie, final RESTFormData data, final Zaak zaak) {
        if (formulierDefinitie.isMailVersturen()) {

            String ontvanger = switch (formulierDefinitie.getMailTo()) {
                case "INITIATOR" -> getInitiatorMail(zaak); // deze moet nog
                case default -> data.substitute(formulierDefinitie.getMailTo());
            };

            if (StringUtils.isNotBlank(ontvanger) && !ValidationUtil.isValidEmail(ontvanger)) {
                // kan ook een medewerker veld zijn
                final String o = ontvanger;
                ontvanger = identityService.listUsers().stream().filter(m -> m.getFullName().equals(o)).findAny()
                        .map(User::getEmail).orElse(o);
            }

            final String gemeente = configuratieService.readGemeenteNaam();
            final String afzender = switch (formulierDefinitie.getMailFrom()) {
                case "GEMEENTE" -> configuratieService.readGemeenteMail();
                case "MEDEWERKER" -> loggedInUserInstance.get().getEmail();
                case default -> data.substitute(formulierDefinitie.getMailFrom());
            };

            final String body = data.substituteText(formulierDefinitie.getMailBody());
            final String subject = data.substituteText(formulierDefinitie.getMailSubject());

            if (StringUtils.isNotBlank(ontvanger) && ValidationUtil.isValidEmail(ontvanger)) {

                MailGegevens mailGegevens = new MailGegevens(
                        new MailAdres(afzender, gemeente),
                        new MailAdres(ontvanger),
                        null, subject, body, data.mailBijlagen, true);


                final String bodyZoalsVerzonden = mailService.sendMail(mailGegevens, Bronnen.fromZaak(zaak));
                data.formState.put("%s_mail-bericht".formatted(formulierDefinitie.getSysteemnaam()),
                                   bodyZoalsVerzonden);
                data.formState.put("%s_mail-onderwerp".formatted(formulierDefinitie.getSysteemnaam()),
                                   mailGegevens.getSubject());
                data.formState.put("%s_mail-afzender".formatted(formulierDefinitie.getSysteemnaam()),
                                   mailGegevens.getFrom().getEmail());
                data.formState.put("%s_mail-ontvanger".formatted(formulierDefinitie.getSysteemnaam()),
                                   mailGegevens.getTo().getEmail());
            } else {
                data.formState.put("%s_mail-bericht".formatted(formulierDefinitie.getSysteemnaam()), body);
                data.formState.put("%s_mail-onderwerp".formatted(formulierDefinitie.getSysteemnaam()), subject);
                data.formState.put("%s_mail-afzender".formatted(formulierDefinitie.getSysteemnaam()), afzender);
                data.formState.put("%s_mail-ontvanger".formatted(formulierDefinitie.getSysteemnaam()),
                                   "%s [NIET VERZONDEN]".formatted(StringUtils.isNotBlank(ontvanger) ?
                                                                           "Ongeldig: %s".formatted(ontvanger) :
                                                                           "Geen e-mail adres"));
            }
        }
    }

    private String getInitiatorMail(Zaak zaak) {
        return zgwApiService.findInitiatorForZaak(zaak).map(initiator -> switch (initiator.getBetrokkeneType()) {
            case NATUURLIJK_PERSOON -> klantenClientService.findPersoon(initiator.getIdentificatienummer()).map(
                    Klant::getEmailadres).orElse(null);
            case VESTIGING -> klantenClientService.findVestiging(initiator.getIdentificatienummer()).map(
                    Klant::getEmailadres).orElse(null);
            case null, default -> null;
        }).orElse(null);
    }


    public void versturenDocumenten(RESTFormData formData) {
        if (formData.documentenVerzenden != null) {
            Arrays.stream(formData.documentenVerzenden.split(SEPARATOR))
                    .map(UUID::fromString)
                    .map(drcClientService::readEnkelvoudigInformatieobject)
                    .forEach(
                            informatieobject -> enkelvoudigInformatieObjectUpdateService.verzendEnkelvoudigInformatieObject(
                                    informatieobject.getUUID(), formData.documentenVerzendenDatum,
                                    formData.toelichting));

        }
    }

    public void ondertekenDocumenten(RESTFormData formData) {
        if (formData.documentenOndertekenen != null) {
            Arrays.stream(formData.documentenOndertekenen.split(SEPARATOR))
                    .map(UUID::fromString)
                    .map(drcClientService::readEnkelvoudigInformatieobject)
                    .filter(enkelvoudigInformatieobject -> enkelvoudigInformatieobject.getOndertekening() == null)
                    .forEach(enkelvoudigInformatieobject ->
                                     enkelvoudigInformatieObjectUpdateService.ondertekenEnkelvoudigInformatieObject(
                                             enkelvoudigInformatieobject.getUUID()));

        }
    }


    private User findBehandelaar(final Zaak zaak) {
        return zgwApiService.findBehandelaarForZaak(zaak)
                .map(behandelaar -> identityService.readUser(
                        behandelaar.getBetrokkeneIdentificatie().getIdentificatie()))
                .orElse(null);
    }


    private Group findGroep(final Zaak zaak) {
        return zgwApiService.findGroepForZaak(zaak)
                .map(groep -> identityService.readGroup(groep.getBetrokkeneIdentificatie().getIdentificatie()))
                .orElse(null);
    }

    private String extractGroupId(final List<? extends IdentityLinkInfo> identityLinks) {
        return identityLinks.stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .findAny()
                .map(IdentityLinkInfo::getGroupId)
                .orElse(null);
    }

    private void resolveDefaultVeldWaardenZonderContext(final FormulierVeldDefinitie veldDefinitie) {
        final String referentietabelCode = StringUtils.substringAfter(veldDefinitie.getMeerkeuzeOpties(), "REF:");
        if (StringUtils.isNotBlank(referentietabelCode)) {
            final ReferentieTabel referentieTabel = referentieTabelService.readReferentieTabel(referentietabelCode);
            veldDefinitie.setMeerkeuzeOpties(referentieTabel.getWaarden()
                                                     .stream()
                                                     .sorted(Comparator.comparingInt(
                                                             ReferentieTabelWaarde::getVolgorde))
                                                     .map(ReferentieTabelWaarde::getNaam)
                                                     .collect(Collectors.joining(SEPARATOR)));
        }

        if (veldDefinitie.getVeldtype() == FormulierVeldtype.CHECKBOX) {
            String defaultWaarde = veldDefinitie.getDefaultWaarde();
            if (StringUtils.equalsIgnoreCase("ja", defaultWaarde) ||
                    StringUtils.equalsIgnoreCase("true", defaultWaarde) || StringUtils.equals("1", defaultWaarde)) {
                veldDefinitie.setDefaultWaarde(BooleanUtils.TRUE);
            }
        }

        if (veldDefinitie.getVeldtype() == FormulierVeldtype.DATUM) {
            String defaultWaarde = veldDefinitie.getDefaultWaarde();
            if (StringUtils.isNotBlank(defaultWaarde)) {
                if (defaultWaarde.matches("^[+-]\\d{1,4}$")) {
                    int dagen = Integer.parseInt(StringUtils.substring(defaultWaarde, 1));
                    if (defaultWaarde.startsWith("+")) {
                        veldDefinitie.setDefaultWaarde(
                                LocalDate.now().plusDays(dagen).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    } else {
                        veldDefinitie.setDefaultWaarde(
                                LocalDate.now().minusDays(dagen).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    }
                }
            }
        }
    }


}
