/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';

import {ZaakZoekObject} from '../../model/zaken/zaak-zoek-object';
import {Router} from '@angular/router';
import {MatSidenav} from '@angular/material/sidenav';
import {ZoekObjectComponent} from '../zoek-object/zoek-object-component';

@Component({
    selector: 'zac-zaak-zoek-object',
    styleUrls: ['./zaak-zoek-object.component.less'],
    templateUrl: './zaak-zoek-object.component.html'
})
export class ZaakZoekObjectComponent extends ZoekObjectComponent {

    @Input() zaak: ZaakZoekObject;
    @Input() sideNav: MatSidenav;

    constructor(private router: Router) {
        super();
    }

    open(zaak: ZaakZoekObject): void {
        super._open();
        this.router.navigate(['/zaken/', zaak.identificatie]);
    }
}
