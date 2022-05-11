/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {MedewerkerGroepFieldBuilder} from '../../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-field-builder';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';
import {MedewerkerGroepFormField} from '../../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-form-field';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';

@Component({
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less']
})
export class ZakenVerdelenDialogComponent implements OnInit {

    medewerkerGroepFormField: MedewerkerGroepFormField;
    redenFormField: AbstractFormField;
    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<ZakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[],
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.medewerkerGroepFormField = new MedewerkerGroepFieldBuilder().id('toekenning')
                                                                         .groepLabel('actie.zaak.toekennen.groep').groepOptioneel()
                                                                         .medewerkerLabel('actie.zaak.toekennen.medewerker').build();
        this.redenFormField = new TextareaFormFieldBuilder().id('reden').label('reden').build();
    }

    verdeel(): void {
        const toekenning: { groep?: Group, medewerker?: User } = this.medewerkerGroepFormField.formControl.value;
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.verdelen(
            this.data,
            toekenning.groep,
            toekenning.medewerker,
            this.redenFormField.formControl.value
        ).subscribe(() => {
            this.dialogRef.close(toekenning.groep || toekenning.medewerker);
        });
    }
}
