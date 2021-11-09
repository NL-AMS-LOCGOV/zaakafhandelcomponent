/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
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
export class FormComponent implements OnInit {

    @Input() formFields: Array<AbstractFormField[]>;
    @Input() config: FormConfig;
    @Output() formSubmit: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
    data: Array<FormItem[]>;
    formGroup: FormGroup = new FormGroup({});

    constructor(private mfbService: MaterialFormBuilderService) {
    }

    ngOnInit(): void {

        this.data = this.mfbService.createForm(this.formFields);

        for (const value of this.data.values()) {
            value.forEach((formItem) => {
                if (formItem.data.fieldType !== FieldType.HEADING) {
                    this.formGroup.addControl(formItem.data.id, formItem.data.formControl);
                }
            });
        }
    }

    submit(): void {
        this.formSubmit.emit(this.formGroup);
    }

    cancel(): void {
        this.formSubmit.emit(null);
    }

}
