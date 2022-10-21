/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {UtilService} from '../../../core/service/util.service';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {IdentityService} from '../../../identity/identity.service';
import {User} from '../../../identity/model/user';
import {EditComponent} from '../edit.component';
import {MedewerkerGroepFormField} from '../../material-form-builder/form-components/select-medewerker/medewerker-groep-form-field';
import {FormControl} from '@angular/forms';

@Component({
    selector: 'zac-edit-groep-behandelaar',
    templateUrl: './edit-groep-behandelaar.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-groep-behandelaar.component.less']
})
export class EditGroepBehandelaarComponent extends EditComponent implements OnInit {

    @Input() formField: MedewerkerGroepFormField;
    @Input() reasonField: InputFormField;
    showAssignToMe: boolean = false;
    private loggedInUser: User;

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService, private identityService: IdentityService) {
        super(mfbService, utilService);
        this.identityService.readLoggedInUser().subscribe(user => {
            this.loggedInUser = user;
        });
    }

    edit(): void {
        super.edit();

        this.showAssignToMe = this.loggedInUser.id !== this.formField.medewerker.value?.id;
        if (this.reasonField) {
            this.formFields.setControl('reden', this.reasonField.formControl);
        }
    }

    release(): void {
        this.formField.medewerkerValue(null);
    }

    assignToMe(): void {
        this.formField.medewerkerValue(this.loggedInUser);
    }

    getFormControl(control: string): FormControl {
        return this.formField.formControl.get(control) as FormControl;
    }
}
