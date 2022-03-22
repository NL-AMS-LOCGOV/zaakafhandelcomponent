/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {EditAutocompleteComponent} from '../edit-autocomplete/edit-autocomplete.component';
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

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    edit(editing: boolean): void {
        super.edit(editing);
        this.reasonField?.formControl.setValue(null);
    }

    release(): void {
        this.formField.formControl.setValue(null);
        this.reasonField?.formControl.setValue(null);
        this.save();
    }

    protected submitSave(): void {
        if (!this.reasonField || this.reasonField.formControl.valid) {
            this.onSave.emit({behandelaar: this.formField.formControl.value, reden: this.reasonField?.formControl.value});
        }
        this.editing = false;
    }

    assignToMe(): void {
        this.onAssignToMe.emit({reden: this.reasonField?.formControl.value});
        this.editing = false;
    }
}
