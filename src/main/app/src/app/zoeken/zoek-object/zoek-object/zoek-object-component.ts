/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {TextIcon} from '../../../shared/edit/text-icon';
import {Conditionals} from '../../../shared/edit/conditional-fn';

@Component({template: ''})
export abstract class ZoekObjectComponent {

    abstract sideNav: MatSidenav;
    viewIcon = new TextIcon(Conditionals.always, 'visibility', 'visibility_icon', '', 'pointer');

    protected constructor() {
    }

    protected _open(): void {
        this.sideNav.close();
    }

}
