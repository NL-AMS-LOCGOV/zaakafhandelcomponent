/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormComponent} from '../../model/form-component';
import {SelectMedewerkerFormField} from './select-medewerker-form-field';
import {TranslateService} from '@ngx-translate/core';
import {IdentityService} from '../../../../identity/identity.service';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {Groep} from '../../../../identity/model/groep';
import {Medewerker} from '../../../../identity/model/medewerker';

@Component({
    templateUrl: './select-medewerker.component.html',
    styleUrls: ['./select-medewerker.component.less']
})
export class SelectMedewerkerComponent extends FormComponent implements OnInit {

    data: SelectMedewerkerFormField;
    formGroup: FormGroup;
    groepControl = new FormControl(null, [Validators.required]);
    behandelaarControl = new FormControl();
    groepen: Observable<Groep[]>;
    behandelaren: Observable<Medewerker[]>;
    inGroep: boolean = true;

    constructor(public translate: TranslateService, public identityService: IdentityService, private formBuilder: FormBuilder) {
        super();
    }

    ngOnInit(): void {
        this.groepen = this.identityService.listGroepen();
        this.groepControl.setValue(this.data.defaultGroep);
        this.behandelaarControl.setValue(this.data.defaultMedewerker);
        this.formGroup = this.formBuilder.group({
            groep: this.groepControl,
            behandelaar: this.behandelaarControl
        });
        this.data.formControl.setValue(this.formGroup.value);
        this.formGroup.valueChanges.subscribe(data => {
            this.data.formControl.setValue(data);
        });
        this.groepControl.valueChanges.subscribe(data => {
            this.behandelaarControl.setValue(null);
            this.getBehandelaren();
        });
        this.getBehandelaren();

    }

    inGroepChanged($event: MouseEvent) {
        $event.stopPropagation();
        this.inGroep = !this.inGroep;
        this.getBehandelaren();
    }

    private getBehandelaren() {
        if (this.inGroep && this.groepControl.value) {
            this.behandelaren = this.identityService.listMedewerkersInGroep(this.groepControl.value.id);
        } else {
            this.behandelaren = this.identityService.listMedewerkers();
        }
    }

    compareGroep(g1: Groep, g2: Groep) {
        return g1?.id === g2?.id;
    }

    compareMedewerker(m1: Medewerker, m2: Medewerker) {
        return m1?.gebruikersnaam === m2?.gebruikersnaam;
    }
}
