/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class EnkelvoudigInformatieobjectActies {

    private final boolean lezen;

    private final boolean verwijderen;

    // (ont)koppelen of verplaatsen van het enkelvoudig informatieobject
    private final boolean koppelen;

    private final boolean downloaden;

    private final boolean toevoegenNieuweVersie;

    private final boolean bewerken;

    private final boolean vergrendelen;

    private final boolean ontgrendelen;

    private final boolean ondertekenen;

    @JsonbCreator
    public EnkelvoudigInformatieobjectActies(@JsonbProperty("lezen") final boolean lezen,
            @JsonbProperty("verwijderen") final boolean verwijderen,
            @JsonbProperty("koppelen") final boolean koppelen,
            @JsonbProperty("downloaden") final boolean downloaden,
            @JsonbProperty("toevoegen_nieuwe_versie") final boolean toevoegenNieuweVersie,
            @JsonbProperty("bewerken") final boolean bewerken,
            @JsonbProperty("vergrendelen") final boolean vergrendelen,
            @JsonbProperty("ontgrendelen") final boolean ontgrendelen,
            @JsonbProperty("ondertekenen") final boolean ondertekenen) {
        this.lezen = lezen;
        this.verwijderen = verwijderen;
        this.koppelen = koppelen;
        this.downloaden = downloaden;
        this.toevoegenNieuweVersie = toevoegenNieuweVersie;
        this.bewerken = bewerken;
        this.vergrendelen = vergrendelen;
        this.ontgrendelen = ontgrendelen;
        this.ondertekenen = ondertekenen;
    }

    public boolean getLezen() {
        return lezen;
    }

    public boolean getVerwijderen() {
        return verwijderen;
    }

    public boolean getKoppelen() {
        return koppelen;
    }

    public boolean getDownloaden() {
        return downloaden;
    }

    public boolean getToevoegenNieuweVersie() {
        return toevoegenNieuweVersie;
    }

    public boolean getBewerken() {
        return bewerken;
    }

    public boolean getVergrendelen() {
        return vergrendelen;
    }

    public boolean getOntgrendelen() {
        return ontgrendelen;
    }

    public boolean getOndertekenen() {
        return ondertekenen;
    }
}
