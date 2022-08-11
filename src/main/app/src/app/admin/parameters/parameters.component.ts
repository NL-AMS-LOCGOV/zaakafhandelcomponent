/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {MatSort} from '@angular/material/sort';
import {ZaakafhandelParametersListParameters} from './zaakafhandel-parameters-list-parameters';
import {ClientMatcher} from '../../shared/dynamic-table/filter/clientMatcher';
import {AdminComponent} from '../admin/admin.component';

@Component({
    templateUrl: './parameters.component.html',
    styleUrls: ['./parameters.component.less']
})
export class ParametersComponent extends AdminComponent implements OnInit, AfterViewInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('parametersSort') parametersSort: MatSort;

    filterParameters: ZaakafhandelParametersListParameters = new ZaakafhandelParametersListParameters('valide', 'asc');
    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();
    loading: boolean = false;

    constructor(private zaakafhandelParametersService: ZaakafhandelParametersService, public utilService: UtilService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.setupMenu('title.parameters');
        this.getZaakafhandelParameters();
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        this.parameters.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'omschrijving':
                    return item.zaaktype.omschrijving;
                case 'model':
                    return item.caseDefinition?.naam;
                case'geldig':
                    return item.zaaktype.nuGeldig;
                case'beginGeldigheid':
                    return item.zaaktype.beginGeldigheid;
                case'eindeGeldigheid':
                    return item.zaaktype.eindeGeldigheid;
                default:
                    return item[property];
            }
        };

        this.parameters.sort = this.parametersSort;
        this.parameters.filterPredicate = (data, filter) => {
            let match: boolean = true;

            const parsedFilter = JSON.parse(filter) as ZaakafhandelParametersListParameters;

            if (!!parsedFilter.valide) {
                match = match && ClientMatcher.matchBoolean(data.valide, (parsedFilter.valide === 'Valide'));
            }

            if (!!parsedFilter.geldig) {
                match = match && ClientMatcher.matchBoolean(data.zaaktype.nuGeldig, (parsedFilter.geldig === 'Ja'));
            }

            if (parsedFilter.beginGeldigheid.van !== null || parsedFilter.beginGeldigheid.tot !== null) {
                match = match && ClientMatcher.matchDatum(data.zaaktype.beginGeldigheid, parsedFilter.beginGeldigheid);
            }

            if (parsedFilter.eindeGeldigheid.van !== null || parsedFilter.eindeGeldigheid.tot !== null) {
                match = match && ClientMatcher.matchDatum(data.zaaktype.eindeGeldigheid, parsedFilter.eindeGeldigheid);
            }

            return match;
        };
    }

    applyFilter(): void {
        this.parameters.filter = JSON.stringify(this.filterParameters);
    }

    private getZaakafhandelParameters(): void {
        this.loading = true;
        this.zaakafhandelParametersService.listZaakafhandelParameters().subscribe(parameters => {
            this.loading = false;
            this.parameters.data = parameters;
        });
    }
}
