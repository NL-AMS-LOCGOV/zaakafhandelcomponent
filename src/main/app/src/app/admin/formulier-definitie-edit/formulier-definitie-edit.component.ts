/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {IdentityService} from '../../identity/identity.service';
import {AdminComponent} from '../admin/admin.component';
import {MatDialog} from '@angular/material/dialog';
import {FormulierDefinitieService} from '../formulier-defintie.service';
import {FormulierDefinitie} from '../model/formulieren/formulier-definitie';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {FormulierVeldDefinitie} from '../model/formulieren/formulier-veld-definitie';
import {MatTableDataSource} from '@angular/material/table';
import {FormulierVeldType} from '../model/formulieren/formulier-veld-type.enum';
import {MatSelectChange} from '@angular/material/select';

@Component({
    templateUrl: './formulier-definitie-edit.component.html',
    styleUrls: ['./formulier-definitie-edit.component.less']
})
export class FormulierDefinitieEditComponent extends AdminComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    definitie: FormulierDefinitie;
    definitieFormGroup: FormGroup;
    veldColumns = ['label', 'systeemnaam', 'beschrijving', 'helptekst', 'veldtype', 'defaultWaarde', 'verplicht', 'meerkeuzeOpties', 'actions'];
    vorigeSysteemnaam: string;

    dataSource: MatTableDataSource<AbstractControl>;

    constructor(private identityService: IdentityService,
                private service: FormulierDefinitieService,
                public dialog: MatDialog,
                private route: ActivatedRoute,
                private formBuilder: FormBuilder,
                public utilService: UtilService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.definitie = data.definitie;
            if (this.definitie.id) {
                this.setupMenu('title.formulierdefinitie.edit');
            } else {
                this.setupMenu('title.formulierdefinitie.add');
            }
            this.init();
        });
    }

    private init(): void {
        this.vorigeSysteemnaam = this.definitie.systeemnaam;

        if (!this.definitie.veldDefinities?.length) {
            this.definitie.veldDefinities = [];
        }

        this.definitieFormGroup = this.formBuilder.group({
            naam: [this.definitie.naam, [Validators.required]],
            systeemnaam: [{value: this.definitie.systeemnaam, disabled: !!this.definitie.id}, [Validators.required, Validators.pattern('[a-z0-9_-]*')]],
            beschrijving: [this.definitie.beschrijving, [Validators.required], Validators.maxLength(200)],
            uitleg: [this.definitie.uitleg],
            veldDefinities: this.formBuilder.array(this.definitie.veldDefinities.map(veld => FormulierVeldDefinitie.asFormGroup(veld)))
        });

        this.dataSource = new MatTableDataSource((this.definitieFormGroup.get('veldDefinities') as FormArray).controls);
    }

    updateSysteemnaam() {
        const naam = this.definitieFormGroup.get('naam').value;
        const systeemnaam = this.toSysteemNaam(naam);
        // tslint:disable-next-line:triple-equals
        if (this.definitieFormGroup.get('systeemnaam').value == this.vorigeSysteemnaam) {
            this.definitieFormGroup.get('systeemnaam').setValue(systeemnaam);
            this.vorigeSysteemnaam = systeemnaam;
        }
    }

    updateSysteemnaamVeld(formgroup: FormGroup) {
        const isNew = !formgroup.get('id').value;
        if (isNew) { // systeemnaam niet upadten bij bewerken
            const label = formgroup.get('label').value;
            formgroup.get('systeemnaam').setValue(this.toSysteemNaam(label));
        }
    }

    toSysteemNaam(naam: string): string {
        return naam.replace(/[^a-zA-Z0-9 ]/g, '').replace(/\s/g, '-').toLowerCase();
    }

    addVeldDefinities() {
        const veldDefinities = this.definitieFormGroup.get('veldDefinities') as FormArray;
        veldDefinities.push(FormulierVeldDefinitie.asFormGroup(new FormulierVeldDefinitie()));
        this.dataSource.data = veldDefinities.controls;
    }

    removeVeldDefinitie(formgroup: FormGroup) {
        const veldDefinities = this.definitieFormGroup.get('veldDefinities') as FormArray;
        veldDefinities.removeAt(veldDefinities.controls.indexOf(formgroup));
        this.dataSource.data = veldDefinities.controls;
    }

    onVeldTypeChange($event: MatSelectChange, element): void {

    }

    getVeldTypes(): string[] {
        return Object.keys(FormulierVeldType);
    }

    opslaan(): void {

    }
}
