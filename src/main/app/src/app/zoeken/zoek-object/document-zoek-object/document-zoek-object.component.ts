/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {MatSidenav} from '@angular/material/sidenav';
import {ZoekObjectComponent} from '../zoek-object/zoek-object-component';
import {DocumentZoekObject} from '../../model/documenten/document-zoek-object';

@Component({
    selector: 'zac-document-zoek-object',
    styleUrls: ['./document-zoek-object.component.less'],
    templateUrl: './document-zoek-object.component.html'
})
export class DocumentZoekObjectComponent extends ZoekObjectComponent {

    @Input() document: DocumentZoekObject;
    @Input() sideNav: MatSidenav;

    constructor(private router: Router) {
        super();
    }

    open(document: DocumentZoekObject): void {
        super._open();
        this.router.navigate(['/informatie-objecten/', document.id]);
    }
}
