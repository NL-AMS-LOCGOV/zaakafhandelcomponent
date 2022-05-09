/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {Taak} from '../model/taak';
import {TakenService} from '../taken.service';
import {Validators} from '@angular/forms';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

@Component({
    selector: 'zac-taken-verdelen-dialog',
    templateUrl: './taken-verdelen-dialog.component.html',
    styleUrls: ['./taken-verdelen-dialog.component.less']
})
export class TakenVerdelenDialogComponent implements OnInit {

    medewerkerFormField: AbstractFormField;
    loading: boolean = false;

    constructor(
        public dialogRef: MatDialogRef<TakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: Taak[],
        private mfbService: MaterialFormBuilderService,
        private takenService: TakenService,
        private identityService: IdentityService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.medewerkerFormField = new AutocompleteFormFieldBuilder().id('behandelaar').label('behandelaar.-kies-')
                                                                     .optionLabel('naam')
                                                                     .options(this.identityService.listUsers())
                                                                     .validators(Validators.required).build();

    }

    verdeel(): void {
        const medewerker = this.medewerkerFormField.formControl.value;
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.takenService.verdelen(this.data, medewerker).subscribe(() => {
            this.dialogRef.close(medewerker);
        });
    }

}
