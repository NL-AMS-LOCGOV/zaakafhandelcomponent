/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {FormGroup, Validators} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {ActivatedRoute} from '@angular/router';
import {Taak} from '../model/taak';
import {IdentityService} from '../../identity/identity.service';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {TakenService} from '../taken.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';

@Component({
    templateUrl: './taak-toekennen.component.html',
    styleUrls: ['./taak-toekennen.component.less']
})
export class TaakToekennenComponent implements OnInit {

    formItems: Array<FormItem[]>;
    formConfig: FormConfig;
    taak: Taak;

    constructor(private route: ActivatedRoute, private identityService: IdentityService, private takenService: TakenService,
                private mfbService: MaterialFormBuilderService, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.taak = this.route.snapshot.data['taak'];
        this.initForm();
    }

    private initForm() {
        this.formConfig = this.utilService.getFormConfig('actie.toekennen');
        const titel = this.mfbService.createHeadingFormItem('toekennenTaak', 'actie.taak.toekennen', '1');
        const naam = this.mfbService.createReadonlyFormItem('naam', 'naam', this.taak.naam);
        this.identityService.getMedewerkersInGroep(this.taak.groep.id).subscribe(medewerkers => {
            this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
                const medewerker = this.mfbService.createSelectFormItem('medewerker', 'medewerker',
                    this.taak.behandelaar ? this.taak.behandelaar : ingelogdeMedewerker, 'naam', medewerkers,
                    new FormFieldConfig([Validators.required]));
                this.formItems = [[titel], [naam], [medewerker]];
            });
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.taak.behandelaar = formGroup.controls['medewerker']?.value;
            this.takenService.toekennen(this.taak).subscribe(() => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }
}
