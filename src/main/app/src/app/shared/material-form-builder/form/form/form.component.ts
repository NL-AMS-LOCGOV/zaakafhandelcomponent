/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {FieldType} from '../../model/field-type.enum';
import {FormConfig} from '../../model/form-config';
import {AbstractFormField} from '../../model/abstract-form-field';

@Component({
    selector: 'mfb-form',
    templateUrl: './form.component.html',
    styleUrls: ['./form.component.less']
})
export class FormComponent {

    @Input() set formFields(formfields: Array<AbstractFormField[]>) {
        this.refreshFormfields(formfields);
    }

    @Input() set config(config: FormConfig) {
        this._config = config;
    }

    get config(): FormConfig {
        return this._config;
    }

    @Output() formSubmit: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
    @Output() formPartial: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();

    data: Array<AbstractFormField[]>;
    formGroup: FormGroup;

    private _config: FormConfig;

    constructor() {
    }

    refreshFormfields(formFields: Array<AbstractFormField[]>): void {
        this.data = formFields;

        this.formGroup = new FormGroup({});
        for (const value of this.data.values()) {
            value.forEach((formField) => {
                if (formField.fieldType !== FieldType.HEADING) {
                    this.formGroup.addControl(formField.id, formField.formControl);
                }
            });
        }
    }

    partial() {
        this.formPartial.emit(this.formGroup);
    }

    submit(): void {
        this.formSubmit.emit(this.formGroup);
    }

    cancel(): void {
        this.formSubmit.emit(null);
    }
}
