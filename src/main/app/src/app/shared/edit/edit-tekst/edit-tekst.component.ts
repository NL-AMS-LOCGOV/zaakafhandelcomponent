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
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {FormItem} from '../../material-form-builder/model/form-item';

@Component({
    selector: 'zac-edit-tekst',
    templateUrl: './edit-tekst.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less']
})
export class EditTekstComponent extends EditComponent {

    @Input() formField: TextareaFormField;
    @Input() reasonField: InputFormField;

    reasonItem: FormItem;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    init(formField: SelectFormField): void {
        this.value = formField.formControl.value;
    }

    valueChanges(): void {
        this.dirty = true;
    }

    edit(editing: boolean): void {
        super.edit(editing);
        if (this.reasonItem) {
            this.reasonItem.data.formControl.setValue(null);
        }
        this.dirty = false;
    }

    protected submitSave(): void {
        if (this.formField.formControl.valid) {
            this.onSave.emit({[this.formField.id]: this.formField.formControl.value, reden: this.reasonField?.formControl.value});
        }
        this.editing = false;
    }
}
