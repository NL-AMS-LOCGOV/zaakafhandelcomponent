/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';
import {MaterialFormBuilderService} from '../material-form-builder/material-form-builder.service';
import {FormItem} from '../material-form-builder/model/form-item';
import {AbstractChoicesFormField} from '../material-form-builder/model/abstract-choices-form-field';
import {StaticTextComponent} from '../static-text/static-text.component';
import {Subscription} from 'rxjs';

@Component({
    selector: 'zac-edit',
    templateUrl: './edit.component.html',
    styleUrls: ['../static-text/static-text.component.less', './edit.component.less']
})
export class EditComponent extends StaticTextComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy {

    editing: boolean;
    dirty: boolean = false;
    formItem: FormItem;

    @Input() formField: AbstractFormField;
    @Output() onSave: EventEmitter<string> = new EventEmitter<string>();

    subscription: Subscription;

    constructor(private mfbService: MaterialFormBuilderService) {
        super();
    }

    ngOnInit(): void {
        super.ngOnInit();

        this.init(this.formField);
    }

    init(formField: AbstractFormField): void {
        if (formField instanceof AbstractChoicesFormField && formField.formControl.value) {
            this.value = this.formField.formControl.value[formField.optionLabel];
        } else {
            this.value = formField.formControl.value;
        }

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }

    ngAfterViewInit(): void {
        this.formItem = this.mfbService.getFormItem(this.formField);
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.init(changes.formField.currentValue);
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    onOutsideClick() {
        if (this.editing && this.dirty) {
            this.save();
        } else {
            this.cancel();
        }
    }

    edit(editing: boolean): void {
        this.editing = editing;
    }

    save(): void {
        this.onSave.emit(this.formItem.data.formControl.value);
        this.editing = false;
    }

    cancel(): void {
        this.editing = false;
    }
}
