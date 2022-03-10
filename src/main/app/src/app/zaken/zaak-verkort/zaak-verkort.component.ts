/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {sideNavToggle} from '../../shared/animations/animations';
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
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {Router} from '@angular/router';

@Component({
    selector: 'zac-zaak-verkort',
    templateUrl: './zaak-verkort.component.html',
    styleUrls: ['./zaak-verkort.component.less'],
    animations: [sideNavToggle]
})
export class ZaakVerkortComponent implements OnInit, OnDestroy {
    @Input() zaakUuid: string;
    @Output() zaakToggle: EventEmitter<void> = new EventEmitter<void>();

    zaak: Zaak;
    einddatumGeplandIcon: TextIcon;
    visibilityIcon = new TextIcon(Conditionals.always, 'visibility', 'visibility_icon', '', 'pointer');
    collapsed: boolean = false;
    enkelvoudigInformatieObjecten: EnkelvoudigInformatieobject[] = [];
    objectenColumnsToDisplay: string[] = ['titel', 'status', 'url'];
    lowValue: number = 0;
    highValue: number = 5;
    private subscriptions$: Subscription[] = [];
    private zaakListener: WebsocketListener;
    private zaakRollenListener: WebsocketListener;
    private zaakDocumentenListener: WebsocketListener;

    constructor(private zakenService: ZakenService, private router: Router, private informatieObjectenService: InformatieObjectenService,
                public utilService: UtilService, private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
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
        this.collapsed = !this.collapsed;
        this.zaakToggle.emit();
    }

    private loadInformatieObjecten(event?: ScreenEvent): void {
        if (event) {
            console.log('callback loadInformatieObjecten: ' + event.key);
        }
        const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekParameters.zaakUUID = this.zaak.uuid;
        this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters).subscribe(objecten => {
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
            this.zaakToggle.emit();
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

    openZaak(): void {
        this.router.navigate(['/zaken/', this.zaak.identificatie]);
    }
}
