/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'werkvoorraad-vrijgeven-dialog',
    templateUrl: 'zaken-vrijgeven-dialog.component.html',
    styleUrls: ['./zaken-vrijgeven-dialog.component.less']
})
export class ZakenVrijgevenDialogComponent {

    redenFormItem: FormItem;
    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<ZakenVrijgevenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[],
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService,
        private translate: TranslateService) {
    }

    ngOnInit(): void {
        this.redenFormItem = this.mfbService.getFormItem(new TextareaFormFieldBuilder(this.translate).id('reden')
                                                                                                     .label('reden')
                                                                                                     .build());
    }

    close(): void {
        this.dialogRef.close();
    }

    vrijgeven(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.vrijgeven(
            this.data, this.redenFormItem.data.formControl.value
        ).subscribe(() => {
            this.dialogRef.close(true);
        });
    }
}
