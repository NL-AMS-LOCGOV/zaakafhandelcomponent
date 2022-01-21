/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {EditAutocompleteComponent} from '../edit-autocomplete/edit-autocomplete.component';
import {FormItem} from '../../material-form-builder/model/form-item';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';

@Component({
    selector: 'zac-edit-behandelaar',
    templateUrl: './edit-behandelaar.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-behandelaar.component.less']
})
export class EditBehandelaarComponent extends EditAutocompleteComponent {

    @Input() reasonField: InputFormField;
    @Input() showAssignToMe: boolean = false;
    @Output() onAssignToMe: EventEmitter<any> = new EventEmitter<any>();

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

    release(): void {
        this.formItem.data.formControl.setValue(null);
        this.save();
    }

    protected submitSave(): void {
        if (this.formItem.data.formControl.valid) {
            this.onSave.emit({behandelaar: this.formItem.data.formControl.value, reden: this.reasonItem?.data.formControl.value});
        }
        this.editing = false;
    }

    assignToMe(): void {
        this.onAssignToMe.emit({reden: this.reasonItem?.data.formControl.value});
        this.editing = false;
    }
}
