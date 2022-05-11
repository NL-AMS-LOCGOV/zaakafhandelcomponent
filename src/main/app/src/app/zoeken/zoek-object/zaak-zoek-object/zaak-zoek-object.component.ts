/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';

import {ZaakZoekObject} from '../../model/zaken/zaak-zoek-object';

@Component({
    selector: 'zac-zaak-zoek-object',
    templateUrl: './zaak-zoek-object.component.html'
})
export class ZaakZoekObjectComponent {

    @Input() zaak: ZaakZoekObject;

    constructor() {}

}
