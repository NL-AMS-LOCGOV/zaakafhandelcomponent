/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';
import {MaterialFormBuilderService} from '../material-form-builder/material-form-builder.service';
import {FormItem} from '../material-form-builder/model/form-item';
import {AbstractChoicesFormField} from '../material-form-builder/model/abstract-choices-form-field';
import {StaticTextComponent} from '../static-text/static-text.component';

@Component({
    selector: 'zac-edit',
    templateUrl: './edit.component.html',
    styleUrls: ['../static-text/static-text.component.less', './edit.component.less']
})
export class EditComponent extends StaticTextComponent implements OnInit, AfterViewInit {

    editing: boolean;
    formItem: FormItem;

    @Input() formField: AbstractFormField;
    @Output() onSave: EventEmitter<string> = new EventEmitter<string>();

    constructor(private mfbService: MaterialFormBuilderService) {
        super();
    }

    ngOnInit(): void {
        super.ngOnInit();
        if (this.formField instanceof AbstractChoicesFormField && this.formField.formControl.value) {
            this.value = this.formField.formControl.value[this.formField.optionLabel];
        } else {
            this.value = this.formField.formControl.value;
        }
    }

    ngAfterViewInit(): void {
        this.formItem = this.mfbService.getFormItem(this.formField);
    }

    edit(editing: boolean): void {
        this.editing = editing;
        this.formField.formControl.setValue(null);
    }

    save(): void {
        this.onSave.emit(this.formItem.data.formControl.value);
        this.editing = false;
    }

    cancel(): void {
        this.editing = false;
    }

}
