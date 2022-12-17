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


import javax.json.bind.annotation.JsonbProperty;


public class FieldValidationError {

    /**
     * Naam van het veld met ongeldige gegevens
     **/
    @JsonbProperty("name")
    private String name;

    /**
     * Systeemcode die het type fout aangeeft
     **/
    @JsonbProperty("code")
    private String code;

    /**
     * Uitleg wat er precies fout is met de gegevens
     **/
    @JsonbProperty("reason")
    private String reason;

    /**
     * Naam van het veld met ongeldige gegevens
     * @return name
     **/
    public String getName() {
        return name;
    }

    /**
     * Set name
     **/
    public void setName(String name) {
        this.name = name;
    }

    public FieldValidationError name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Systeemcode die het type fout aangeeft
     * @return code
     **/
    public String getCode() {
        return code;
    }

    /**
     * Set code
     **/
    public void setCode(String code) {
        this.code = code;
    }

    public FieldValidationError code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Uitleg wat er precies fout is met de gegevens
     * @return reason
     **/
    public String getReason() {
        return reason;
    }

    /**
     * Set reason
     **/
    public void setReason(String reason) {
        this.reason = reason;
    }

    public FieldValidationError reason(String reason) {
        this.reason = reason;
        return this;
    }


    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FieldValidationError {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
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

