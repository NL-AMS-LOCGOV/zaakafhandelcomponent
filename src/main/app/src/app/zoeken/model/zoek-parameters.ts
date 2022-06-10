/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SorteerVeld} from './sorteer-veld';

export class ZoekParameters {
    alleenMijnZaken: boolean = false;
    alleenOpenstaandeZaken: boolean = false;
    alleenAfgeslotenZaken: boolean = false;
    zoeken: { [key: string]: string } = {};
    filters: { [key: string]: string } = {};
    datums: { [key: string]: { van: string; tot: string } } = {
        ZAAK_STARTDATUM: {van: null, tot: null},
        ZAAK_EINDDATUM: {van: null, tot: null},
        ZAAK_EINDDATUM_GEPLAND: {van: null, tot: null},
        ZAAK_UITERLIJKE_EINDDATUM_AFDOENING: {van: null, tot: null}
    };
    sorteerVeld: string = SorteerVeld.IDENTIFICATIE;
    sorteerRichting: 'desc' | 'asc' | '';
    rows: number = 25;
    page: number = 0;
}
