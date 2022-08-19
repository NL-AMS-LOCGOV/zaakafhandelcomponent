/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Klant} from '../../model/klanten/klant';
import {SelectFormFieldBuilder} from '../../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Validators} from '@angular/forms';
import {SelectFormField} from '../../../shared/material-form-builder/form-components/select/select-form-field';
import {ZaakActies} from '../../../zaken/model/zaak-acties';
import {KlantenService} from '../../klanten.service';
import {KlantGegevens} from '../../model/klanten/klant-gegevens';
import {InputFormField} from '../../../shared/material-form-builder/form-components/input/input-form-field';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';

@Component({
    selector: 'zac-klant-zoek',
    templateUrl: './klant-zoek.component.html',
    styleUrls: ['./klant-zoek.component.less']
})
export class KlantZoekComponent implements OnInit {
    @Input() initiator: boolean;
    @Input() acties: ZaakActies;
    @Input() zaaktypeUUID: string;
    @Output() klantGegevens = new EventEmitter<KlantGegevens>();
    betrokkeneRoltype: SelectFormField;
    betrokkeneToelichting: InputFormField;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.betrokkeneRoltype = new SelectFormFieldBuilder().id('betrokkeneType')
                                                             .label('betrokkeneRoltype')
                                                             .optionLabel('naam')
                                                             .options(this.klantenService.listBetrokkeneRoltypen(this.zaaktypeUUID))
                                                             .validators(Validators.required)
                                                             .build();
        this.betrokkeneToelichting = new InputFormFieldBuilder().id('betrokkenToelichting')
                                                                .label('toelichting')
                                                                .validators(Validators.required)
                                                                .maxlength(75, true)
                                                                .build();
    }

    klantGeselecteerd(klant: Klant): void {
        const klantGegevens: KlantGegevens = new KlantGegevens(klant);
        if (!this.initiator) {
            klantGegevens.betrokkeneRoltype = this.betrokkeneRoltype.formControl.value;
            klantGegevens.betrokkeneToelichting = this.betrokkeneToelichting.formControl.value;
        }
        this.klantGegevens.emit(klantGegevens);
    }
}
