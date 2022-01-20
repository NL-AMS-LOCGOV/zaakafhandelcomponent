/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {Observable, Subscription} from 'rxjs';
import {SignaleringenService} from '../../signaleringen.service';
import {Opcode} from '../websocket/model/opcode';
import {ObjectType} from '../websocket/model/object-type';
import {WebsocketService} from '../websocket/websocket.service';
import {WebsocketListener} from '../websocket/model/websocket-listener';

@Component({
    selector: 'zac-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.less']
})
export class ToolbarComponent implements OnInit, OnDestroy {

    headerTitle$: Observable<string>;
    hasNewSignaleringen: boolean;
    ingelogdeMedewerker: Medewerker;

    private subscription$: Subscription;
    private signaleringListener: WebsocketListener;

    constructor(public utilService: UtilService, public navigation: NavigationService, private identityService: IdentityService,
                private signaleringenService: SignaleringenService, private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.headerTitle$ = this.utilService.headerTitle$;
        this.identityService.readIngelogdeMedewerker().subscribe(medewerker => {
            this.ingelogdeMedewerker = medewerker;
            this.signaleringListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.SIGNALERINGEN,
                medewerker.gebruikersnaam,
                () => this.updateSignaleringen());
        });

    }

    ngOnDestroy(): void {
        this.subscription$.unsubscribe();
        this.websocketService.removeListener(this.signaleringListener);
    }

    updateSignaleringen(): void {
        this.subscription$ = this.signaleringenService.hasNewSignaleringen$.subscribe(
            value => this.hasNewSignaleringen = value);
    }

}
