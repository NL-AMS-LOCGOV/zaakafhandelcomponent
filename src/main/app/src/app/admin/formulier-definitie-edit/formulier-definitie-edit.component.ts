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
import {ActivatedRoute, Router} from '@angular/router';
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
    veldColumns = ['label', 'systeemnaam', 'beschrijving', 'helptekst', 'veldType', 'defaultWaarde', 'verplicht', 'meerkeuzeOpties', 'volgorde', 'acties'];
    vorigeSysteemnaam: string;
    bezigMetOpslaan = false;

    dataSource: MatTableDataSource<AbstractControl>;

    constructor(private identityService: IdentityService,
                private service: FormulierDefinitieService,
                public dialog: MatDialog,
                private route: ActivatedRoute,
                private formBuilder: FormBuilder,
                private router: Router,
                public utilService: UtilService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.definitie = data.definitie;
            this.init();
        });
    }

    private init(): void {
        if (this.definitie.id) {
            this.setupMenu('title.formulierdefinitie.edit');
        } else {
            this.setupMenu('title.formulierdefinitie.add');
        }

        this.vorigeSysteemnaam = this.definitie.systeemnaam;

        if (!this.definitie.veldDefinities?.length) {
            this.definitie.veldDefinities = [];
        }

        this.definitieFormGroup = this.formBuilder.group({
            id: [this.definitie.id],
            naam: [this.definitie.naam, [Validators.required]],
            systeemnaam: [{
                value: this.definitie.systeemnaam,
                disabled: !!this.definitie.id
            }, [Validators.required, Validators.pattern('[a-z0-9_-]*')]],
            beschrijving: [this.definitie.beschrijving, [Validators.required, Validators.maxLength(200)]],
            uitleg: [this.definitie.uitleg],
            veldDefinities: this.formBuilder.array(this.definitie.veldDefinities.map(veld => FormulierVeldDefinitie.asFormGroup(veld)))
        });
        (this.definitieFormGroup.get('veldDefinities') as FormArray).addValidators(Validators.required); // minimaal 1 veld definitie
        this.dataSource = new MatTableDataSource((this.definitieFormGroup.get('veldDefinities') as FormArray).controls);
    }

    updateSysteemnaam() {
        const isNew = !this.definitieFormGroup.get('id').value;
        const naam = this.definitieFormGroup.get('naam').value;
        const systeemnaam = this.toSysteemNaam(naam);
        // tslint:disable-next-line:triple-equals
        if (isNew && this.definitieFormGroup.get('systeemnaam').value == this.vorigeSysteemnaam) {
            this.definitieFormGroup.get('systeemnaam').setValue(systeemnaam);
            this.vorigeSysteemnaam = systeemnaam;
        }
    }

    updateSysteemnaamVeld(formgroup: FormGroup) {
        const isNew = !formgroup.get('id').value;
        if (isNew) { // systeemnaam niet aanpassen bij bewerken
            const label = formgroup.get('label').value;
            formgroup.get('systeemnaam').setValue(this.toSysteemNaam(label));
        }
    }

    toSysteemNaam(naam: string): string {
        return naam.replace(/[^a-zA-Z0-9 ]/g, '').replace(/\s/g, '-').toLowerCase();
    }

    addVeldDefinities() {
        const veldDefinities = this.definitieFormGroup.get('veldDefinities') as FormArray;
        const vd = new FormulierVeldDefinitie();
        vd.volgorde = veldDefinities.length + 1;
        const formGroup = FormulierVeldDefinitie.asFormGroup(vd);
        veldDefinities.push(formGroup);
        this.dataSource.data = veldDefinities.controls;
    }

    removeVeldDefinitie(formgroup: FormGroup) {
        const veldDefinities = this.definitieFormGroup.get('veldDefinities') as FormArray;
        veldDefinities.removeAt(veldDefinities.controls.indexOf(formgroup));
        this.dataSource.data = veldDefinities.controls;
    }

    onVeldTypeChange($event: MatSelectChange, veldDefinitieFormGroup: FormGroup): void {
        console.log($event, veldDefinitieFormGroup);
        const veldType: FormulierVeldType = $event.value;
        if (veldType === FormulierVeldType.CHECKBOXES || veldType === FormulierVeldType.RADIO || veldType === FormulierVeldType.KEUZELIJST) {
            veldDefinitieFormGroup.get('meerkeuzeOpties').enable();
            veldDefinitieFormGroup.get('meerkeuzeOpties').setValidators(Validators.required);
        } else {
            veldDefinitieFormGroup.get('meerkeuzeOpties').removeValidators(Validators.required);
            veldDefinitieFormGroup.get('meerkeuzeOpties').disable();
        }
        veldDefinitieFormGroup.get('meerkeuzeOpties').updateValueAndValidity();
    }

    getVeldTypes(): string[] {
        return Object.keys(FormulierVeldType);
    }

    opslaan(): void {
        this.bezigMetOpslaan = true;
        const val = this.definitieFormGroup.value as FormulierDefinitie;
        console.log(val);
        if (val.id) {
            this.service.update(val).subscribe(data => {
                this.definitie = data;
                this.init();
                this.bezigMetOpslaan = false;
                this.utilService.openSnackbar('msg.formulierdefinitie.gewijzigd');
            });
        } else {
            this.service.create(val).subscribe(data => {
                this.utilService.openSnackbar('msg.formulierdefinitie.toegevoegd');
                this.router.navigate(['admin/formulierdefinities', data.id]);
            });
        }

    }

    annuleren() {
        this.router.navigate(['/admin/formulierdefinities']);
    }
}
