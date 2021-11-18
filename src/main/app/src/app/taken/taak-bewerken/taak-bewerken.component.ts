/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {Taak} from '../model/taak';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {FormGroup} from '@angular/forms';
import {UtilService} from '../../core/service/util.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {TextareaFormField} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

@Component({
    templateUrl: './taak-bewerken.component.html',
    styleUrls: ['./taak-bewerken.component.less']
})
export class TaakBewerkenComponent implements OnInit, OnDestroy {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    taak: Taak;

    constructor(private route: ActivatedRoute, private takenService: TakenService,
                private navigation: NavigationService,
                private utilService: UtilService,
                private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.taak = this.route.snapshot.data['taak'];
        this.utilService.setTitle('title.taak.wijzigen', {taak: this.taak.naam});
        this.formConfig = new FormConfig('actie.bewerken', 'actie.annuleren');
        this.initToelichtingVeld();
        this.websocketService.addListenerMetSnackbar(Opcode.UPDATED, ObjectType.TAAK, this.taak.id,
            () => this.updateTaak());
        this.websocketService.addListenerMetSnackbar(Opcode.DELETED, ObjectType.TAAK, this.taak.id,
            () => this.updateTaak());
    }

    ngOnDestroy() {
        this.websocketService.removeListeners(Opcode.UPDATED, ObjectType.TAAK, this.taak.id);
        this.websocketService.removeListeners(Opcode.DELETED, ObjectType.TAAK, this.taak.id);
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

    private initToelichtingVeld() {
        const toelichting = new TextareaFormField('toelichting', 'toelichting', this.taak.toelichting);
        this.formItems = [[toelichting]];
    }

    private updateTaak() {
        this.takenService.getTaak(this.taak.id).subscribe(taak => {
            this.taak = taak;
            this.initToelichtingVeld();
        });
    }
}
