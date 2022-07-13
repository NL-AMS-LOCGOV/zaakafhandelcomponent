/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {User} from '../../identity/model/user';
import {Observable, Subscription} from 'rxjs';
import {SignaleringenService} from '../../signaleringen.service';
import {Opcode} from '../websocket/model/opcode';
import {ObjectType} from '../websocket/model/object-type';
import {WebsocketService} from '../websocket/websocket.service';
import {WebsocketListener} from '../websocket/model/websocket-listener';
import * as moment from 'moment';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {PolicyService} from '../../policy/policy.service';
import {AppActies} from '../../policy/model/app-acties';

@Component({
    selector: 'zac-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.less']
})
export class ToolbarComponent implements OnInit, OnDestroy {
    @Output() zoekenClicked = new EventEmitter<void>();
    headerTitle$: Observable<string>;
    hasNewSignaleringen: boolean;
    ingelogdeMedewerker: User;
    acties = new AppActies();

    private subscription$: Subscription;
    private signaleringListener: WebsocketListener;

    constructor(public utilService: UtilService, public navigation: NavigationService, private identityService: IdentityService,
                private signaleringenService: SignaleringenService, private websocketService: WebsocketService, private policyService: PolicyService) {
    }

    ngOnInit(): void {
        this.headerTitle$ = this.utilService.headerTitle$;
        this.identityService.readLoggedInUser().subscribe(medewerker => {
            this.ingelogdeMedewerker = medewerker;
            this.signaleringListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.SIGNALERINGEN,
                medewerker.id,
                () => this.signaleringenService.updateSignaleringen());
        });
        this.policyService.readAppActies().subscribe(appActies => this.acties = appActies);
        this.setSignaleringen();
    }

    ngOnDestroy(): void {
        this.subscription$.unsubscribe();
        this.websocketService.removeListener(this.signaleringListener);
    }

    setSignaleringen(): void {
        this.subscription$ = this.signaleringenService.latestSignalering$.subscribe(
            value => {
                // TODO instead of session storage use userpreferences in a db
                const dashboardLastOpenendStorage: string = SessionStorageUtil.getItem(
                    'dashboardOpened');
                if (!dashboardLastOpenendStorage) {
                    this.hasNewSignaleringen = !!value;
                } else {
                    const dashboardLastOpenendMoment: moment.Moment = moment(dashboardLastOpenendStorage,
                        moment.ISO_8601);

                    const newestSignalering: moment.Moment = moment(value, moment.ISO_8601);
                    this.hasNewSignaleringen = newestSignalering.isAfter(dashboardLastOpenendMoment);
                }
            }
        );
    }

}
