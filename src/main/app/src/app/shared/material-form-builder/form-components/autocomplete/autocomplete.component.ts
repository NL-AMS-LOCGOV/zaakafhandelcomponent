/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {AutocompleteFormField} from './autocomplete-form-field';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@Component({
    selector: 'zac-autocomplete',
    templateUrl: './autocomplete.component.html',
    styleUrls: ['./autocomplete.component.less']
})
export class AutocompleteComponent implements AfterViewInit, IFormComponent {

    data: AutocompleteFormField;

    options: any[];
    filteredOptions: Observable<any[]>;

    constructor() { }

    ngAfterViewInit() {
        this.data.options.subscribe(options => {
            this.options = options;

            this.filteredOptions = this.data.formControl.valueChanges.pipe(
                startWith(''),
                map(value => (typeof value === 'string' ? value : value ? value[this.data.optionLabel] : null)),
                map(name => (name ? this._filter(name) : this.options.slice()))
            );
        });
    }

    displayFn = (obj: any): string => {
        return obj && obj[this.data.optionLabel] ? obj[this.data.optionLabel] : obj;
    };

    private _filter(filter: string): any[] {
        const filterValue = filter.toLowerCase();

        return this.options.filter(option => option[this.data.optionLabel].toLowerCase().includes(filterValue));
    }

}
