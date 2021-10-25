/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Medewerker} from '../../identity/model/medewerker';
import {ZakenService} from '../../zaken/zaken.service';
import {TakenService} from '../../taken/taken.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Zaak} from '../../zaken/model/zaak';
import {Taak} from '../../taken/model/taak';
import {Operatie} from '../../core/websocket/model/operatie';
import {ObjectType} from '../../core/websocket/model/object-type';
import {WebsocketService} from '../../core/websocket/websocket.service';

@Component({
    selector: 'zac-behandelaar-veld',
    templateUrl: './behandelaar-veld.component.html',
    styleUrls: ['./behandelaar-veld.component.less']
})
export class BehandelaarVeldComponent implements OnInit {

    @Input() zaakUuid: string;
    @Input() taakId: string;
    @Input() behandelaar: Medewerker;
    @Input() laatKnopZien: boolean;
    @Output() behandelaarGewijzigd: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor(private zakenService: ZakenService, private takenService: TakenService, private snackbar: MatSnackBar, private websocketService: WebsocketService) { }

    ngOnInit(): void {
        this.websocketService.addListener(Operatie.WIJZIGING, ObjectType.ZAAK, this.zaakUuid, () => {});
    }

    toekennen() {
        if (!this.zaakUuid) {
            this.toekennenAanIngelogdeGebruikerViaTaak();
        } else {
            this.toekennenAanIngelogdeGebruikerViaZaak();
        }
    }

    toekennenAanIngelogdeGebruikerViaZaak() {
        let zaak: Zaak = new Zaak();
        zaak.uuid = this.zaakUuid;

        this.zakenService.toekennenAanIngelogdeMedewerker(zaak).subscribe(response => {
            this.geefBehandelaarWijzigingDoor(response.behandelaar);
            this.laatSnackbarZien(`Zaak toegekend aan ${response.behandelaar.naam}`);
            this.websocketService.removeListeners(Operatie.WIJZIGING, ObjectType.ZAAK, this.zaakUuid);
        });
    }

    toekennenAanIngelogdeGebruikerViaTaak() {
        let taak: Taak = new Taak();
        taak.id = this.taakId;
        taak.zaakUUID = this.zaakUuid;

        this.takenService.toekennenAanIngelogdeMedewerker(taak).subscribe(response => {
            this.geefBehandelaarWijzigingDoor(response.behandelaar);
            this.laatSnackbarZien(`Taak toegekend aan ${response.behandelaar.naam}`);
            this.websocketService.removeListeners(Operatie.WIJZIGING, ObjectType.TAAK, this.taakId);
        });
    }

    private geefBehandelaarWijzigingDoor(behandelaar: Medewerker) {
        this.behandelaar = behandelaar;
        this.behandelaarGewijzigd.emit(true);
    }

    private laatSnackbarZien(message: string) {
        this.snackbar.open(message, "Sluit", {
            duration: 3000,
        });
    }

}
