/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {SelectFormField} from '../../material-form-builder/form-components/select/select-form-field';
import {EditComponent} from '../edit.component';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {FormItem} from '../../material-form-builder/model/form-item';

@Component({
    selector: 'zac-edit-vertrouwelijkheidaanduiding',
    templateUrl: './edit-vertrouwelijkheidaanduiding.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-vertrouwelijkheidaanduiding.component.less']
})
export class EditVertrouwelijkheidaanduidingComponent extends EditComponent {

    @Input() formField: SelectFormField;
    @Input() reasonField: InputFormField;

    reasonItem: FormItem;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    init(formField: SelectFormField): void {
        this.value = formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
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
        this.dirty = false;
    }

    protected submitSave(): void {
        if (this.formItem.data.formControl.valid) {
            this.onSave.emit({vertrouwelijkheidaanduiding: this.formItem.data.formControl.value.value, reden: this.reasonItem?.data.formControl.value});
        }
        this.editing = false;
    }
}
