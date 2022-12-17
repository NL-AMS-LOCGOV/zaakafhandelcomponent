/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * Klanten API
 * Een API om klanten te benaderen.  Een API om zowel klanten te registreren als op te vragen. Een klant is een natuurlijk persoon, niet-natuurlijk persoon (bedrijf) of vestiging waarbij het gaat om niet geverifieerde gegevens. De Klanten API kan zelfstandig of met andere API's samen werken om tot volledige functionaliteit te komen.  **Afhankelijkheden**  Deze API is afhankelijk van:  * Autorisaties API * Notificaties API * Zaken API *(optioneel)* * Documenten API *(optioneel)*  **Autorisatie**  Deze API vereist autorisatie. Je kan de [token-tool](https://zaken-auth.vng.cloud/) gebruiken om JWT-tokens te genereren.  ** Notificaties  Deze API publiceert notificaties op het kanaal `klanten`.  **Main resource**  `klant`    **Kenmerken**  * `subject_type`: Type van de `subject`.  **Resources en acties**   **Handige links**  * [Documentatie](https://zaakgerichtwerken.vng.cloud/standaard) * [Zaakgericht werken](https://zaakgerichtwerken.vng.cloud)
 * <p>
 * The version of the OpenAPI document: 1.0.0
 * Contact: standaarden.ondersteuning@vng.nl
 * <p>
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.klanten.model.generated;

import java.net.URI;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbCreator;


public class Vestiging extends Klant {

    @JsonbProperty("subjectIdentificatie")
    private Vestiging subjectIdentificatie;

    public Vestiging() {
    }

    @JsonbCreator
    public Vestiging(
            @JsonbProperty(value = "url", nillable = true) URI url
    ) {
        this.url = url;
    }

    /**
     * Get subjectIdentificatie
     * @return subjectIdentificatie
     **/
    public Vestiging getSubjectIdentificatie() {
        return subjectIdentificatie;
    }

    /**
     * Set subjectIdentificatie
     **/
    public void setSubjectIdentificatie(Vestiging subjectIdentificatie) {
        this.subjectIdentificatie = subjectIdentificatie;
    }

    public Vestiging subjectIdentificatie(Vestiging subjectIdentificatie) {
        this.subjectIdentificatie = subjectIdentificatie;
        return this;
    }


    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Vestiging {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    subjectIdentificatie: ").append(toIndentedString(subjectIdentificatie)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

