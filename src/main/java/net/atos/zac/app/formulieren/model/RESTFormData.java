package net.atos.zac.app.formulieren.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.zac.util.LocalDateAdapter;

public class RESTFormData {

    private static final String ZAAK_OPSCHORTEN = "zaak-opschorten";

    private static final String ZAAK_HERVATTEN = "zaak-hervatten";

    private static final String TAAK_FATALE_DATUM = "taak-fatale-datum";

    private static final String TAAK_TOEKENNEN_GROEP = "taak-toekennen-groep";

    private static final String TAAK_TOEKENNEN_MEDEWERKER = "taak-toekennen-behandelaar";

    private static final String DOCUMENTEN_VERZENDEN = "documenten-verzenden";

    private static final String DOCUMENTEN_VERZENDEN_DATUM = "documenten-verzenden-datum";

    private static final String DOCUMENTEN_ONDERTEKENEN = "documenten-onderteken";

    private static final String MAIL_BIJLAGEN = "mail-bijlagen";

    private static final String TOELICHTING = "toelichting";

    public boolean zaakOpschorten;

    public boolean zaakHervatten;

    public LocalDate taakFataleDatum;

    public String taakToekennenGroep;

    public String taakToekennenMedewerker;

    public String toelichting;

    public String mailBijlagen;

    public LocalDate documentenVerzendenDatum;

    public String documentenVerzenden;

    public String documentenOndertekenen;

    public Map<String, String> dataElementen;

    public Map<String, String> formState;


    public RESTFormData(final Map<String, String> dataElementen) {
        this.formState = new HashMap<>(dataElementen);
        if (dataElementen.containsKey(ZAAK_OPSCHORTEN)) {
            if (BooleanUtils.TRUE.equals(dataElementen.get(ZAAK_OPSCHORTEN))) {
                this.zaakOpschorten = true;
            }
            dataElementen.remove(ZAAK_OPSCHORTEN);
        }
        if (dataElementen.containsKey(ZAAK_HERVATTEN)) {
            if (BooleanUtils.TRUE.equals(dataElementen.get(ZAAK_HERVATTEN))) {
                this.zaakHervatten = true;
            }
            dataElementen.remove(ZAAK_HERVATTEN);
        }
        if (dataElementen.containsKey(TAAK_FATALE_DATUM)) {
            this.taakFataleDatum = new LocalDateAdapter().adaptFromJson(dataElementen.get(TAAK_FATALE_DATUM));
            dataElementen.remove(TAAK_FATALE_DATUM);
        }

        if (dataElementen.containsKey(TOELICHTING)) {
            this.toelichting = dataElementen.get(TOELICHTING);
            dataElementen.remove(TOELICHTING);
        }
        if (dataElementen.containsKey(TAAK_TOEKENNEN_GROEP)) {
            this.taakToekennenGroep = dataElementen.get(TAAK_TOEKENNEN_GROEP);
            dataElementen.remove(TAAK_TOEKENNEN_GROEP);
        }
        if (dataElementen.containsKey(TAAK_TOEKENNEN_MEDEWERKER)) {
            this.taakToekennenMedewerker = dataElementen.get(TAAK_TOEKENNEN_MEDEWERKER);
            dataElementen.remove(TAAK_TOEKENNEN_MEDEWERKER);
        }
        if (dataElementen.containsKey(MAIL_BIJLAGEN)) {
            if (StringUtils.isNotBlank(dataElementen.get(MAIL_BIJLAGEN))) {
                this.mailBijlagen = dataElementen.get(MAIL_BIJLAGEN);
            }
            dataElementen.remove(MAIL_BIJLAGEN);
        }

        if (dataElementen.containsKey(DOCUMENTEN_VERZENDEN)) {
            if (StringUtils.isNotBlank(dataElementen.get(DOCUMENTEN_VERZENDEN))) {
                this.documentenVerzenden = dataElementen.get(DOCUMENTEN_VERZENDEN);
            }
            dataElementen.remove(DOCUMENTEN_VERZENDEN);
        }

        if (dataElementen.containsKey(DOCUMENTEN_VERZENDEN_DATUM)) {
            this.documentenVerzendenDatum = new LocalDateAdapter().adaptFromJson(
                    dataElementen.get(DOCUMENTEN_VERZENDEN_DATUM));
            dataElementen.remove(DOCUMENTEN_VERZENDEN_DATUM);
        } else {
            this.documentenVerzendenDatum = LocalDate.now();
        }

        if (dataElementen.containsKey(DOCUMENTEN_ONDERTEKENEN)) {
            if (StringUtils.isNotBlank(dataElementen.get(DOCUMENTEN_ONDERTEKENEN))) {
                this.documentenOndertekenen = dataElementen.get(DOCUMENTEN_ONDERTEKENEN);
            }
            dataElementen.remove(DOCUMENTEN_ONDERTEKENEN);
        }

        this.dataElementen = dataElementen;
    }

    public String substituteText(String tekst) {
        for (final Map.Entry<String, String> entry : this.formState.entrySet()) {
            final String search = "[%s]".formatted(entry.getKey());
            tekst = StringUtils.replace(tekst, search, entry.getValue());
        }
        return tekst;
    }

    public String substitute(String tekst) {
        for (final Map.Entry<String, String> entry : this.formState.entrySet()) {
            tekst = StringUtils.replace(tekst, entry.getKey(), entry.getValue());
        }
        return tekst;
    }

}
