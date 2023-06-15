/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {FormulierVeldDefinitie} from '../../../../admin/model/formulieren/formulier-veld-definitie';
import {MatTableDataSource} from '@angular/material/table';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {InformatieObjectenService} from '../../../../informatie-objecten/informatie-objecten.service';
import {Zaak} from '../../../../zaken/model/zaak';
import {InformatieobjectZoekParameters} from '../../../../informatie-objecten/model/informatieobject-zoek-parameters';
import {SelectionModel} from '@angular/cdk/collections';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {Observable, of} from 'rxjs';

@Component({
    selector: 'zac-documenten-formulier-veld',
    templateUrl: './documenten-formulier-veld.component.html',
    styleUrls: ['./documenten-formulier-veld.component.less']
})
export class DocumentenFormulierVeldComponent implements OnInit {

    @Input() veldDefinitie: FormulierVeldDefinitie;
    @Input() control: FormControl;
    @Input() zaak: Zaak;
    columns: string[] = ['select', 'titel', 'documentType', 'status', 'versie', 'auteur', 'creatiedatum', 'bestandsomvang', 'indicaties', 'url'];
    loading = false;

    dataSource: MatTableDataSource<EnkelvoudigInformatieobject> = new MatTableDataSource<EnkelvoudigInformatieobject>();
    selection = new SelectionModel<EnkelvoudigInformatieobject>(true, []);

    constructor(public informatieObjectenService: InformatieObjectenService) {
    }

    ngOnInit(): void {
        this.ophalenDocumenten();
    }

    ophalenDocumenten() {
        let observable: Observable<EnkelvoudigInformatieobject[]>;
        if (this.veldDefinitie.meerkeuzeOpties === 'ZAAK_VERZENDBAAR') {
            observable = this.informatieObjectenService.listInformatieobjectenVoorVerzenden(this.zaak.uuid);
        } else if (this.veldDefinitie.meerkeuzeOpties === 'ZAAK') {
            const zoekparameters = new InformatieobjectZoekParameters();
            zoekparameters.zaakUUID = this.zaak.uuid;
            observable = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        } else {
            observable = this.getDocumentenVariable();
        }
        observable.subscribe(documenten => {
            this.dataSource.data = documenten;
        });
    }

    toggleCheckbox($event: MatCheckboxChange, document): void {
        this.selection.toggle(document);
        this.control.setValue(this.selection.selected.map(value => value.uuid).join(';'));
    }

    selectDisabled(document): boolean {
        return this.control.disabled;
    }

    isSelected(document): boolean {
        return this.selection.isSelected(document);
    }

    getDocumentenVariable(): Observable<EnkelvoudigInformatieobject[]> {
        const uuids: string = this.zaak.zaakdata[this.veldDefinitie.meerkeuzeOpties];
        if (uuids) {
            const zoekparameters = new InformatieobjectZoekParameters();
            zoekparameters.informatieobjectUUIDs = uuids.split(';');
            return this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        }
        return of([]);
    }
}
