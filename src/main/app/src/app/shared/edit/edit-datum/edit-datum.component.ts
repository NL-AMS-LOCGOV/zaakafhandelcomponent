/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {DateFormField} from '../../material-form-builder/form-components/date/date-form-field';

@Component({
    selector: 'zac-edit-datum',
    templateUrl: './edit-datum.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-datum.component.less']
})
export class EditDatumComponent extends EditComponent {

    @Input() formField: DateFormField;

    constructor(mfbService: MaterialFormBuilderService) {
        super(mfbService);
    }

    init(formField: DateFormField): void {
        this.value = formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }
}
