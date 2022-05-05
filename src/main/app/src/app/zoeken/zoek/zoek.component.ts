/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, OnInit, Output} from '@angular/core';

import {ZoekObject} from '../model/zoek-object';
import {ZoekenService} from '../zoeken.service';
import {ZoekParameters} from '../model/zoek-parameters';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {Results} from '../../shared/model/results';

@Component({
    selector: 'zac-zoeken',
    templateUrl: './zoek.component.html',
    styleUrls: ['./zoek.component.less']
})
export class ZoekComponent implements OnInit {

    @Output() zoekItem = new EventEmitter<ZoekObject>();

    zoekResultaat: Results<ZoekObject> = {count: 0, foutmelding: '', results: []};
    loading = false;
    public zoekFormField: AbstractFormField;

    constructor(private zoekService: ZoekenService) {
    }

    ngOnInit(): void {
        this.zoekFormField = new InputFormFieldBuilder().id('zoekVeld').label('zoekVeld').value('').build();
    }

    zoeken() {
        const zoekParameters: ZoekParameters = new ZoekParameters();
        zoekParameters.tekst = this.zoekFormField.formControl.value;
        this.loading = true;
        this.zoekResultaat.results = [];
        this.zoekService.list(zoekParameters).subscribe(resultaat => {
            this.zoekResultaat = resultaat;
            this.loading = false;
        });
    }

    select(zoekItem: ZoekObject): void {
        this.zoekItem.emit(zoekItem);
    }
}
