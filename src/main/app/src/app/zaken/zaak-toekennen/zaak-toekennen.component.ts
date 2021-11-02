/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {Zaak} from '../model/zaak';
import {FormGroup, Validators} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute} from '@angular/router';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {IdentityService} from '../../identity/identity.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Operatie} from '../../core/websocket/model/operatie';
import {ObjectType} from '../../core/websocket/model/object-type';

@Component({
    templateUrl: './zaak-toekennen.component.html',
    styleUrls: ['./zaak-toekennen.component.less']
})
export class ZaakToekennenComponent implements OnInit, OnDestroy {

    formItems: Array<FormItem[]>;
    formConfig: FormConfig;
    zaak: Zaak;

    constructor(private route: ActivatedRoute, private mfbService: MaterialFormBuilderService, private identityService: IdentityService,
                private navigation: NavigationService, private zakenService: ZakenService, private utilService: UtilService, private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.zaak = this.route.snapshot.data['zaak'];

        this.utilService.setTitle('title.zaak.toekennen', {zaak: this.zaak.identificatie});

        this.websocketService.addListenerMetSnackbar(Operatie.WIJZIGING, ObjectType.ZAAK, this.zaak.uuid,
            () => this.updateZaak());

        this.initForm();
    }

    ngOnDestroy(): void {
        this.websocketService.removeListeners(Operatie.WIJZIGING, ObjectType.ZAAK, this.zaak.uuid);
    }

    private updateZaak() {
        this.zakenService.getZaak(this.zaak.uuid).subscribe(zaak => {
            this.zaak.behandelaar = zaak.behandelaar;
            this.initForm();
        });
    }

    private initForm() {
        this.identityService.getMedewerkersInGroep(this.zaak.groep.id).subscribe(medewerkers => {
            this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
                const medewerker = this.mfbService.createSelectFormItem('medewerker', 'medewerker',
                    this.zaak.behandelaar ? this.zaak.behandelaar : ingelogdeMedewerker, 'naam', medewerkers,
                    new FormFieldConfig([Validators.required]));
                this.formItems = [[medewerker]];
            });
        });
        this.formConfig = this.utilService.getFormConfig('actie.toekennen');
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
