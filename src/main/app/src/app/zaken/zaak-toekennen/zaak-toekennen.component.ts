/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {Zaak} from '../model/zaak';
import {FormGroup} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute} from '@angular/router';
import {IdentityService} from '../../identity/identity.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

@Component({
    templateUrl: './zaak-toekennen.component.html',
    styleUrls: ['./zaak-toekennen.component.less']
})
export class ZaakToekennenComponent implements OnInit {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    zaak: Zaak;

    constructor(private route: ActivatedRoute, private identityService: IdentityService,
                private navigation: NavigationService, private zakenService: ZakenService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.zaak = this.route.snapshot.data['zaak'];

        this.utilService.setTitle('title.zaak.toekennen', {zaak: this.zaak.identificatie});

        this.initForm();
    }

    private initForm() {

        this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            // const medewerker = new SelectFormField('medewerker', 'medewerker',
            //     this.zaak.behandelaar ? this.zaak.behandelaar : ingelogdeMedewerker, 'naam',
            //     this.identityService.getMedewerkersInGroep(this.zaak.groep.id), new FormFieldConfig([Validators.required]));
            // this.formItems = [[medewerker]];
        });
        this.formConfig = new FormConfig('actie.toekennen', 'actie.annuleren');
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.zaak.behandelaar = formGroup.controls['medewerker']?.value;
            this.zakenService.toekennen(this.zaak).subscribe(() => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }

}
