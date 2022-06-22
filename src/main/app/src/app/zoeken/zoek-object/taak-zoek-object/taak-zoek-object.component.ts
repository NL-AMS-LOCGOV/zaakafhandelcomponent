/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {MatSidenav} from '@angular/material/sidenav';
import {ZoekObjectComponent} from '../zoek-object/zoek-object-component';
import {TaakZoekObject} from '../../model/taken/taak-zoek-object';

@Component({
    selector: 'zac-taak-zoek-object',
    styleUrls: ['./taak-zoek-object.component.less'],
    templateUrl: './taak-zoek-object.component.html'
})
export class TaakZoekObjectComponent extends ZoekObjectComponent {

    @Input() taak: TaakZoekObject;
    @Input() sideNav: MatSidenav;

    constructor(private router: Router) {
        super();
    }

    open(taak: TaakZoekObject): void {
        super._open();
        this.router.navigate(['/taken/', taak.identificatie]);
    }
}
