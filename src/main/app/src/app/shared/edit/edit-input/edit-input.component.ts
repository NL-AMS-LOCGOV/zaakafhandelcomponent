/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';

@Component({
    selector: 'zac-edit-input',
    templateUrl: './edit-input.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less']
})
export class EditInputComponent extends EditComponent {

    @Input() formField: InputFormField;
    @Input() reasonField: InputFormField;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    init(formField: InputFormField): void {
        this.value = formField.formControl.value;
    }

    valueChanges(): void {
        this.dirty = true;
    }

    edit(editing: boolean): void {
        super.edit(editing);
        this.dirty = false;
    }

    protected submitSave(): void {
        if (this.formField.formControl.valid) {
            this.onSave.emit({[this.formField.id]: this.formField.formControl.value, reden: this.reasonField?.formControl.value});
        }
        this.editing = false;
    }
}
