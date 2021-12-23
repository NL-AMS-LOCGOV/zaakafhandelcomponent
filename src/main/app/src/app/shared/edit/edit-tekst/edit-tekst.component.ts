/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EditComponent} from '../edit.component';
import {SelectFormField} from '../../material-form-builder/form-components/select/select-form-field';
import {TextareaFormField} from '../../material-form-builder/form-components/textarea/textarea-form-field';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';

@Component({
    selector: 'zac-edit-tekst',
    templateUrl: './edit-tekst.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-tekst.component.less']
})
export class EditTekstComponent extends EditComponent {

    @Input() formField: TextareaFormField;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    init(formField: SelectFormField): void {
        this.value = formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }
}
