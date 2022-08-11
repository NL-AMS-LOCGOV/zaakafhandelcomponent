/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {ViewComponent} from '../../shared/abstract-view/view-component';
import {MatSort} from '@angular/material/sort';
import {ZaakafhandelParametersListParameters} from './zaakafhandel-parameters-list-parameters';
import {ClientMatcher} from '../../shared/dynamic-table/filter/clientMatcher';

@Component({
    templateUrl: './parameters.component.html',
    styleUrls: ['./parameters.component.less']
})
export class ParametersComponent extends ViewComponent implements OnInit, AfterViewInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('parametersSort') parametersSort: MatSort;

    filterParameters: ZaakafhandelParametersListParameters = new ZaakafhandelParametersListParameters('valide', 'asc');
    menu: MenuItem[] = [];
    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();
    loading: boolean = false;

    constructor(private zaakafhandelParametersService: ZaakafhandelParametersService, public utilService: UtilService) {
        super();
    }

    ngOnInit(): void {
        this.menu = [];
        this.menu.push(new HeaderMenuItem('actie.admin'));
        this.utilService.setTitle('title.parameters');
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
        this.zaakafhandelParametersService.listZaakafhandelParameters().subscribe(parameters => {
            this.loading = false;
            this.parameters.data = parameters;
        });
    }
}
