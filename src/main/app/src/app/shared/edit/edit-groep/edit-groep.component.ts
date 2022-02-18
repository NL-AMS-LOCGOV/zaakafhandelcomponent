/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {EditAutocompleteComponent} from '../edit-autocomplete/edit-autocomplete.component';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {FormItem} from '../../material-form-builder/model/form-item';

@Component({
    selector: 'zac-edit-groep',
    templateUrl: './edit-groep.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less']
})
export class EditGroepComponent extends EditAutocompleteComponent {

    @Input() reasonField: InputFormField;

    reasonItem: FormItem;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        if (this.reasonField) {
            this.reasonItem = this.mfbService.getFormItem(this.reasonField);
        }
    }

    edit(editing: boolean): void {
        super.edit(editing);
        this.reasonItem.data.formControl.setValue(null);
    }

    protected submitSave(): void {
        if (this.formItem.data.formControl.valid) {
            this.onSave.emit({groep: this.formItem.data.formControl.value, reden: this.reasonItem?.data.formControl.value});
        }
        this.editing = false;
    }
}
