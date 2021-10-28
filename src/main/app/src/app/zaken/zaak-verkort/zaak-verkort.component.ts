/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {sideNavToggle} from '../../shared/animations/animations';
import {State} from '../state/zaken.state';
import {Store} from '@ngrx/store';
import {isZaakVerkortCollapsed} from '../state/zaak-verkort.reducer';
import {toggleCollapseZaakVerkort} from '../state/zaak-verkort.actions';
import {EnkelvoudigInformatieObject} from '../../informatie-objecten/model/enkelvoudig-informatie-object';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {PageEvent} from '@angular/material/paginator';
import {UtilService} from '../../core/service/util.service';
import {Subscription} from 'rxjs';

@Component({
    selector: 'zac-zaak-verkort',
    templateUrl: './zaak-verkort.component.html',
    styleUrls: ['./zaak-verkort.component.less'],
    animations: [sideNavToggle]
})
export class ZaakVerkortComponent implements OnInit {
    @Input() zaakUuid: string;
    @Output() zaakLoadedEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    zaak: Zaak;
    collapsed: boolean = false;
    enkelvoudigInformatieObjecten: EnkelvoudigInformatieObject[] = [];
    objectenColumnsToDisplay: string[] = ['titel', 'status', 'url'];
    lowValue: number = 0;
    highValue: number = 5;
    private subscriptions$: Subscription[] = [];

    constructor(private store: Store<State>, private zakenService: ZakenService, private informatieObjectenService: InformatieObjectenService, public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.store.select(isZaakVerkortCollapsed).subscribe(isCollapsed => {
            this.collapsed = isCollapsed;
        }));

        this.zakenService.getZaak(this.zaakUuid).subscribe(zaak => {
            this.zaak = zaak;
            this.zaakLoadedEmitter.emit(true);
            this.loadInformatieObjecten();
        });

        this.subscriptions$.push(this.utilService.isTablet$.subscribe(isTablet => {
            if (isTablet && this.collapsed) {
                this.toggleMenu();
            }
        }));
    }

    toggleMenu(): void {
        this.store.dispatch(toggleCollapseZaakVerkort());
    }

    private loadInformatieObjecten(): void {
        this.informatieObjectenService.getEnkelvoudigInformatieObjectenVoorZaak(this.zaak.uuid).subscribe(objecten => {
            this.enkelvoudigInformatieObjecten = objecten;
        });
    }

    onPageChanged(event: PageEvent): PageEvent {
        this.lowValue = event.pageIndex * event.pageSize;
        this.highValue = this.lowValue + event.pageSize;
        return event;
    }

    ngOnDestroy(): void {
        this.subscriptions$.forEach(subscription$ => subscription$.unsubscribe());
    }
}
