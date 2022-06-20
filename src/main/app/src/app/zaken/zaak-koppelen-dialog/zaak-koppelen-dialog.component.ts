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
import {GerelateerdeZaak} from '../model/gerelateerde-zaak';

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
        private utilService: UtilService) {
    }

    close(): void {
        this.zakenService.addTeKoppelenZaak(this.bronZaak);
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.loading = true;
        this.zakenService.readZaak(this.data.bronZaakUuid).subscribe(bronZaak => {
            this.bronZaak = bronZaak;
            this.zakenService.readZaakByID(this.data.gerelateerdeZaak.identificatie).subscribe(teKoppelenZaak => {
                this.teKoppelenZaak = teKoppelenZaak;

                if (bronZaak.heeftDeelzaken && !teKoppelenZaak.heeftDeelzaken && !teKoppelenZaak.heeftHoofdzaak) {
                    this.koppelKeuzes.push({label: 'Hoofdzaak', value: 'HOOFDZAAK'});
                } else if (!bronZaak.heeftDeelzaken && !bronZaak.heeftHoofdzaak &&
                    !teKoppelenZaak.heeftDeelzaken && !teKoppelenZaak.heeftHoofdzaak) {
                    this.koppelKeuzes.push({label: 'Deelzaak', value: 'DEELZAAK'});
                    this.koppelKeuzes.push({label: 'Hoofdzaak', value: 'HOOFDZAAK'});
                } else if (!bronZaak.heeftHoofdzaak && !bronZaak.heeftDeelzaken && !teKoppelenZaak.heeftHoofdzaak) {
                    this.koppelKeuzes.push({label: 'Deelzaak', value: 'DEELZAAK'});
                }

                if (this.koppelKeuzes.length > 0) {
                    this.koppelSelectFormField = new SelectFormFieldBuilder().id('koppelkeuze')
                                                                             .label('title.zaak.koppelen.dialog')
                                                                             .optionLabel('label')
                                                                             .validators(Validators.required)
                                                                             .options(this.koppelKeuzes).build();
                }

                this.loading = false;
            });
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
        const gerelateerdeZaak = new GerelateerdeZaak();
        gerelateerdeZaak.identificatie = nieuweZaakID;
        gerelateerdeZaak.relatieType = relatieType.value;
        zaakKoppelGegevens.gerelateerdeZaak = gerelateerdeZaak;
        this.zakenService.postKoppelZaak(zaakKoppelGegevens).subscribe(() => {
            this.dialogRef.close(true);
            this.utilService.openSnackbar('msg.zaak.koppelen.uitgevoerd',
                {zaak: zaak.identificatie, relatie: relatieType.label, gekoppeldeZaak: nieuweZaakID});
        });
    }
}
