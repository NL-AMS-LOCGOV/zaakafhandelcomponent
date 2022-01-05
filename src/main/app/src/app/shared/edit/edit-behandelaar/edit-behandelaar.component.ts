/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {EditAutocompleteComponent} from '../edit-autocomplete/edit-autocomplete.component';

@Component({
    selector: 'zac-edit-behandelaar',
    templateUrl: './edit-behandelaar.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-behandelaar.component.less']
})
export class EditBehandelaarComponent extends EditAutocompleteComponent {

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
}
