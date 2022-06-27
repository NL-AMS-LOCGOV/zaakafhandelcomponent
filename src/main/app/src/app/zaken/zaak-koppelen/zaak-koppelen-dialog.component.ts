/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZakenService} from '../zaken.service';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {Zaak} from '../model/zaak';
import {ZaakKoppelGegevens} from '../model/zaak-koppel-gegevens';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Validators} from '@angular/forms';
import {UtilService} from '../../core/service/util.service';
import {ZaakKoppelenService} from './zaak-koppelen.service';
import {combineLatestWith} from 'rxjs';

@Component({
    templateUrl: 'zaak-koppelen-dialog.component.html'
})
export class ZaakKoppelenDialogComponent implements OnInit {

    koppelSelectFormField: AbstractFormField;
    bronZaak: Zaak;
    teKoppelenZaak: Zaak;
    loading: boolean;
    koppelKeuzes: { label: string, value: string }[] = [];

    constructor(
        public dialogRef: MatDialogRef<ZaakKoppelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakKoppelGegevens,
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService,
        private utilService: UtilService,
        private zaakKoppelenService: ZaakKoppelenService) {
    }

    close(): void {
        this.zaakKoppelenService.addTeKoppelenZaak(this.bronZaak);
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.loading = true;
        const bronZaak$ = this.zakenService.readZaak(this.data.bronZaakUuid);
        const teKoppelenZaak$ = this.zakenService.readZaakByID(this.data.identificatie);

        bronZaak$.pipe(combineLatestWith(teKoppelenZaak$)).subscribe(([bronZaak, teKoppelenZaak]) => {
            this.bronZaak = bronZaak;
            this.teKoppelenZaak = teKoppelenZaak;

            if (!bronZaak.isDeelzaak && !teKoppelenZaak.isHoofdzaak && !teKoppelenZaak.isDeelzaak) {
                this.koppelKeuzes.push({label: 'Hoofdzaak', value: 'HOOFDZAAK'});
            }

            if (!bronZaak.isDeelzaak && !bronZaak.isHoofdzaak && !teKoppelenZaak.isDeelzaak) {
                this.koppelKeuzes.push({label: 'Deelzaak', value: 'DEELZAAK'});
            }

            if (this.koppelKeuzes.length > 0) {
                this.koppelSelectFormField = new SelectFormFieldBuilder().id('koppelkeuze')
                                                                         .label('title.zaak.koppelen.dialog')
                                                                         .optionLabel('label')
                                                                         .value(this.koppelKeuzes.length === 1 ? this.koppelKeuzes[0] : null)
                                                                         .validators(Validators.required)
                                                                         .options(this.koppelKeuzes).build();
            }

            this.loading = false;
        });
    }

    koppel(): void {
        const relatieType = this.koppelSelectFormField.formControl.value;
        this.dialogRef.disableClose = true;
        this.loading = true;

        this.koppelZaak(this.bronZaak, this.teKoppelenZaak.identificatie, relatieType);
    }

    private koppelZaak(zaak: Zaak, nieuweZaakID: string, relatieType: any) {
        const zaakKoppelGegevens = new ZaakKoppelGegevens();
        zaakKoppelGegevens.bronZaakUuid = zaak.uuid;
        zaakKoppelGegevens.identificatie = nieuweZaakID;
        zaakKoppelGegevens.relatieType = relatieType.value;
        this.zakenService.postKoppelZaak(zaakKoppelGegevens).subscribe(() => {
            this.dialogRef.close(true);
        });
    }
}
