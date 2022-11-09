/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';

@Component({
    selector: 'zac-multi-facet-filter',
    templateUrl: './multi-facet-filter.component.html',
    styleUrls: ['./multi-facet-filter.component.less']
})
export class MultiFacetFilterComponent implements OnInit {

    formGroup: FormGroup;
    @Input() selected: string[];
    @Input() opties: string[];
    @Input() label: string;
    @Output() changed = new EventEmitter<string[]>();

    /* veld: prefix */
    public VERTAALBAARE_FACETTEN = {
        TAAK_STATUS: 'taak.status',
        TYPE: 'type',
        TOEGEKEND: 'zoeken.filter.jaNee'
    };

    constructor(private _formBuilder: FormBuilder) {}

    ngOnInit(): void {
        this.formGroup = this._formBuilder.group({});
        this.opties.forEach((value, index) => {
            this.formGroup.addControl(value, new FormControl(!!this.selected?.find(s => s === value)));
        });
    }

    checkboxChange(): void {
        const checked: string[] = [];
        Object.keys(this.formGroup.controls).forEach(key => {
            if (this.formGroup.controls[key].value) {
                checked.push(key);
            }
        });
        this.changed.emit(checked);
    }

    isVertaalbaar(veld: string): boolean {
        return this.VERTAALBAARE_FACETTEN[veld] !== undefined;
    }
}