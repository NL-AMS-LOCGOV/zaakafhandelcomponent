/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormComponent} from '../../model/form-component';
import {DocumentenLijstFormField} from './documenten-lijst-form-field';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {SelectionModel} from '@angular/cdk/collections';
import {MatTableDataSource} from '@angular/material/table';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {DatumPipe} from '../../../pipes/datum.pipe';
import {InformatieObjectenService} from '../../../../informatie-objecten/informatie-objecten.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './documenten-lijst.component.html',
    styleUrls: ['./documenten-lijst.component.less']
})
export class DocumentenLijstComponent extends FormComponent implements OnInit {

    data: DocumentenLijstFormField;
    columns: string[] = ['select', 'titel', 'documentType', 'status', 'versie', 'auteur', 'creatiedatum', 'bestandsomvang', 'ondertekenen', 'url'];
    selection = new SelectionModel<EnkelvoudigInformatieobject>(true, []);
    teOndertekenenSelection = new SelectionModel<EnkelvoudigInformatieobject>(true, []);
    dataSource: MatTableDataSource<EnkelvoudigInformatieobject> = new MatTableDataSource<EnkelvoudigInformatieobject>();
    datumPipe = new DatumPipe('nl');
    loading = true;

    constructor(public translate: TranslateService, private informatieObjectenService: InformatieObjectenService) {
        super();
    }

    ngOnInit(): void {
        if (this.data.readonly) {
            this.columns.splice(this.columns.indexOf('select'), 1);
        }
        if (!this.data.ondertekenen) {
            this.columns.splice(this.columns.indexOf('ondertekenen'), 1);
        }
        this.data.documenten$.subscribe(documenten => {
            for (const document of documenten) {
                document.creatiedatum = this.datumPipe.transform(document.creatiedatum); // nodig voor zoeken
                document['viewLink'] = `/informatie-objecten/${document.uuid}`;
                document['downloadLink'] = this.informatieObjectenService.getDownloadURL(document.uuid);
                if (this.data.documentenCheckedVoorOndertekenen?.includes(document.uuid) || document.ondertekening) {
                    this.teOndertekenenSelection.toggle(document);
                }
            }
            this.dataSource.data = documenten;
            if (this.data.ondertekenen && this.teOndertekenenSelection.selected.length > 0) {
                this.data.formControl.setValue(this.teOndertekenenSelection.selected.map(value => value.uuid).join(';'));
            }
            this.loading = false;
        });
    }

    applyFilter($event: KeyboardEvent): void {
        const filterValue = ($event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();
    }

    updateSelected($event: MatCheckboxChange, document): void {
        if ($event) {
            this.selection.toggle(document);
            this.data.formControl.setValue(this.selection.selected.map(value => value.uuid).join(';'));
        }
    }

    updateTeOndertekenen($event: MatCheckboxChange, document): void {
        if ($event) {
            this.teOndertekenenSelection.toggle(document);
            this.data.formControl.setValue(this.teOndertekenenSelection.selected.map(value => value.uuid).join(';'));
        }
    }
}
