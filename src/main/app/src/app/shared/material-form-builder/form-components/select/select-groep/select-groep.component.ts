/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {SelectComponent} from '../select.component';
import {IdentityService} from '../../../../../identity/identity.service';
import {SelectGroepFormField} from './select-groep-form-field';

@Component({
    templateUrl: '../select.component.html',
    styleUrls: ['../select.component.less']
})
export class SelectGroepComponent extends SelectComponent implements OnInit {

    data: SelectGroepFormField;

    constructor(private identityService: IdentityService) {
        super();
    }

    ngOnInit(): void {
        this.identityService.getGroepen().subscribe(groepen => {
            this.data.options = groepen;
        });
    }

}
