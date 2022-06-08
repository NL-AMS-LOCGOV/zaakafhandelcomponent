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
        ZAAK_EINDDATUM_GEPLAND: {
            van: null,
            tot: null
        },
        ZAAK_UITERLIJKE_EINDDATUM_AFDOENING: {
            van: null,
            tot: null
        }
    };
    sorteerVeld: string = 'IDENTIFICATIE';
    sorteerRichting: 'desc' | 'asc' | '';
    rows: number = 25;
    page: number = 0;
}
