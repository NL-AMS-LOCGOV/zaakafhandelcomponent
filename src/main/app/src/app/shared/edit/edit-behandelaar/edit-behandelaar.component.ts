/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {AutocompleteFormField} from '../../material-form-builder/form-components/autocomplete/autocomplete-form-field';
import {UtilService} from '../../../core/service/util.service';

@Component({
    selector: 'zac-edit-behandelaar',
    templateUrl: './edit-behandelaar.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-behandelaar.component.less']
})
export class EditBehandelaarComponent extends EditComponent {

    @Input() formField: AutocompleteFormField;
    @Input() showAssignToMe: boolean = false;
    @Output() onAssignToMe: EventEmitter<any> = new EventEmitter<any>();

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    assignToMe(): void {
        this.onAssignToMe.emit();
        this.editing = false;
    }

    release(): void {
        this.formItem.data.formControl.setValue(null);
        this.save();
    }

    init(formField: AutocompleteFormField): void {
        this.value = formField.formControl.value ? formField.formControl.value[formField.optionLabel] : formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }

    edit(editing: boolean): void {
        super.edit(editing);
        this.formItem.data.formControl.setValue(null);
        this.dirty = false;
    }
}
