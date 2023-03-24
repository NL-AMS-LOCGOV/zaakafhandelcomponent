package net.atos.zac.shared.helper;

import static net.atos.client.zgw.ztc.model.Statustype.isHeropend;
import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Opschorting;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.zac.flowable.ZaakVariabelenService;
import net.atos.zac.policy.PolicyService;

public class OpschortenZaakHelper {

    private static final String OPSCHORTING = "Opschorting";

    private static final String HERVATTING = "Hervatting";


    @Inject
    private PolicyService policyService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZaakVariabelenService zaakVariabelenService;

    public Zaak opschortenZaak(Zaak zaak, final long aantalDagen, final String redenOpschorting) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final UUID zaakUUID = zaak.getUuid();
        final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
        final Statustype statustype = status != null ? ztcClientService.readStatustype(status.getStatustype()) : null;
        assertPolicy(zaak.isOpen() && !isHeropend(statustype) && !zaak.isOpgeschort() && StringUtils.isEmpty(zaak.getOpschorting().getReden()));
        final String toelichting = String.format("%s: %s", OPSCHORTING, redenOpschorting);
        final LocalDate einddatumGepland = zaak.getEinddatumGepland().plusDays(aantalDagen);
        final LocalDate uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening().plusDays(aantalDagen);
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID,
                                                            toPatch(einddatumGepland, uiterlijkeEinddatumAfdoening, redenOpschorting, true),
                                                            toelichting);
        zaakVariabelenService.setDatumtijdOpgeschort(zaakUUID, ZonedDateTime.now());
        zaakVariabelenService.setVerwachteDagenOpgeschort(zaakUUID, Math.toIntExact(aantalDagen));
        return updatedZaak;
    }

    public Zaak hervattenZaak(final Zaak zaak, final String redenHervatting) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        assertPolicy(zaak.isOpgeschort());
        final UUID zaakUUID = zaak.getUuid();
        final ZonedDateTime datumOpgeschort =
                zaakVariabelenService.findDatumtijdOpgeschort(zaak.getUuid()).orElseGet(() -> ZonedDateTime.now());
        final int verwachteDagenOpgeschort = zaakVariabelenService.findVerwachteDagenOpgeschort(zaak.getUuid())
                .orElse(0);
        final long dagenVerschil = ChronoUnit.DAYS.between(datumOpgeschort, ZonedDateTime.now());
        final long offset = dagenVerschil - verwachteDagenOpgeschort;
        final LocalDate einddatumGepland = zaak.getEinddatumGepland().plusDays(offset);
        final LocalDate uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening().plusDays(offset);

        final String toelichting = String.format("%s: %s", HERVATTING, redenHervatting);
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID,
                                                            toPatch(einddatumGepland, uiterlijkeEinddatumAfdoening,
                                                                    redenHervatting, false),
                                                            toelichting);
        zaakVariabelenService.removeDatumtijdOpgeschort(zaakUUID);
        zaakVariabelenService.removeVerwachteDagenOpgeschort(zaakUUID);
        return updatedZaak;
    }


    /**
     * @param einddatumGepland             streefdatum van de zaak
     * @param uiterlijkeEinddatumAfdoening fataledatum van de zaak
     * @param reden                        reden voor de opschorting
     * @param opschorting                  true indien opschorten, false indien hervatten
     * @return zaak voor patch
     */
    public Zaak toPatch(final LocalDate einddatumGepland, final LocalDate uiterlijkeEinddatumAfdoening, final String reden, final boolean opschorting) {
        final Zaak zaak = new Zaak();
        zaak.setEinddatumGepland(einddatumGepland);
        zaak.setUiterlijkeEinddatumAfdoening(uiterlijkeEinddatumAfdoening);
        zaak.setOpschorting(new Opschorting(opschorting, reden));
        return zaak;
    }

}
