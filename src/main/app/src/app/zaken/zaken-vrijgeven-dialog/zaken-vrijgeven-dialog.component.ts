/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZakenService} from '../zaken.service';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';

@Component({
    templateUrl: 'zaken-vrijgeven-dialog.component.html',
    styleUrls: ['./zaken-vrijgeven-dialog.component.less']
})
export class ZakenVrijgevenDialogComponent implements OnInit {

    loading: boolean;
    redenFormField: AbstractFormField;

    constructor(
        public dialogRef: MatDialogRef<ZakenVrijgevenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakZoekObject[],
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService) {
    }

    ngOnInit(): void {
        this.redenFormField = new TextareaFormFieldBuilder().id('reden')
                                                            .label('reden')
                                                            .maxlength(100)
                                                            .build();
    }

    close(): void {
        this.dialogRef.close();
    }

    vrijgeven(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.vrijgeven(this.data.map(zaak => zaak.id), this.redenFormField.formControl.value).subscribe(() => {
            this.dialogRef.close(true);
        });
    }
}
