/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {MatSidenav} from '@angular/material/sidenav';
import {ZoekObject} from '../../model/zoek-object';
import {ZoekObjectComponent} from '../zoek-object/zoek-object-component';

@Component({
    selector: 'zac-document-zoek-object',
    templateUrl: './document-zoek-object.component.html'
})
export class DocumentZoekObjectComponent extends ZoekObjectComponent {

    @Input() infoObject: ZoekObject;
    @Input() sideNav: MatSidenav;

    constructor(private router: Router) {
        super();
    }

    openInfoObject(): void {
        super._open();
        this.router.navigate(['/informatie-objecten/', this.infoObject.uuid]);
    }
}
