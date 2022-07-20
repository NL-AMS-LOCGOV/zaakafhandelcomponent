/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Klant} from '../../model/klant';
import {SelectFormFieldBuilder} from '../../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Validators} from '@angular/forms';
import {SelectFormField} from '../../../shared/material-form-builder/form-components/select/select-form-field';
import {ZaakActies} from '../../../zaken/model/zaak-acties';

@Component({
    selector: 'zac-klant-zoek',
    templateUrl: './klant-zoek.component.html',
    styleUrls: ['./klant-zoek.component.less']
})
export class KlantZoekComponent implements OnInit {
    @Input() initiator: boolean;
    @Input() acties: ZaakActies;
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

    klantGeselecteerd(klant: Klant): void {
        if (this.initiator) {
            this.klant.emit(klant);
        } else {
            // TODO #1325
        }
    }
}
