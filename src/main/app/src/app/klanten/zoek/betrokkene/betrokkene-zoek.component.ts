/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Klant} from '../../model/klant';
import {SelectFormFieldBuilder} from '../../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Validators} from '@angular/forms';
import {SelectFormField} from '../../../shared/material-form-builder/form-components/select/select-form-field';

@Component({
    selector: 'zac-betrokkene-zoek',
    templateUrl: './betrokkene-zoek.component.html',
    styleUrls: ['./betrokkene-zoek.component.less']
})
export class BetrokkeneZoekComponent implements OnInit {
    @Input() initiator: boolean;
    @Output() klant = new EventEmitter<Klant>();
    betrokkeneType: SelectFormField;

    constructor() {
    }

    ngOnInit(): void {
        this.betrokkeneType = new SelectFormFieldBuilder().id('betrokkeneType')
                                                          .label('betrokkeneType')
                                                          .optionLabel('naam')
                                                          .options(['TODO'])
                                                          .validators(Validators.required)
                                                          .build();

    }

    betrokkeneGeselecteerd(betrokkene: Klant): void {
        if (this.initiator) {
            this.klant.emit(betrokkene);
        } else {
            // TODO
        }
    }
}
