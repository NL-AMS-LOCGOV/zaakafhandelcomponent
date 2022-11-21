/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {GebruikersvoorkeurenService} from '../gebruikersvoorkeuren.service';
import {Zoekopdracht} from '../model/zoekopdracht';
import {Werklijst} from '../model/werklijst';
import {Subscription} from 'rxjs';
import {ZoekopdrachtSaveDialogComponent} from '../zoekopdracht-save-dialog/zoekopdracht-save-dialog.component';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ListParameters} from '../../shared/model/list-parameters';
import {DatumRange} from '../../zoeken/model/datum-range';

@Component({
    selector: 'zac-zoekopdracht',
    templateUrl: './zoekopdracht.component.html',
    styleUrls: ['./zoekopdracht.component.less']
})
export class ZoekopdrachtComponent implements OnInit, OnDestroy {

    @Input() werklijst: Werklijst;
    @Input() zoekParameters: ZoekParameters | ListParameters;
    @Output() zoekopdracht = new EventEmitter<Zoekopdracht>();
    @Input() filtersChanged: EventEmitter<void>;

    zoekopdrachten: Zoekopdracht[] = [];
    actieveZoekopdracht: Zoekopdracht;
    actieveFilters: boolean;
    filtersChangedSubscription$: Subscription;

    constructor(private gebruikersvoorkeurenService: GebruikersvoorkeurenService, private dialog: MatDialog) { }

    ngOnDestroy(): void {
        this.filtersChangedSubscription$.unsubscribe();
    }

    ngOnInit(): void {
        this.loadZoekopdrachten();
        this.filtersChangedSubscription$ = this.filtersChanged.subscribe(() => {
            this.clearActief();
        });
    }

    private heeftZoekParameters(): boolean {
        const parameters: any = this.zoekParameters;
        if (parameters != null) {
            if (parameters.zoeken) {
                for (const field in parameters.zoeken) {
                    if (parameters.zoeken.hasOwnProperty(field)) {
                        if (parameters.zoeken[field] != null) {
                            return true;
                        }
                    }
                }
            }
            if (parameters.filters) {
                for (const field in parameters.filters) {
                    if (parameters.filters.hasOwnProperty(field)) {
                        if (parameters.filters[field] != null) {
                            return true;
                        }
                    }
                }
            }
            if (parameters.datums) {
                for (const field in parameters.datums) {
                    if (parameters.datums.hasOwnProperty(field)) {
                        const datum: DatumRange = parameters.datums[field];
                        if (datum && datum.hasValue()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    saveSearch(): void {
        const dialogRef = this.dialog.open(ZoekopdrachtSaveDialogComponent, {
            data: {zoekopdrachten: this.zoekopdrachten, lijstID: this.werklijst, zoekopdracht: this.zoekParameters}
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadZoekopdrachten();
            }
        });
    }

    setActief(zoekopdracht: Zoekopdracht): void {
        this.actieveZoekopdracht = zoekopdracht;
        this.actieveFilters = true;
        this.zoekopdracht.emit(this.actieveZoekopdracht);
        this.gebruikersvoorkeurenService.setZoekopdrachtActief(this.actieveZoekopdracht).subscribe();
    }

    deleteZoekopdracht($event: MouseEvent, zoekopdracht: Zoekopdracht): void {
        $event.stopPropagation();
        this.gebruikersvoorkeurenService.deleteZoekOpdrachten(zoekopdracht.id).subscribe(() => {
            this.loadZoekopdrachten();
        });
    }

    clearActief(emit?: boolean): void {
        this.actieveZoekopdracht = null;
        this.gebruikersvoorkeurenService.removeZoekopdrachtActief(this.werklijst).subscribe();
        if (emit) {
            this.actieveFilters = false;
            this.zoekopdracht.emit(this.actieveZoekopdracht);
        } else {
            this.actieveFilters = this.heeftZoekParameters();
        }
    }

    private loadZoekopdrachten(): void {
        this.gebruikersvoorkeurenService.listZoekOpdrachten(this.werklijst).subscribe(zoekopdrachten => {
            this.zoekopdrachten = zoekopdrachten;
            this.actieveZoekopdracht = zoekopdrachten.find(z => z.actief);
            this.actieveFilters = this.heeftZoekParameters();
            this.zoekopdracht.emit(this.actieveZoekopdracht);
        });
    }
}
