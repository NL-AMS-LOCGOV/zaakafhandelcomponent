/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {DocumentenTonenFormField} from './documenten-tonen-form-field';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {InformatieObjectenService} from '../../../../informatie-objecten/informatie-objecten.service';

@Component({
    templateUrl: './documenten-tonen.component.html',
    styleUrls: ['./documenten-tonen.component.less']
})
export class DocumentenTonenComponent implements OnInit, IFormComponent {

    data: DocumentenTonenFormField;
    documenten: EnkelvoudigInformatieobject[];
    columns: string[] = ['titel', 'documentType', 'status', 'versie', 'auteur', 'creatiedatum', 'bestandsomvang', 'url'];

    constructor(public informatieObjectenService: InformatieObjectenService) {
    }

    ngOnInit(): void {
        if (this.data.formControl.value) {
            const uuids = this.data.formControl.value.split(';');
            this.informatieObjectenService.readEnkelvoudigInformatieobjecten(uuids).subscribe(data => {
                this.documenten = data;
            })
            ;
        }
    }
}
