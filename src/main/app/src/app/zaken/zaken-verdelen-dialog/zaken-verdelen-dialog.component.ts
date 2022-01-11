/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {Validators} from '@angular/forms';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';

@Component({
    selector: 'werkvoorraad-verdelen-dialog',
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less']
})
export class ZakenVerdelenDialogComponent implements OnInit {

    medewerkerFormItem: FormItem;
    redenFormItem: FormItem;
    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<ZakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[],
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService,
        private identityService: IdentityService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.medewerkerFormItem = this.mfbService.getFormItem(new AutocompleteFormFieldBuilder().id('behandelaar')
                                                                                                .label('behandelaar')
                                                                                                .optionLabel('naam')
                                                                                                .options(this.identityService.listMedewerkers())
                                                                                                .validators(Validators.required)
                                                                                                .build());
        this.redenFormItem = this.mfbService.getFormItem(new TextareaFormFieldBuilder().id('reden')
                                                                                       .label('reden')
                                                                                       .build());
    }

    verdeel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.verdelen(
            this.data,
            null,
            this.medewerkerFormItem.data.formControl.value,
            this.redenFormItem.data.formControl.value
        ).subscribe(() => {
            this.dialogRef.close(this.medewerkerFormItem.data.formControl.value);
        });
    }
}
