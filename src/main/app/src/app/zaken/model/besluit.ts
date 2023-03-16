/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Besluittype} from './besluittype';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';

export class Besluit {
    url: string;
    uuid: string;
    identificatie: string;
    toelichting: string;
    datum: string;
    ingangsdatum: string;
    vervaldatum: string;
    vervalreden: string;
    besluittype: Besluittype;
    informatieobjecten: EnkelvoudigInformatieobject[];
}
