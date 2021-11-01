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
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Operatie} from '../../core/websocket/model/operatie';
import {ObjectType} from '../../core/websocket/model/object-type';

@Component({
    templateUrl: './taak-toekennen.component.html',
    styleUrls: ['./taak-toekennen.component.less']
})
export class TaakToekennenComponent implements OnInit {

    formItems: Array<FormItem[]>;
    formConfig: FormConfig;
    taak: Taak;

    constructor(private route: ActivatedRoute, private identityService: IdentityService, private takenService: TakenService,
                private mfbService: MaterialFormBuilderService, private navigation: NavigationService, private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.taak = this.route.snapshot.data['taak'];
        this.websocketService.addListenerMetSnackbar(Operatie.WIJZIGING, ObjectType.TAAK, this.taak.id,
            () => this.updateTaak());
        this.initForm();
    }

    private initForm() {
        this.formConfig = new FormConfig('Toekennen', 'Annuleren');
        const titel = this.mfbService.createHeadingFormItem('toekennenTaak', 'Toekennen Taak', '1');
        const naam = this.mfbService.createReadonlyFormItem('naam', 'Naam', this.taak.naam);
        this.identityService.getMedewerkersInGroep(this.taak.groep.id).subscribe(medewerkers => {
            this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
                const medewerker = this.mfbService.createSelectFormItem('medewerker', 'medewerker',
                    this.taak.behandelaar ? this.taak.behandelaar : ingelogdeMedewerker, 'naam', medewerkers,
                    new FormFieldConfig([Validators.required]));
                this.formItems = [[titel], [naam], [medewerker]];
            });
        });
    }

    private updateTaak() {
        this.takenService.getTaak(this.taak.id).subscribe(taak => {
            this.taak.behandelaar = taak.behandelaar;
            this.initForm();
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
