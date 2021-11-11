/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ZakenService} from '../zaken.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormGroup} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {UtilService} from '../../core/service/util.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {TextareaFormField} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

@Component({
    templateUrl: './zaak-edit.component.html',
    styleUrls: ['./zaak-edit.component.less']
})
export class ZaakEditComponent implements OnInit, OnDestroy {

    editZaakFields: Array<AbstractFormField[]>;
    private zaak: Zaak;
    formConfig: FormConfig;

    constructor(private zakenService: ZakenService,
                private navigation: NavigationService,
                private router: Router,
                private route: ActivatedRoute,
                private utilService: UtilService,
                private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.zaak = this.route.snapshot.data['zaak'];
        this.websocketService.addListenerMetSnackbar(Opcode.UPDATED, ObjectType.ZAAK, this.zaak.uuid,
            () => this.updateZaak());
        this.initForm();
    }

    ngOnDestroy(): void {
        this.websocketService.removeListeners(Opcode.UPDATED, ObjectType.ZAAK, this.zaak.uuid);
    }

    private initForm() {
        this.utilService.setTitle('title.zaak.wijzigen', {zaak: this.zaak.identificatie});

        this.formConfig = new FormConfig('actie.opslaan', 'actie.annuleren');

        const omschrijving =
            new TextareaFormField('omschrijving', 'omschrijving', this.zaak.omschrijving);
        const toelichting =
            new TextareaFormField('toelichting', 'toelichting', this.zaak.toelichting);
        this.editZaakFields = [[omschrijving], [toelichting]];
    }

    private updateZaak() {
        this.zakenService.getZaak(this.zaak.uuid).subscribe(zaak => {
            this.zaak = zaak;
            this.initForm();
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const patchData: Zaak = new Zaak();
            patchData.omschrijving = formGroup.controls['omschrijving'].value;
            patchData.toelichting = formGroup.controls['toelichting'].value;
            this.zakenService.updateZaak(this.zaak.uuid, patchData).subscribe(updatedZaak => {
                this.router.navigate(['/zaken/', updatedZaak.uuid]);
            });
        } else {
            this.navigation.back();
        }
    }
}

