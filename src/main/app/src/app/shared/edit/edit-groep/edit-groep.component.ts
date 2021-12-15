/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {AutocompleteFormField} from '../../material-form-builder/form-components/autocomplete/autocomplete-form-field';

@Component({
    selector: 'zac-edit-groep',
    templateUrl: './edit-groep.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-groep.component.less']
})
export class EditGroepComponent extends EditComponent {

    @Input() formField: AutocompleteFormField;

    constructor(mfbService: MaterialFormBuilderService) {
        super(mfbService);
    }

    init(formField: AutocompleteFormField): void {
        this.value = formField.formControl.value ? formField.formControl.value[formField.optionLabel] : formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }

}
