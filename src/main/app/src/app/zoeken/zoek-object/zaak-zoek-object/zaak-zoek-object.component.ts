/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';

import {ZaakZoekObject} from '../../model/zaken/zaak-zoek-object';
import {TextIcon} from '../../../shared/edit/text-icon';
import {Conditionals} from '../../../shared/edit/conditional-fn';
import {Router} from '@angular/router';
import {MatSidenav} from '@angular/material/sidenav';

@Component({
    selector: 'zac-zaak-zoek-object',
    styleUrls: ['./zoek-zaak-object.component.less'],
    templateUrl: './zaak-zoek-object.component.html'
})
export class ZaakZoekObjectComponent {

    @Input() zaak: ZaakZoekObject;
    @Input() sideNav: MatSidenav;

    viewIcon = new TextIcon(Conditionals.always, 'visibility', 'visibility_icon', '', 'pointer');

    constructor(private router: Router) {}

    openZaak(zaak: ZaakZoekObject): void {
        this.sideNav.close();
        this.router.navigate(['/zaken/', zaak.identificatie]);
    }
}
