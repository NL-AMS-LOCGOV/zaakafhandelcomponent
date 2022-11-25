/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';

@Component({
    selector: 'zac-informatie-object-indicaties',
    templateUrl: './informatie-object-indicaties.component.html',
    styleUrls: ['./informatie-object-indicaties.component.less']
})
export class InformatieObjectIndicatiesComponent {

    @Input() infoObject: EnkelvoudigInformatieobject;
    @Input() lijst: boolean;

    constructor() {}
}
