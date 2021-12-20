/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {sideNavToggle} from '../../shared/animations/animations';
import {State} from '../state/zaken.state';
import {Store} from '@ngrx/store';
import {isZaakVerkortCollapsed} from '../state/zaak-verkort.reducer';
import {toggleCollapseZaakVerkort} from '../state/zaak-verkort.actions';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {PageEvent} from '@angular/material/paginator';
import {UtilService} from '../../core/service/util.service';
import {Subscription} from 'rxjs';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';

@Component({
    selector: 'zac-zaak-verkort',
    templateUrl: './zaak-verkort.component.html',
    styleUrls: ['./zaak-verkort.component.less'],
    animations: [sideNavToggle]
})
export class ZaakVerkortComponent implements OnInit, OnDestroy {
    @Input() zaakUuid: string;
    @Output() zaakLoadedEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    zaak: Zaak;
    einddatumGeplandIcon: TextIcon;
    collapsed: boolean = false;
    enkelvoudigInformatieObjecten: EnkelvoudigInformatieobject[] = [];
    objectenColumnsToDisplay: string[] = ['titel', 'status', 'url'];
    lowValue: number = 0;
    highValue: number = 5;
    private subscriptions$: Subscription[] = [];
    private zaakListener: WebsocketListener;
    private zaakRollenListener: WebsocketListener;
    private zaakDocumentenListener: WebsocketListener;

    constructor(private store: Store<State>, private zakenService: ZakenService, private informatieObjectenService: InformatieObjectenService, public utilService: UtilService, private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.store.select(isZaakVerkortCollapsed).subscribe(isCollapsed => {
            this.collapsed = isCollapsed;
        }));

        this.loadZaak();

        this.subscriptions$.push(this.utilService.isTablet$.subscribe(isTablet => {
            if (isTablet && this.collapsed) {
                this.toggleMenu();
            }
        }));

        this.zaakListener = this.websocketService.addListener(Opcode.ANY, ObjectType.ZAAK, this.zaakUuid,
            (event) => this.loadZaak(event));
        this.zaakRollenListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_ROLLEN, this.zaakUuid,
            (event) => this.loadZaak(event));
        this.zaakDocumentenListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_INFORMATIEOBJECTEN, this.zaakUuid,
            (event) => this.loadInformatieObjecten(event));
    }

    toggleMenu(): void {
        this.store.dispatch(toggleCollapseZaakVerkort());
    }

    private loadInformatieObjecten(event?: ScreenEvent): void {
        if (event) {
            console.log('callback loadInformatieObjecten: ' + event.key);
        }
        this.informatieObjectenService.listEnkelvoudigInformatieobjectenVoorZaak(this.zaak.uuid).subscribe(objecten => {
            this.enkelvoudigInformatieObjecten = objecten;
        });
    }

    private loadZaak(event?: ScreenEvent) {
        if (event) {
            console.log('callback loadZaak: ' + event.key);
        }
        this.zakenService.readZaak(this.zaakUuid).subscribe(zaak => {
            this.zaak = zaak;
            this.einddatumGeplandIcon = new TextIcon(Conditionals.isAfterDate(this.zaak.einddatum), 'report_problem', 'warningZaakVerkortVerlopen_icon',
                'msg.datum.overschreden', 'warning');
            this.zaakLoadedEmitter.emit(true);
            this.loadInformatieObjecten();
        });
    }

    onPageChanged(event: PageEvent): PageEvent {
        this.lowValue = event.pageIndex * event.pageSize;
        this.highValue = this.lowValue + event.pageSize;
        return event;
    }

    ngOnDestroy(): void {
        this.websocketService.removeListener(this.zaakListener);
        this.websocketService.removeListener(this.zaakRollenListener);
        this.websocketService.removeListener(this.zaakDocumentenListener);
        this.subscriptions$.forEach(subscription$ => subscription$.unsubscribe());
    }
}
