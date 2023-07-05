/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export enum FormulierVeldtype {
    TEKST_VELD = 'TEKST_VELD',
    TEKST_VLAK = 'TEKST_VLAK',
    NUMMER = 'NUMMER',
    EMAIL = 'EMAIL',
    DATUM = 'DATUM',
    KEUZELIJST = 'KEUZELIJST',
    RADIO = 'RADIO',
    CHECKBOX = 'CHECKBOX',
    CHECKBOXES = 'CHECKBOXES',
    GROEP_KEUZELIJST = 'GROEP_KEUZELIJST',
    MEDEWERKER_KEUZELIJST = 'MEDEWERKER_KEUZELIJST',
    // MEDEWERKER_GROEP_KEUZELIJST ='MEDEWERKER_GROEP_KEUZELIJST', // TODO een lijst van medewerkers die afhankelijk is van de gekozen groep
    DOCUMENTEN_LIJST = 'DOCUMENTEN_LIJST',
    READONLY = 'READONLY',
}
