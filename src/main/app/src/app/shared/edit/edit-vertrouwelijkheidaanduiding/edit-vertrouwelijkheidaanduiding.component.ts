/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {SelectFormField} from '../../material-form-builder/form-components/select/select-form-field';
import {EditComponent} from '../edit.component';

@Component({
    selector: 'zac-edit-vertrouwelijkheidaanduiding',
    templateUrl: './edit-vertrouwelijkheidaanduiding.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-vertrouwelijkheidaanduiding.component.less']
})
export class EditVertrouwelijkheidaanduidingComponent extends EditComponent {

    @Input() formField: SelectFormField;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    init(formField: SelectFormField): void {
        this.value = formField.formControl.value;

        this.subscription = formField.formControl.valueChanges.subscribe(() => {
            this.dirty = true;
        });
    }

    protected submitSave(): void {
        if (this.formItem.data.formControl.valid) {
            this.onSave.emit(this.formItem.data.formControl.value.value);
        }
        this.editing = false;
    }

}
