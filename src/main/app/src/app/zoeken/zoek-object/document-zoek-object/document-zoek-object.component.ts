/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {TextIcon} from '../../../shared/edit/text-icon';
import {Conditionals} from '../../../shared/edit/conditional-fn';
import {Router} from '@angular/router';
import {MatSidenav} from '@angular/material/sidenav';
import {ZoekObject} from '../../model/zoek-object';

@Component({
    selector: 'zac-document-zoek-object',
    templateUrl: './document-zoek-object.component.html'
})
export class DocumentZoekObjectComponent {

    @Input() infoObject: ZoekObject;
    @Input() sideNav: MatSidenav;

    viewIcon = new TextIcon(Conditionals.always, 'visibility', 'visibility_icon', '', 'pointer');

    constructor(private router: Router) {}

    openInfoObject(): void {
        this.sideNav.close();
        this.router.navigate(['/informatie-objecten/', this.infoObject.uuid]);
    }
}
