/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {of} from 'rxjs';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {AbstractChoicesFormField} from '../../shared/material-form-builder/model/abstract-choices-form-field';

@Component({
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less']
})
export class ZakenVerdelenDialogComponent implements OnInit {

    groepFormField: AbstractFormField;
    filterFormField: AbstractFormField;
    medewerkerFormField: AbstractChoicesFormField;
    redenFormField: AbstractFormField;
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
        this.groepFormField =
            new AutocompleteFormFieldBuilder()
            .id('groep')
            .label('groep')
            .optionLabel('naam')
            .options(this.identityService.listGroepen())
            .build();
        this.filterFormField =
            new CheckboxFormFieldBuilder()
            .id('filter')
            .label('filter.groep')
            .value(true)
            .build();
        this.redenFormField =
            new TextareaFormFieldBuilder()
            .id('reden')
            .label('reden')
            .build();
        this.medewerkerFormField =
            new AutocompleteFormFieldBuilder()
            .id('behandelaar')
            .label('behandelaar')
            .optionLabel('naam')
            .options(of([]))
            .build();
        this.laadMedewerkers();
    }

    public laadMedewerkers(): void {
        let medewerkers;
        if (this.groepFormField.formControl.value && this.filterFormField.formControl.value) {
            medewerkers = this.identityService.listMedewerkersInGroep(this.groepFormField.formControl.value.id);
        } else {
            medewerkers = this.identityService.listMedewerkers();
        }
        this.medewerkerFormField.options = medewerkers;
    }

    verdeel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.verdelen(
            this.data,
            this.groepFormField.formControl.value,
            this.medewerkerFormField.formControl.value,
            this.redenFormField.formControl.value
        ).subscribe(() => {
            this.dialogRef.close(this.groepFormField.formControl.value || this.medewerkerFormField.formControl.value);
        });
    }
}
