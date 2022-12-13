package net.atos.zac.mailtemplates;

import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.DOCUMENT_LINK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.DOCUMENT_TITEL;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.DOCUMENT_URL;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TAAK_BEHANDELAAR_GROEP;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TAAK_BEHANDELAAR_MEDEWERKER;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TAAK_LINK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.TAAK_URL;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_BEHANDELAAR_GROEP;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_BEHANDELAAR_MEDEWERKER;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_FATALEDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_INITIATOR;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_INITIATOR_ADRES;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_LINK;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_NUMMER;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_OMSCHRIJVING;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_REGISTRATIEDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_STARTDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_STATUS;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_STREEFDATUM;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_TOELICHTING;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_TYPE;
import static net.atos.zac.mailtemplates.model.MailTemplateVariabelen.ZAAK_URL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Optional;
import java.util.regex.Pattern;

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
import net.atos.zac.flowable.ZaakVariabelenService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.mailtemplates.model.MailLink;
import net.atos.zac.mailtemplates.model.MailTemplateVariabelen;

public class MailTemplateHelper {

    public static final Pattern PTAGS = Pattern.compile("</?p>", Pattern.CASE_INSENSITIVE);

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
    private ZaakVariabelenService zaakVariabelenService;

    public static String stripParagraphTags(final String onderwerp) {
        // Can't parse HTML with a regular expression, but in this case there will only be bare P-tags.
        return PTAGS.matcher(onderwerp).replaceAll(StringUtils.EMPTY);
    }

    public String resolveVariabelen(final String tekst, final Zaak zaak) {
        String resolvedTekst = tekst;
        if (zaak != null) {
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_NUMMER, zaak.getIdentificatie());

            final MailLink link = getLink(zaak);
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_URL, link.url);
            resolvedTekst = replaceVariabeleHtml(resolvedTekst, ZAAK_LINK, link.toHtml());

            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_OMSCHRIJVING, zaak.getOmschrijving());
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_TOELICHTING, zaak.getToelichting());
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_REGISTRATIEDATUM, zaak.getRegistratiedatum());
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_STARTDATUM, zaak.getStartdatum());
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_STREEFDATUM, zaak.getEinddatumGepland());
            resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_FATALEDATUM, zaak.getUiterlijkeEinddatumAfdoening());

            if (resolvedTekst.contains(ZAAK_STATUS.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_STATUS,
                                                 Optional.of(zaak.getStatus())
                                                         .map(zrcClientService::readStatus)
                                                         .map(Status::getStatustype)
                                                         .map(ztcClientService::readStatustype)
                                                         .map(Statustype::getOmschrijving)
                );
            }

            if (resolvedTekst.contains(ZAAK_TYPE.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_TYPE,
                                                 Optional.of(zaak.getZaaktype())
                                                         .map(ztcClientService::readZaaktype)
                                                         .map(Zaaktype::getOmschrijving));
            }

            if (resolvedTekst.contains(ZAAK_INITIATOR.getVariabele()) ||
                    resolvedTekst.contains(ZAAK_INITIATOR_ADRES.getVariabele())) {
                resolvedTekst = replaceInitiatorVariabelen(resolvedTekst,
                                                           zgwApiService.findInitiatorForZaak(zaak));
            }

            if (resolvedTekst.contains(ZAAK_BEHANDELAAR_GROEP.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_BEHANDELAAR_GROEP,
                                                 zgwApiService.findGroepForZaak(zaak)
                                                         .map(RolOrganisatorischeEenheid::getNaam));
            }

            if (resolvedTekst.contains(ZAAK_BEHANDELAAR_MEDEWERKER.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, ZAAK_BEHANDELAAR_MEDEWERKER,
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
            resolvedTekst = replaceVariabele(resolvedTekst, TAAK_URL, link.url);
            resolvedTekst = replaceVariabeleHtml(resolvedTekst, TAAK_LINK, link.toHtml());

            if (resolvedTekst.contains(TAAK_BEHANDELAAR_GROEP.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, TAAK_BEHANDELAAR_GROEP,
                                                 taskInfo.getIdentityLinks().stream()
                                                         .filter(identityLinkInfo ->
                                                                         IdentityLinkType.CANDIDATE.equals(
                                                                                 identityLinkInfo.getType()))
                                                         .findAny()
                                                         .map(IdentityLinkInfo::getGroupId)
                                                         .map(identityService::readGroup)
                                                         .map(Group::getName));
            }

            if (resolvedTekst.contains(TAAK_BEHANDELAAR_MEDEWERKER.getVariabele())) {
                resolvedTekst = replaceVariabele(resolvedTekst, TAAK_BEHANDELAAR_MEDEWERKER,
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
            resolvedTekst = replaceVariabele(resolvedTekst, DOCUMENT_TITEL, document.getTitel());

            final MailLink link = getLink(document);
            resolvedTekst = replaceVariabele(resolvedTekst, DOCUMENT_URL, link.url);
            resolvedTekst = replaceVariabeleHtml(resolvedTekst, DOCUMENT_LINK, link.toHtml());
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
        final String zaakIdentificatie = zaakVariabelenService.readZaakIdentificatie(taskInfo.getScopeId());
        final String zaaktypeOmschrijving = zaakVariabelenService.readZaaktypeOmschrijving(taskInfo.getScopeId());
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
                                                 ZAAK_INITIATOR,
                                                 naam),
                                ZAAK_INITIATOR_ADRES,
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
