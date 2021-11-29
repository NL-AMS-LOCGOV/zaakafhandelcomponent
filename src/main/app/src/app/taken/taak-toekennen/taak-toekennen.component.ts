/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute} from '@angular/router';
import {Taak} from '../model/taak';
import {IdentityService} from '../../identity/identity.service';
import {TakenService} from '../taken.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

@Component({
    templateUrl: './taak-toekennen.component.html',
    styleUrls: ['./taak-toekennen.component.less']
})
export class TaakToekennenComponent implements OnInit {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    taak: Taak;

    constructor(private route: ActivatedRoute, private identityService: IdentityService, private takenService: TakenService,
                private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.taak = this.route.snapshot.data['taak'];
        this.initForm();
    }

    private initForm() {
        this.utilService.setTitle('title.taak.toekennen', {taak: this.taak.naam});
        this.formConfig = new FormConfig('actie.toekennen', 'actie.annuleren');
        // const titel = new HeadingFormField('toekennenTaak', 'actie.taak.toekennen', '1');
        // const naam = new ReadonlyFormField('naam', 'naam', this.taak.naam);
        //
        // this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
        //     const medewerker = new SelectFormField('medewerker', 'medewerker',
        //         this.taak.behandelaar ? this.taak.behandelaar : ingelogdeMedewerker, 'naam',
        //         this.identityService.getMedewerkersInGroep(this.taak.groep.id), new FormFieldConfig([Validators.required]));
        //     this.formItems = [[titel], [naam], [medewerker]];
        // });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.taak.behandelaar = formGroup.controls['medewerker']?.value;
            this.takenService.assign(this.taak).subscribe(() => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }
}
