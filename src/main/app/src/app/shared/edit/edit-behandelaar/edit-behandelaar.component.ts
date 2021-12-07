/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {SelectFormField} from '../../material-form-builder/form-components/select/select-form-field';

@Component({
    selector: 'zac-edit-behandelaar',
    templateUrl: './edit-behandelaar.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-behandelaar.component.less']
})
export class EditBehandelaarComponent extends EditComponent {

    @Input() formField: SelectFormField;
    @Input() showAssignToMe: boolean = false;
    @Output() onAssignToMe: EventEmitter<any> = new EventEmitter<any>();

    constructor(mfbService: MaterialFormBuilderService) {
        super(mfbService);
    }

    assignToMe(): void {
        this.onAssignToMe.emit();
        this.editing = false;
    }

    init(formField: SelectFormField): void {
        this.value = formField.formControl.value ? formField.formControl.value[formField.optionLabel] : formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }

}
