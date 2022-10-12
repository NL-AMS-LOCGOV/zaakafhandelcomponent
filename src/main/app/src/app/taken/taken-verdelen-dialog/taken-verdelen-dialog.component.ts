/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {TakenService} from '../taken.service';
import {Validators} from '@angular/forms';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';
import {MedewerkerGroepFormField} from '../../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-form-field';
import {MedewerkerGroepFieldBuilder} from '../../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-field-builder';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';

@Component({
    selector: 'zac-taken-verdelen-dialog',
    templateUrl: './taken-verdelen-dialog.component.html',
    styleUrls: ['./taken-verdelen-dialog.component.less']
})
export class TakenVerdelenDialogComponent implements OnInit {

    medewerkerGroepFormField: MedewerkerGroepFormField;
    loading: boolean = false;

    constructor(
        public dialogRef: MatDialogRef<TakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: TaakZoekObject[],
        private mfbService: MaterialFormBuilderService,
        private takenService: TakenService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.medewerkerGroepFormField = new MedewerkerGroepFieldBuilder().id('behandelaar').label('behandelaar.-kies-')
                                                                          .validators(Validators.required).maxlength(50)
                                                                          .build();

    }

    verdeel(): void {
        const toekenning: { groep?: Group, medewerker?: User } = this.medewerkerGroepFormField.formControl.value;
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.takenService.verdelen(this.data, toekenning.groep, toekenning.medewerker).subscribe(() => {
            this.dialogRef.close(toekenning.groep || toekenning.medewerker);
        });
    }

}
