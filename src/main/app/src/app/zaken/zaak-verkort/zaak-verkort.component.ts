/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {UtilService} from '../../core/service/util.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {Router} from '@angular/router';
import {Observable, share} from 'rxjs';
import {SkeletonLayout} from '../../shared/skeleton-loader/skeleton-loader-options';
import {IndicatiesLayout} from '../../shared/indicaties/indicaties.component';

@Component({
    selector: 'zac-zaak-verkort',
    templateUrl: './zaak-verkort.component.html',
    styleUrls: ['./zaak-verkort.component.less']
})
export class ZaakVerkortComponent implements OnInit, OnDestroy {
    @Input() zaak$: Observable<Zaak>;
    readonly indicatiesLayout = IndicatiesLayout;
    zaakUuid: string;
    einddatumGeplandIcon: TextIcon;
    skeletonLayout = SkeletonLayout;

    private zaakListener: WebsocketListener;
    private zaakRollenListener: WebsocketListener;
    private zaakDocumentenListener: WebsocketListener;

    constructor(private zakenService: ZakenService, private router: Router, private informatieObjectenService: InformatieObjectenService,
                public utilService: UtilService, private websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        this.zaak$.subscribe(zaak => {
            this.zaakUuid = zaak.uuid;
            this.zaakListener = this.websocketService.addListener(Opcode.ANY, ObjectType.ZAAK, zaak.uuid,
                (event) => this.loadZaak(event));
        });
    }

    private loadZaak(event?: ScreenEvent) {
        if (event) {
            console.debug('callback loadZaak: ' + event.key);
        }
        this.zaak$ = this.zakenService.readZaak(this.zaakUuid).pipe(share());
        this.zaak$.subscribe(zaak => {
            this.einddatumGeplandIcon = new TextIcon(Conditionals.isAfterDate(zaak.einddatum), 'report_problem', 'warningZaakVerkortVerlopen_icon',
                'msg.datum.overschreden', 'warning');
        });
    }

    ngOnDestroy(): void {
        this.websocketService.removeListener(this.zaakListener);
        this.websocketService.removeListener(this.zaakRollenListener);
        this.websocketService.removeListener(this.zaakDocumentenListener);
    }
}
