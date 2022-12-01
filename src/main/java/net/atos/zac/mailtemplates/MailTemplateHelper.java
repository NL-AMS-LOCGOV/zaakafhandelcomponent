package net.atos.zac.mailtemplates;

import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ADRESINITIATOR;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.DOCUMENTLINK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.DOCUMENTTITEL;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.DOCUMENTURL;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.FATALEDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.INITIATOR;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.OMSCHRIJVINGZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.REGISTRATIEDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.STARTDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.STREEFDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TAAKLINK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TAAKURL;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOEGEWEZENGEBRUIKERTAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOEGEWEZENGEBRUIKERZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOEGEWEZENGROEPTAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOEGEWEZENGROEPZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TOELICHTINGZAAK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKLINK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKNUMMER;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKSTATUS;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKTYPE;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAKURL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.client.brp.BRPClientService;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.Verblijfplaats;
import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.documentcreatie.converter.DataConverter;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.mailtemplates.model.MailLink;
import net.atos.zac.mailtemplates.model.MailTemplateVariabelen;

public class MailTemplateHelper {

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private BRPClientService brpClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private IdentityService identityService;

    @Inject
    private CaseVariablesService caseVariablesService;

    public String resolveVariabelen(final String tekst, final Zaak zaak) {
        String resolvedTekst = tekst;
        if (zaak != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAKNUMMER, zaak.getIdentificatie());

            final MailLink link = getLink(zaak);
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAKURL, link.url);
            resolvedTekst = replaceVariabeleHtml(resolvedTekst, ZAAKLINK, link.toHtml());

            resolvedTekst = replaceVariabele(resolvedTekst, OMSCHRIJVINGZAAK, zaak.getOmschrijving());
            resolvedTekst = replaceVariabele(resolvedTekst, TOELICHTINGZAAK, zaak.getToelichting());
            resolvedTekst = replaceVariabele(resolvedTekst, REGISTRATIEDATUM, zaak.getRegistratiedatum());
            resolvedTekst = replaceVariabele(resolvedTekst, STARTDATUM, zaak.getStartdatum());
            resolvedTekst = replaceVariabele(resolvedTekst, STREEFDATUM, zaak.getEinddatumGepland());
            resolvedTekst = replaceVariabele(resolvedTekst, FATALEDATUM, zaak.getUiterlijkeEinddatumAfdoening());

            if (resolvedTekst.contains(ZAAKSTATUS.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAKSTATUS,
                                                 Optional.of(zaak.getStatus())
                                                         .map(zrcClientService::readStatus)
                                                         .map(Status::getStatustype)
                                                         .map(ztcClientService::readStatustype)
                                                         .map(Statustype::getOmschrijving)
                );
            }

            if (resolvedTekst.contains(ZAAKTYPE.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAKTYPE,
                                                 Optional.of(zaak.getZaaktype())
                                                         .map(ztcClientService::readZaaktype)
                                                         .map(Zaaktype::getOmschrijving));
            }

            if (resolvedTekst.contains(INITIATOR.getVariabele()) || resolvedTekst.contains(
                    ADRESINITIATOR.getVariabele())) {
                resolvedTekst = replaceInitiatorVariabelen(resolvedTekst,
                                                           zgwApiService.findInitiatorForZaak(zaak));
            }

            if (resolvedTekst.contains(TOEGEWEZENGROEPZAAK.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, TOEGEWEZENGROEPZAAK,
                                                 zgwApiService.findGroepForZaak(zaak)
                                                         .map(RolOrganisatorischeEenheid::getNaam));
            }

            if (resolvedTekst.contains(TOEGEWEZENGEBRUIKERZAAK.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, TOEGEWEZENGEBRUIKERZAAK,
                                                 zgwApiService.findBehandelaarForZaak(zaak)
                                                         .map(RolMedewerker::getNaam));
            }
        }
        return resolvedTekst;
    }

    public String resolveVariabelen(final String tekst, final TaskInfo taskInfo) {
        String resolvedTekst = tekst;
        if (taskInfo != null) {
            final MailLink link = getLink(taskInfo);
            resolvedTekst = replaceVariabele(resolvedTekst, TAAKURL, link.url);
            resolvedTekst = replaceVariabeleHtml(resolvedTekst, TAAKLINK, link.toHtml());

            if (resolvedTekst.contains(TOEGEWEZENGROEPTAAK.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, TOEGEWEZENGROEPTAAK,
                                                 taskInfo.getIdentityLinks().stream()
                                                         .filter(identityLinkInfo ->
                                                                         IdentityLinkType.CANDIDATE.equals(
                                                                                 identityLinkInfo.getType()))
                                                         .findAny()
                                                         .map(IdentityLinkInfo::getGroupId)
                                                         .map(identityService::readGroup)
                                                         .map(Group::getName));
            }

            if (resolvedTekst.contains(TOEGEWEZENGEBRUIKERTAAK.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, TOEGEWEZENGEBRUIKERTAAK,
                                                 Optional.of(taskInfo.getAssignee())
                                                         .map(identityService::readUser)
                                                         .map(User::getFullName));
            }
        }
        return resolvedTekst;
    }

    public String resolveVariabelen(final String tekst, final EnkelvoudigInformatieobject document) {
        String resolvedTekst = tekst;
        if (document != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, DOCUMENTTITEL, document.getTitel());

            final MailLink link = getLink(document);
            resolvedTekst = replaceVariabele(resolvedTekst, DOCUMENTURL, link.url);
            resolvedTekst = replaceVariabeleHtml(resolvedTekst, DOCUMENTLINK, link.toHtml());
        }
        return resolvedTekst;
    }

    private MailLink getLink(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        return new MailLink(zaak.getIdentificatie(),
                            configuratieService.zaakTonenUrl(zaak.getIdentificatie()),
                            "de zaak", "(%s)".formatted(zaaktype.getOmschrijving()));
    }

    private MailLink getLink(final TaskInfo taskInfo) {
        final String zaakIdentificatie = caseVariablesService.readZaakIdentificatie(taskInfo.getScopeId());
        final String zaaktypeOmschrijving = caseVariablesService.readZaaktypeOmschrijving(taskInfo.getScopeId());
        return new MailLink(taskInfo.getName(),
                            configuratieService.taakTonenUrl(taskInfo.getId()),
                            "de taak", "voor zaak %s (%s)".formatted(zaakIdentificatie, zaaktypeOmschrijving));
    }

    private MailLink getLink(final EnkelvoudigInformatieobject document) {
        return new MailLink(document.getTitel(),
                            configuratieService.informatieobjectTonenUrl(document.getUUID()),
                            "het document", null);
    }

    private String replaceInitiatorVariabelen(final String resolvedTekst, final Optional<Rol<?>> initiator) {
        if (initiator.isPresent()) {
            final String identificatie = initiator.get().getIdentificatienummer();
            final BetrokkeneType betrokkene = initiator.get().getBetrokkeneType();
            return switch (betrokkene) {
                case NATUURLIJK_PERSOON -> replaceInitiatorVariabelenPersoon(
                        resolvedTekst,
                        brpClientService.findPersoon(identificatie, DataConverter.FIELDS_PERSOON));
                case VESTIGING -> replaceInitiatorVariabelenResultaatItem(
                        resolvedTekst,
                        kvkClientService.findVestiging(identificatie));
                case NIET_NATUURLIJK_PERSOON -> replaceInitiatorVariabelenResultaatItem(
                        resolvedTekst,
                        kvkClientService.findRechtspersoon(identificatie));
                default -> throw new IllegalStateException(String.format("unexpected betrokkenetype %s", betrokkene));
            };
        }
        return replaceInitiatorVariabelen(resolvedTekst, null, null);
    }

    private static String replaceInitiatorVariabelenPersoon(final String resolvedTekst,
            final Optional<IngeschrevenPersoonHal> initiator) {
        return initiator
                .map(persoon -> replaceInitiatorVariabelen(resolvedTekst, getNaam(persoon), getAdres(persoon)))
                .orElse(replaceInitiatorVariabelenOnbekend(resolvedTekst));
    }

    private static String replaceInitiatorVariabelenResultaatItem(final String resolvedTekst,
            final Optional<ResultaatItem> initiator) {
        return initiator
                .map(item -> replaceInitiatorVariabelen(resolvedTekst, getNaam(item), getAdres(item)))
                .orElse(replaceInitiatorVariabelenOnbekend(resolvedTekst));
    }

    private static String getNaam(final IngeschrevenPersoonHal persoon) {
        return "%s %s".formatted(
                persoon.getNaam().getVoornamen(),
                persoon.getNaam().getGeslachtsnaam());
    }

    private static String getNaam(final ResultaatItem item) {
        return item.getHandelsnaam();
    }

    private static String getAdres(final IngeschrevenPersoonHal persoon) {
        final Verblijfplaats verblijfplaats = persoon.getVerblijfplaats();
        if (verblijfplaats != null) {
            if (isNotBlank(verblijfplaats.getStraat())) {
                return "%s %s%s%s, %s %s".formatted(
                        verblijfplaats.getStraat(),
                        emptyIfBlank(verblijfplaats.getHuisnummer()),
                        emptyIfBlank(verblijfplaats.getHuisletter()),
                        emptyIfBlank(verblijfplaats.getHuisnummertoevoeging()),
                        emptyIfBlank(verblijfplaats.getPostcode()),
                        verblijfplaats.getWoonplaats());
            } else {
                return "%s, %s".formatted(
                        verblijfplaats.getAdresregel1(),
                        verblijfplaats.getAdresregel2());
            }
        }
        return null;
    }

    private static String getAdres(final ResultaatItem item) {
        return "%s %s%s, %s %s".formatted(
                item.getStraatnaam(),
                emptyIfBlank(item.getHuisnummer()),
                emptyIfBlank(item.getHuisnummerToevoeging()),
                emptyIfBlank(item.getPostcode()),
                item.getPlaats());
    }

    private static String replaceInitiatorVariabelenOnbekend(final String resolvedTekst) {
        return replaceInitiatorVariabelen(resolvedTekst, "Onbekend", null);
    }

    private static String replaceInitiatorVariabelen(final String resolvedTekst, final String naam,
            final String adres) {
        return replaceVariabele(replaceVariabele(resolvedTekst,
                                                 INITIATOR,
                                                 naam),
                                ADRESINITIATOR,
                                adres);
    }

    private static <T> String replaceVariabele(final String target, final MailTemplateVariabelen variabele,
            final T waarde) {
        return replaceVariabele(target, variabele, waarde != null ? waarde.toString() : null);
    }

    private static <T> String replaceVariabele(final String target, final MailTemplateVariabelen variabele,
            final Optional<T> waarde) {
        return replaceVariabele(target, variabele, waarde.map(T::toString).orElse(null));
    }

    private static String replaceVariabele(final String target, final MailTemplateVariabelen variabele,
            final String waarde) {
        return replaceVariabeleHtml(target, variabele, StringEscapeUtils.escapeHtml4(waarde));
    }

    // Make sure that what is passed in the html argument is FULLY encoded HTML (no injection vulnerabilities please!)
    private static String replaceVariabeleHtml(final String target, final MailTemplateVariabelen variabele,
            final String html) {
        return StringUtils.replace(target,
                                   variabele.getVariabele(),
                                   variabele.isResolveVariabeleAlsLegeString()
                                           ? emptyIfBlank(html)
                                           : html);
    }

    private static <T> String emptyIfBlank(final T value) {
        return emptyIfBlank(value != null ? value.toString() : null);
    }

    private static String emptyIfBlank(final String value) {
        return isNotBlank(value) ? value : StringUtils.EMPTY;
    }
}
