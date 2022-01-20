/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {SignaleringType} from '../../shared/signaleringen/signalering-type';
import {ZaakOverzicht} from '../../zaken/model/zaak-overzicht';
import {SignaleringenService} from '../../signaleringen.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';

@Component({
    selector: 'zac-zaken-card',
    templateUrl: './zaken-card.component.html',
    styleUrls: ['./zaken-card.component.less']
})
export class ZakenCardComponent implements OnInit, AfterViewInit {

    @Input() data: any;
    @ViewChild(MatPaginator) zakenPaginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    zakenDataSource: MatTableDataSource<ZaakOverzicht> = new MatTableDataSource<ZaakOverzicht>();
    zakenColumns: string[] = ['identificatie', 'zaaktype', 'omschrijving', 'url'];

    constructor(private signaleringenService: SignaleringenService) { }

    ngOnInit(): void {
        this.signaleringenService.listZakenSignalering(SignaleringType.ZAAK_OP_NAAM).subscribe(zaken => {
            this.zakenDataSource.data = zaken;
        });
    }

    ngAfterViewInit(): void {
        this.zakenDataSource.paginator = this.zakenPaginator;
        this.zakenDataSource.sort = this.sort;
    }

}
