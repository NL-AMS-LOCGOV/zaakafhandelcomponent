/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {DatumVeld} from './datum-veld';
import {ZoekVeld} from './zoek-veld';
import {FilterVeld} from './filter-veld';
import {SorteerVeld} from './sorteer-veld';
import {ZoekObjectType} from './zoek-object-type';

export class ZoekParameters {
    type: ZoekObjectType;
    alleenMijnZaken: boolean = false;
    alleenOpenstaandeZaken: boolean = false;
    alleenAfgeslotenZaken: boolean = false;
    alleenMijnTaken: boolean = false;
    zoeken: Partial<Record<ZoekVeld, string>> = {};
    filters: Partial<Record<FilterVeld, string[]>> = {};
    datums: Partial<Record<DatumVeld, { van: Date; tot: Date }>> = {
        STARTDATUM: {van: null, tot: null},
        STREEFDATUM: {van: null, tot: null},
        ZAAK_EINDDATUM: {van: null, tot: null},
        ZAAK_UITERLIJKE_EINDDATUM_AFDOENING: {van: null, tot: null},
        TAAK_TOEKENNINGSDATUM: {van: null, tot: null}
    };
    sorteerVeld: SorteerVeld;
    sorteerRichting: 'desc' | 'asc' | '';
    rows: number = 25;
    page: number = 0;
}
