/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';
import {MaterialFormBuilderService} from '../material-form-builder/material-form-builder.service';
import {StaticTextComponent} from '../static-text/static-text.component';
import {Subscription} from 'rxjs';
import {UtilService} from '../../core/service/util.service';

@Component({
    template: '',
    styleUrls: ['../static-text/static-text.component.less', './edit.component.less']
})
export abstract class EditComponent extends StaticTextComponent implements OnInit, OnChanges, OnDestroy {

    editing: boolean;
    dirty: boolean = false;

    @Input() readonly: boolean = false;
    @Input() abstract formField: AbstractFormField;
    @Output() onSave: EventEmitter<any> = new EventEmitter<any>();

    subscription: Subscription;

    protected constructor(protected mfbService: MaterialFormBuilderService, protected utilService: UtilService) {
        super();
    }

    abstract init(formField: AbstractFormField): void;

    ngOnChanges(changes: SimpleChanges): void {
        for (const propName in changes) {
            if (changes.hasOwnProperty(propName)) {
                switch (propName) {
                    case 'formField':
                        this.init(changes.formField.currentValue);
                        break;
                    case 'icon':
                        this.showIcon = this.icon?.showIcon(this.formField.formControl);
                }
            }
        }
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
        if (!this.readonly && !this.utilService.hasEditOverlay()) {
            this.editing = editing;
            this.formField.formControl.markAsUntouched();
        }
    }

    save(): void {
        // Wait for an async validator is it is present.
        if (this.formField.formControl.pending) {
            const sub = this.formField.formControl.statusChanges.subscribe(() => {
                if (this.formField.formControl.valid) {
                    this.submitSave();
                }
                sub.unsubscribe();
            });
        } else {
            this.submitSave();
        }
    }

    protected submitSave(): void {
        if (this.formField.formControl.valid) {
            this.onSave.emit(this.formField.formControl.value);
        }
        this.editing = false;
    }

    cancel(): void {
        this.formField.formControl.setValue(this.value);
        this.editing = false;
    }
}
