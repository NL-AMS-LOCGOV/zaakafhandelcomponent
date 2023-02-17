/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZakenService} from '../zaken.service';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {Zaak} from '../model/zaak';
import {ZaakKoppelGegevens} from '../model/zaak-koppel-gegevens';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Validators} from '@angular/forms';
import {UtilService} from '../../core/service/util.service';
import {ZaakKoppelenService} from './zaak-koppelen.service';
import {forkJoin} from 'rxjs';
import {RadioFormField} from '../../shared/material-form-builder/form-components/radio/radio-form-field';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';
import {RadioFormFieldBuilder} from '../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {ZaakRelatietype} from '../model/zaak-relatietype';
import {ZaakKoppelDialogGegevens} from '../model/zaak-koppel-dialog-gegevens';

@Component({
    templateUrl: 'zaak-koppelen-dialog.component.html'
})
export class ZaakKoppelenDialogComponent implements OnInit {

    loading: boolean;
    bronZaak: Zaak;
    doelZaak: Zaak;
    soortRadioFormField: RadioFormField;
    hoofddeelZaakSelectFormField: SelectFormField;
    relevanteZaakBronSelectFormField: SelectFormField;
    relevanteZaakDoelSelectFormField: SelectFormField;
    readonly soortOptions: string[] = [];
    readonly HOOFDDEEL: string = 'relatieSoort.hoofddeelZaak';
    readonly RELEVANTE: string = 'relatieSoort.relevanteZaak';
    hoofddeelZaakKeuzes: { label: string, value: ZaakRelatietype }[] = [];
    relevanteZaakKeuzes: { label: string, lebal: string, value: ZaakRelatietype }[] = [];

    constructor(
        public dialogRef: MatDialogRef<ZaakKoppelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakKoppelDialogGegevens,
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService,
        private utilService: UtilService,
        private zaakKoppelenService: ZaakKoppelenService,
        private translate: TranslateService) {
    }

    ngOnInit(): void {
        this.loading = true;

        forkJoin([
            this.zakenService.readZaak(this.data.bronZaakUuid),
            this.zakenService.readZaakByID(this.data.doelZaakIdentificatie)])
        .subscribe(([bronZaak, doelZaak]) => {
            this.bronZaak = bronZaak;
            this.doelZaak = doelZaak;

            if (!bronZaak.isDeelzaak && !doelZaak.isHoofdzaak && !doelZaak.isDeelzaak) {
                this.hoofddeelZaakKeuzes.push({
                    label: this.label(ZaakRelatietype.HOOFDZAAK, bronZaak, doelZaak),
                    value: ZaakRelatietype.HOOFDZAAK
                });
            }

            if (!bronZaak.isDeelzaak && !bronZaak.isHoofdzaak && !doelZaak.isDeelzaak) {
                this.hoofddeelZaakKeuzes.push({
                    label: this.label(ZaakRelatietype.DEELZAAK, bronZaak, doelZaak),
                    value: ZaakRelatietype.DEELZAAK
                });
            }

            this.relevanteZaakKeuzes.push({
                label: this.label(ZaakRelatietype.VERVOLG, bronZaak, doelZaak),
                lebal: this.label(ZaakRelatietype.VERVOLG, doelZaak, bronZaak),
                value: ZaakRelatietype.VERVOLG
            });

            this.relevanteZaakKeuzes.push({
                label: this.label(ZaakRelatietype.BIJDRAGE, bronZaak, doelZaak),
                lebal: this.label(ZaakRelatietype.BIJDRAGE, doelZaak, bronZaak),
                value: ZaakRelatietype.BIJDRAGE
            });

            this.relevanteZaakKeuzes.push({
                label: this.label(ZaakRelatietype.ONDERWERP, bronZaak, doelZaak),
                lebal: this.label(ZaakRelatietype.ONDERWERP, doelZaak, bronZaak),
                value: ZaakRelatietype.ONDERWERP
            });

            if (0 < this.hoofddeelZaakKeuzes.length) {
                this.hoofddeelZaakSelectFormField = new SelectFormFieldBuilder(
                    this.hoofddeelZaakKeuzes.length === 1 ? this.hoofddeelZaakKeuzes[0] : null)
                .id('hoofddeelkeuze')
                .label('relatieType.koppelen')
                .optionLabel('label')
                .validators(Validators.required)
                .options(this.hoofddeelZaakKeuzes).build();

                this.soortOptions.push(this.HOOFDDEEL);
            }

            if (0 < this.relevanteZaakKeuzes.length) {
                this.relevanteZaakBronSelectFormField = new SelectFormFieldBuilder(
                    this.relevanteZaakKeuzes.length === 1 ? this.relevanteZaakKeuzes[0] : null)
                .id('relevantebronkeuze')
                .label('relatieType.koppelen')
                .optionLabel('label')
                .validators(Validators.required)
                .options(this.relevanteZaakKeuzes).build();

                this.relevanteZaakDoelSelectFormField = new SelectFormFieldBuilder(
                    this.relevanteZaakKeuzes.length === 1 ? this.relevanteZaakKeuzes[0] : null)
                .id('relevantetekoppelenkeuze')
                .label('relatieType.koppelen.terug')
                .optionLabel('lebal')
                .options(this.relevanteZaakKeuzes).build();

                this.soortOptions.push(this.RELEVANTE);
            }

            this.soortRadioFormField = new RadioFormFieldBuilder(
                this.soortOptions.length == 1 ? this.soortOptions[0] : null)
            .id('relatiesoort')
            .label('relatieSoort')
            .options(this.soortOptions)
            .validators(Validators.required)
            .build();

            this.loading = false;
        });
    }

    private label(relatieType: ZaakRelatietype, bron: Zaak, doel: Zaak): string {
        return this.translate.instant('relatieType.koppelen.' + relatieType,
            {bron: bron.identificatie, doel: doel.identificatie});
    }

    isKoppelenToegestaan(): boolean {
        return 0 < this.soortOptions.length;
    }

    isSoortKiesbaar(): boolean {
        return 1 < this.soortOptions.length;
    }

    isSoortHoofdDeelZaak(): boolean {
        return this.soortRadioFormField?.formControl.value == this.HOOFDDEEL;
    }

    isSoortRelevanteZaak(): boolean {
        return this.soortRadioFormField?.formControl.value == this.RELEVANTE;
    }

    isValid(): boolean {
        if (this.isKoppelenToegestaan() &&
            this.soortRadioFormField.formControl.valid) {
            if (this.isSoortHoofdDeelZaak()) {
                return this.hoofddeelZaakSelectFormField.formControl.valid;
            }
            if (this.isSoortRelevanteZaak()) {
                return this.relevanteZaakBronSelectFormField.formControl.valid &&
                    this.relevanteZaakDoelSelectFormField.formControl.valid;
            }
        }
        return false;
    }

    close(): void {
        this.loading = true;
        this.zaakKoppelenService.addTeKoppelenZaak(this.bronZaak);
        this.dialogRef.close();
    }

    koppel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.koppelZaak(this.bronZaak, this.doelZaak, this.getRelatieType(), this.getRelatieTypeReverse());
    }

    private getRelatieType(): ZaakRelatietype {
        if (this.isSoortHoofdDeelZaak()) {
            return this.hoofddeelZaakSelectFormField.formControl.value.value;
        }
        if (this.isSoortRelevanteZaak()) {
            return this.relevanteZaakBronSelectFormField.formControl.value.value;
        }
        return null;
    }

    private getRelatieTypeReverse(): ZaakRelatietype {
        if (this.isSoortRelevanteZaak()) {
            return this.relevanteZaakDoelSelectFormField.formControl.value?.value;
        }
        return null;
    }

    private koppelZaak(bronZaak: Zaak, doelZaak: Zaak, relatieType: ZaakRelatietype, relatieTypeReverse: ZaakRelatietype) {
        const zaakKoppelGegevens = new ZaakKoppelGegevens();
        zaakKoppelGegevens.zaakUuid = doelZaak.uuid;
        zaakKoppelGegevens.teKoppelenZaakUuid = bronZaak.uuid;
        zaakKoppelGegevens.relatieType = relatieType;
        zaakKoppelGegevens.reverseRelatieType = relatieTypeReverse;
        this.zakenService.koppelZaak(zaakKoppelGegevens).subscribe({
            next: () => this.dialogRef.close(true),
            error: () => this.dialogRef.close(false)
        });
    }
}
