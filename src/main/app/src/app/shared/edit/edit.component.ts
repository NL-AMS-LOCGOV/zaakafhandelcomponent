/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {TextareaFormField} from '../material-form-builder/form-components/textarea/textarea-form-field';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';
import {FormConfig} from '../material-form-builder/model/form-config';

@Component({
    selector: 'zac-edit',
    templateUrl: './edit.component.html',
    styleUrls: ['./edit.component.less']
})
export class EditComponent implements OnInit {

    editing: boolean;
    editField: Array<AbstractFormField[]>;
    formConfig: FormConfig;

    @Input() formField: AbstractFormField;

    @Input() label: string;
    @Input() value: string;

    @Output() onSave: EventEmitter<string> = new EventEmitter<string>();

    constructor() { }

    ngOnInit(): void {
        const textarea = new TextareaFormField(this.label, null, this.value);
        this.formConfig = new FormConfig('actie.opslaan', 'actie.annuleren');
        this.editField = [[textarea]];
    }

    save(formGroup: FormGroup): void {
        if (formGroup) {
            this.onSave.emit(formGroup.controls[this.label].value);
        }

        this.editing = false;
    }

}
