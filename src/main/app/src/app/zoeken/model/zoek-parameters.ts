/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class ZoekParameters {
    zoeken: { [key: string]: string } = {};
    filters: { [key: string]: string } = {};
    filterQueries: { [key: string]: string } = {};
    datums: { [key: string]: { van: any; tot: any } } = {
        ZAAK_STARTDATUM: {
            van: null,
            tot: null
        },
        ZAAK_EINDDATUMGEPLAND: {
            van: null,
            tot: null
        },
        ZAAK_UITERLIJKEEINDDATUMAFDOENING: {
            van: null,
            tot: null
        }
    };
    sorteerVeld: string;
    sorteerRichting: string;
    rows: number;
    start: number;
}
