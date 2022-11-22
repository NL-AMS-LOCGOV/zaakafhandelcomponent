package net.atos.zac.healthcheck;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Afleidingswijze;
import net.atos.client.zgw.ztc.model.Besluittype;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.healthcheck.model.ZaaktypeInrichtingscheck;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

public class HealthCheckService {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterBeheerService;


    public boolean bestaatCommunicatiekanaalEformulier() {
        return vrlClientService.findCommunicatiekanaal(ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER).isPresent();
    }

    public ZaaktypeInrichtingscheck controleerZaaktype(final URI zaaktypeUrl) {
        ztcClientService.readCacheTime();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaaktypeUrl);
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaaktype.getUUID());
        final ZaaktypeInrichtingscheck zaaktypeInrichtingscheck = new ZaaktypeInrichtingscheck(zaaktype);
        zaaktypeInrichtingscheck.setZaakafhandelParametersValide(zaakafhandelParameters.isValide());
        controleerZaaktypeStatustypeInrichting(zaaktypeInrichtingscheck);
        controleerZaaktypeResultaattypeInrichting(zaaktypeInrichtingscheck);
        controleerZaaktypeBesluittypeInrichting(zaaktypeInrichtingscheck);
        controleerZaaktypeRoltypeInrichting(zaaktypeInrichtingscheck);
        controleerZaaktypeInformatieobjecttypeInrichting(zaaktypeInrichtingscheck);
        return zaaktypeInrichtingscheck;
    }

    private void controleerZaaktypeStatustypeInrichting(final ZaaktypeInrichtingscheck zaaktypeInrichtingscheck) {
        final List<Statustype> statustypes = ztcClientService.readStatustypen(zaaktypeInrichtingscheck.getZaaktype().getUrl());
        int afgerondVolgnummer = 0;
        int hoogsteVolgnummer = 0;
        for (Statustype statustype : statustypes) {
            if (statustype.getVolgnummer() > hoogsteVolgnummer) {
                hoogsteVolgnummer = statustype.getVolgnummer();
            }
            switch (statustype.getOmschrijving()) {
                case ConfiguratieService.STATUSTYPE_OMSCHRIJVING_INTAKE -> zaaktypeInrichtingscheck.setStatustypeIntakeAanwezig(true);
                case ConfiguratieService.STATUSTYPE_OMSCHRIJVING_IN_BEHANDELING -> zaaktypeInrichtingscheck.setStatustypeInBehandelingAanwezig(true);
                case ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND -> zaaktypeInrichtingscheck.setStatustypeHeropendAanwezig(true);
                case ConfiguratieService.STATUSTYPE_OMSCHRIJVING_AFGEROND -> {
                    afgerondVolgnummer = statustype.getVolgnummer();
                    zaaktypeInrichtingscheck.setStatustypeAfgerondAanwezig(true);
                }
            }
        }
        if (afgerondVolgnummer == hoogsteVolgnummer) {
            zaaktypeInrichtingscheck.setStatustypeAfgerondLaatsteVolgnummer(true);
        }
    }

    private void controleerZaaktypeResultaattypeInrichting(final ZaaktypeInrichtingscheck zaaktypeInrichtingscheck) {
        final List<Resultaattype> resultaattypes = ztcClientService.readResultaattypen(zaaktypeInrichtingscheck.getZaaktype().getUrl());
        if (CollectionUtils.isNotEmpty(resultaattypes)) {
            zaaktypeInrichtingscheck.setResultaattypeAanwezig(true);
            resultaattypes.forEach(resultaattype -> {
                final Afleidingswijze afleidingswijze = resultaattype.getBrondatumArchiefprocedure().getAfleidingswijze();
                if (Afleidingswijze.VERVALDATUM_BESLUIT.equals(afleidingswijze) || Afleidingswijze.INGANGSDATUM_BESLUIT.equals(afleidingswijze)) {
                    zaaktypeInrichtingscheck.addResultaattypesMetVerplichtBesluit(resultaattype.getOmschrijving());
                }
            });
        }
    }

    private void controleerZaaktypeBesluittypeInrichting(final ZaaktypeInrichtingscheck zaaktypeInrichtingscheck) {
        final List<Besluittype> besluittypes = ztcClientService.readBesluittypen(zaaktypeInrichtingscheck.getZaaktype().getUrl());
        if (CollectionUtils.isNotEmpty(besluittypes)) {
            zaaktypeInrichtingscheck.setBesluittypeAanwezig(true);
        }
    }

    private void controleerZaaktypeRoltypeInrichting(final ZaaktypeInrichtingscheck zaaktypeInrichtingscheck) {
        final List<Roltype> roltypes = ztcClientService.listRoltypen(zaaktypeInrichtingscheck.getZaaktype().getUrl());
        if (CollectionUtils.isNotEmpty(roltypes)) {
            roltypes.forEach(roltype -> {
                switch (roltype.getOmschrijvingGeneriek()) {
                    case ADVISEUR, MEDE_INITIATOR, BELANGHEBBENDE, BESLISSER, KLANTCONTACTER, ZAAKCOORDINATOR -> zaaktypeInrichtingscheck.setRolOverigeAanwezig(
                            true);
                    case BEHANDELAAR -> zaaktypeInrichtingscheck.setRolBehandelaarAanwezig(true);
                    case INITIATOR -> zaaktypeInrichtingscheck.setRolInitiatorAanwezig(true);
                }
            });
        }
    }

    private void controleerZaaktypeInformatieobjecttypeInrichting(final ZaaktypeInrichtingscheck zaaktypeInrichtingscheck) {
        final List<Informatieobjecttype> informatieobjecttypes = ztcClientService.readInformatieobjecttypen(zaaktypeInrichtingscheck.getZaaktype().getUrl());
        informatieobjecttypes.forEach(informatieobjecttype -> {
            if (informatieobjecttype.isNuGeldig() && ConfiguratieService.INFORMATIEOBJECTTYPE_OMSCHRIJVING_EMAIL.equals(
                    informatieobjecttype.getOmschrijving())) {
                zaaktypeInrichtingscheck.setInformatieobjecttypeEmailAanwezig(true);
            }
        });
    }
}
