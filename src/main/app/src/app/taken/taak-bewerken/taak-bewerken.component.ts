/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {Taak} from '../model/taak';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {FormGroup} from '@angular/forms';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';

@Component({
    templateUrl: './taak-bewerken.component.html',
    styleUrls: ['./taak-bewerken.component.less']
})
export class TaakBewerkenComponent implements OnInit {

    formItems: Array<FormItem[]>;
    formConfig: FormConfig;
    taak: Taak;

    constructor(private route: ActivatedRoute, private takenService: TakenService, private mfbService: MaterialFormBuilderService,
                private navigation: NavigationService,
                private titleService: Title,
                private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.taak = this.route.snapshot.data['taak'];
        this.titleService.setTitle(`Taak '${this.taak.naam}' wijzigen`);
        this.utilService.setHeaderTitle(`Taak '${this.taak.naam}' wijzigen`);
        this.formConfig = new FormConfig('Bewerken', 'Annuleren');
        const toelichting = this.mfbService.createTextareaFormItem('toelichting', 'Toelichting', this.taak.toelichting);
        this.formItems = [[toelichting]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.taak.toelichting = formGroup.controls['toelichting'] ? formGroup.controls['toelichting'].value : null;
            this.takenService.bewerken(this.taak).subscribe(() => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }
}
