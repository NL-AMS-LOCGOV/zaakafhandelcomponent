/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Taak} from '../model/taak';
import {MatButtonToggle} from '@angular/material/button-toggle';
import {TakenWerkvoorraadDatasource} from './taken-werkvoorraad-datasource';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {UtilService} from '../../core/service/util.service';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {TaakSortering} from '../model/taak-sortering';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {TaakRechten} from '../model/taak-rechten';

@Component({
    templateUrl: './taken-werkvoorraad.component.html',
    styleUrls: ['./taken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class TakenWerkvoorraadComponent implements AfterViewInit, OnInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<Taak>;
    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;

    dataSource: TakenWerkvoorraadDatasource;
    expandedRow: Taak | null;

    get taakRechten(): typeof TaakRechten {
        return TaakRechten;
    }

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService) {
    }

    ngOnInit() {
        this.utilService.setTitle('title.taken.werkvoorraad');

        this.dataSource = new TakenWerkvoorraadDatasource(this.takenService, this.utilService);

        const creatieDatum: TableColumn = new TableColumn('creatiedatumTijd', 'creatiedatumTijd', true, TaakSortering.CREATIEDATUM).pipe(DatumPipe);

        const streefDatum: TableColumn = new TableColumn('streefdatum', 'streefdatum', true, TaakSortering.STREEFDATUM).pipe(DatumPipe);

        this.dataSource.columns = [
            new TableColumn('naam', 'naam', true, TaakSortering.TAAKNAAM),
            new TableColumn('zaakIdentificatie', 'zaakIdentificatie', true),
            new TableColumn('zaaktypeOmschrijving', 'zaaktypeOmschrijving', true),
            creatieDatum,
            streefDatum,
            new TableColumn('groep', 'groep.naam', true),
            new TableColumn('behandelaar', 'behandelaar.naam', true, TaakSortering.BEHANDELAAR),
            new TableColumn('url', 'url', true, null, true)
        ];
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }

    toekennenAanIngelogdeMedewerker(taak: Taak, event) {
        event.stopPropagation();
        this.takenService.toekennenAanIngelogdeMedewerker(taak).subscribe(taakResponse => {
            taak['behandelaar.naam'] = taakResponse.behandelaar.naam;
            taak.rechten = taakResponse.rechten;
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taakResponse.behandelaar.naam});
        });
    }
}
