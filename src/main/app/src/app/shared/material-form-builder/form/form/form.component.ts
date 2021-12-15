/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormItem} from '../../model/form-item';
import {FormGroup} from '@angular/forms';
import {FieldType} from '../../model/field-type.enum';
import {FormConfig} from '../../model/form-config';
import {AbstractFormField} from '../../model/abstract-form-field';
import {MaterialFormBuilderService} from '../../material-form-builder.service';

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
    };

    @Output() formSubmit: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
    @Output() formPartial: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();

    data: Array<FormItem[]>;
    formGroup: FormGroup;

    private _config: FormConfig;

    constructor(private mfbService: MaterialFormBuilderService) {
    }

    refreshFormfields(formFields: Array<AbstractFormField[]>): void {
        this.data = this.mfbService.createForm(formFields);

        this.formGroup = new FormGroup({});
        for (const value of this.data.values()) {
            value.forEach((formItem) => {
                if (formItem.data.fieldType !== FieldType.HEADING) {
                    this.formGroup.addControl(formItem.data.id, formItem.data.formControl);
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

    get config(): FormConfig {
        return this._config;
    }
}
