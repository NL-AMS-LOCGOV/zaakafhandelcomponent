/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EditComponent} from '../edit.component';
import {SelectFormField} from '../../material-form-builder/form-components/select/select-form-field';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';

@Component({
    selector: 'zac-edit-communicatiekanaal',
    templateUrl: './edit-communicatiekanaal.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less']
})
export class EditCommunicatiekanaalComponent extends EditComponent {

    @Input() formField: SelectFormField;
    @Input() reasonField: InputFormField;

    option: {};

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    init(formField: SelectFormField): void {
        this.option = formField.formControl.value;
        this.value = formField.formControl.value.value;
    }

    valueChanges(): void {
        this.dirty = true;
    }

    edit(editing: boolean): void {
        super.edit(editing);
        this.reasonField.formControl.setValue(null);
        this.dirty = false;
    }

    protected submitSave(): void {
        if (this.formField.formControl.valid) {
            this.onSave.emit(
                {communicatiekanaal: this.formField.formControl.value, reden: this.reasonField?.formControl.value});
        }
        this.editing = false;
    }

    cancel(): void {
        this.formField.formControl.setValue(this.option);
        this.editing = false;
    }

}
