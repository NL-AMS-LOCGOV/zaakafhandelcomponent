/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Medewerker} from '../../identity/model/medewerker';
import {ZakenService} from '../../zaken/zaken.service';
import {TakenService} from '../../taken/taken.service';
import {Zaak} from '../../zaken/model/zaak';
import {Taak} from '../../taken/model/taak';
import {UtilService} from '../../core/service/util.service';

@Component({
    selector: 'zac-behandelaar-veld',
    templateUrl: './behandelaar-veld.component.html',
    styleUrls: ['./behandelaar-veld.component.less']
})
export class BehandelaarVeldComponent {

    @Input() zaakUuid: string;
    @Input() taakId: string;
    @Input() behandelaar: Medewerker;
    @Input() laatKnopZien: boolean;
    @Output() behandelaarGewijzigd: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor(private zakenService: ZakenService, private takenService: TakenService, private utilService: UtilService) { }

    toekennen() {
        if (!this.taakId) {
            this.toekennenAanIngelogdeGebruikerViaZaak();
        } else {
            this.toekennenAanIngelogdeGebruikerViaTaak();
        }
    }

    toekennenAanIngelogdeGebruikerViaZaak() {
        let zaak: Zaak = new Zaak();
        zaak.uuid = this.zaakUuid;

        this.zakenService.toekennenAanIngelogdeMedewerker(zaak).subscribe(response => {
            this.geefBehandelaarWijzigingDoor(response.behandelaar);
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: response.behandelaar.naam});
        });
    }

    toekennenAanIngelogdeGebruikerViaTaak() {
        let taak: Taak = new Taak();
        taak.id = this.taakId;
        taak.zaakUUID = this.zaakUuid;

        this.takenService.assignToLoggedOnUser(taak).subscribe(response => {
            this.geefBehandelaarWijzigingDoor(response.behandelaar);
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: response.behandelaar.naam});
        });
    }

    private geefBehandelaarWijzigingDoor(behandelaar: Medewerker) {
        this.behandelaar = behandelaar;
        this.behandelaarGewijzigd.emit(true);
    }
}
