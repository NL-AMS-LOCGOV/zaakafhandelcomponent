/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering;

import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.User;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringSubjectField;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringVerzendInfo;
import net.atos.zac.signalering.model.SignaleringVerzonden;
import net.atos.zac.signalering.model.SignaleringVerzondenZoekParameters;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.DatumRange;
import net.atos.zac.zoeken.model.DatumVeld;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@ApplicationScoped
@Transactional
public class SignaleringenJob {

    private static final Logger LOG = Logger.getLogger(SignaleringenJob.class.getName());

    public static final String ZAAK_SIGNALERINGEN_VERZENDEN = "Zaak signaleringen verzenden";

    public static final String ZAAK_AFGEHANDELD_QUERY = "zaak_afgehandeld";

    @Inject
    private SignaleringenService signaleringenService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private IdentityService identityService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private ZoekenService zoekenService;

    // TODO #1080 asynchroon uitvoeren
    public void zaakSignaleringenVerzenden() {
        final SignaleringVerzendInfo info = new SignaleringVerzendInfo();
        LOG.info(String.format("%s: gestart", ZAAK_SIGNALERINGEN_VERZENDEN));
        ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI())
                .forEach(zaaktype -> {
                    final UUID zaaktypeUUID = UriUtil.uuidFromURI(zaaktype.getUrl());
                    final ZaakafhandelParameters parameters = zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaaktypeUUID);
                    if (parameters.getEinddatumGeplandWaarschuwing() != null) {
                        info.einddatumGeplandVerzonden +=
                                zaakEinddatumGeplandVerzenden(zaaktype, parameters.getEinddatumGeplandWaarschuwing());
                        zaakEinddatumGeplandVerzendenOpruimen(zaaktype, parameters.getEinddatumGeplandWaarschuwing());
                    }
                    info.uiterlijkeEinddatumAfdoeningVerzonden +=
                            zaakUiterlijkeEinddatumAfdoeningVerzenden(zaaktype, parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
                    zaakUiterlijkeEinddatumAfdoeningVerzendenOpruimen(zaaktype, parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
                });
        LOG.info(String.format("%s: gestopt (%d streefdatum waarschuwingen, %d fatale datum waarschuwingen)",
                               ZAAK_SIGNALERINGEN_VERZENDEN,
                               info.einddatumGeplandVerzonden,
                               info.uiterlijkeEinddatumAfdoeningVerzonden));
    }

    private int zaakEinddatumGeplandVerzenden(final Zaaktype zaaktype, final int venster) {
        final int[] verzonden = new int[1];
        zoekenService.zoekZaak(getZaakSignaleringVerzendenZoekParameters(DatumVeld.ZAAK_EINDDATUM_GEPLAND, zaaktype, venster)).getItems()
                .forEach(zaak -> {
                    final User target = getZaakSignaleringTarget(zaak.getBehandelaarGebruikersnaam(), zaak.getUuid(), SignaleringSubjectField.DUE);
                    if (target != null) {
                        final Signalering signalering = buildZaakSignalering(target, zaak);
                        signaleringenService.sendSignalering(signalering, getZaakSignaleringBericht(SignaleringSubjectField.DUE));
                        markeerSignaleringAlsVerzonden(signalering, SignaleringSubjectField.DUE);
                        verzonden[0]++;
                    }
                });
        return verzonden[0];
    }

    private int zaakUiterlijkeEinddatumAfdoeningVerzenden(final Zaaktype zaaktype, final int venster) {
        final int[] verzonden = new int[1];
        zoekenService.zoekZaak(getZaakSignaleringVerzendenZoekParameters(DatumVeld.ZAAK_UITERLIJKE_EINDDATUM_AFDOENING, zaaktype, venster)).getItems()
                .forEach(zaak -> {
                    final User target = getZaakSignaleringTarget(zaak.getBehandelaarGebruikersnaam(), zaak.getUuid(), SignaleringSubjectField.FATAL);
                    if (target != null) {
                        final Signalering signalering = buildZaakSignalering(target, zaak);
                        signaleringenService.sendSignalering(signalering, getZaakSignaleringBericht(SignaleringSubjectField.FATAL));
                        markeerSignaleringAlsVerzonden(signalering, SignaleringSubjectField.FATAL);
                        verzonden[0]++;
                    }
                });
        return verzonden[0];
    }

    private ZoekParameters getZaakSignaleringVerzendenZoekParameters(final DatumVeld veld, final Zaaktype zaaktype, final int venster) {
        final LocalDate now = LocalDate.now();
        final ZoekParameters parameters = getOpenZaakZoekParameters(zaaktype);
        parameters.addDatum(veld, new DatumRange(now, now.plusDays(venster)));
        return parameters;
    }

    private User getZaakSignaleringTarget(final String behandelaarGebruikersnaam, final String zaakUUID, final SignaleringSubjectField field) {
        if (behandelaarGebruikersnaam != null) {
            final User user = identityService.readUser(behandelaarGebruikersnaam);
            if (signaleringenService.readInstellingen(SignaleringType.Type.ZAAK_VERLOPEND, user).isMail() &&
                    signaleringenService.findSignaleringVerzonden(new SignaleringVerzondenZoekParameters(user)
                                                                          .types(SignaleringType.Type.ZAAK_VERLOPEND)
                                                                          .subjectZaak(UUID.fromString(zaakUUID))
                                                                          .field(field)).isEmpty()) {
                return user;
            }
        }
        return null;
    }

    private Signalering buildZaakSignalering(final User target, final ZaakZoekObject zoekObject) {
        final Zaak zaak = new Zaak();
        zaak.setUuid(UUID.fromString(zoekObject.getUuid()));
        final Signalering signalering = signaleringenService.signaleringInstance(SignaleringType.Type.ZAAK_VERLOPEND);
        signalering.setTarget(target);
        signalering.setSubject(zaak);
        return signalering;
    }

    private String getZaakSignaleringBericht(final SignaleringSubjectField field) {
        return SignaleringType.Type.ZAAK_VERLOPEND.getBericht().split(";")[field.ordinal()];
    }

    private void markeerSignaleringAlsVerzonden(final Signalering signalering, final SignaleringSubjectField field) {
        final SignaleringVerzonden signaleringVerzonden = signaleringenService.signaleringVerzondenInstance(signalering);
        signaleringVerzonden.setSubjectfield(field);
        signaleringenService.createSignaleringVerzonden(signaleringVerzonden);
    }

    private void zaakEinddatumGeplandVerzendenOpruimen(final Zaaktype zaaktype, final int venster) {
        zoekenService.zoekZaak(getZaakSignaleringVerzondenZoekParameters(DatumVeld.ZAAK_EINDDATUM_GEPLAND, zaaktype, venster)).getItems()
                .forEach(zaak -> {
                    // TODO #1065 Verwijder de onterecht verzonden streefsignalering markeringen
                });
    }

    private void zaakUiterlijkeEinddatumAfdoeningVerzendenOpruimen(final Zaaktype zaaktype, final int venster) {
        zoekenService.zoekZaak(getZaakSignaleringVerzondenZoekParameters(DatumVeld.ZAAK_UITERLIJKE_EINDDATUM_AFDOENING, zaaktype, venster)).getItems()
                .forEach(zaak -> {
                    // TODO #1065 Verwijder de onterecht verzonden fataalsignalering markeringen
                });
    }

    private ZoekParameters getZaakSignaleringVerzondenZoekParameters(final DatumVeld veld, final Zaaktype zaaktype, final int venster) {
        final LocalDate now = LocalDate.now();
        final ZoekParameters parameters = getOpenZaakZoekParameters(zaaktype);
        parameters.addDatum(veld, new DatumRange(now.plusDays(venster + 1), null));
        return parameters;
    }

    private ZoekParameters getOpenZaakZoekParameters(final Zaaktype zaaktype) {
        final ZoekParameters parameters = new ZoekParameters(ZoekObjectType.ZAAK);
        parameters.addFilter(FilterVeld.ZAAK_ZAAKTYPE_UUID, UriUtil.uuidFromURI(zaaktype.getUrl()).toString());
        parameters.addFilterQuery(ZAAK_AFGEHANDELD_QUERY, "false");
        parameters.setRows(Integer.MAX_VALUE);
        return parameters;
    }
}
