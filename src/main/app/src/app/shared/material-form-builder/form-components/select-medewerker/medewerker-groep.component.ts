/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormComponent} from '../../model/form-component';
import {TranslateService} from '@ngx-translate/core';
import {IdentityService} from '../../../../identity/identity.service';
import {FormBuilder, FormControl, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {Groep} from '../../../../identity/model/groep';
import {Medewerker} from '../../../../identity/model/medewerker';
import {MedewerkerGroepFormField} from './medewerker-groep-form-field';
import {AutocompleteValidators} from '../autocomplete/autocomplete-validators';
import {map, startWith} from 'rxjs/operators';

@Component({
    templateUrl: './medewerker-groep.component.html',
    styleUrls: ['./medewerker-groep.component.less']
})
export class MedewerkerGroepComponent extends FormComponent implements OnInit {
    data: MedewerkerGroepFormField;
    formGroup: FormGroup;
    groepControl = new FormControl();
    medewerkerControl = new FormControl();
    groepen: Groep[];
    filteredGroepen: Observable<Groep[]>;
    medewerkers: Medewerker[];
    filteredMedewerkers: Observable<Medewerker[]>;
    inGroep: boolean = true;

    constructor(public translate: TranslateService, public identityService: IdentityService, private formBuilder: FormBuilder) {
        super();
    }

    ngOnInit(): void {
        this.initGroepen();
        this.medewerkerControl.setValue(this.data.defaultMedewerker);
        this.formGroup = this.formBuilder.group({
            groep: this.groepControl,
            medewerker: this.medewerkerControl
        });
        this.data.formControl.setValue(this.formGroup.value);
        this.formGroup.valueChanges.subscribe(data => {
            if (this.formGroup.valid) {
                this.data.formControl.setErrors(null);
                if (data.medewerker === '') {
                    data.medewerker = undefined;
                }
                this.data.formControl.setValue(data);
            } else {
                this.data.formControl.setErrors([]);
            }
        });

        this.groepControl.valueChanges.subscribe(() => {
            if (this.groepControl.valid) {
                this.medewerkerControl.setValue(null);
                this.getMedewerkers();
            }
        });
        this.getMedewerkers();

    }

    initGroepen(): void {
        this.identityService.listGroepen().subscribe(groepen => {
            this.groepen = groepen;
            const validators: ValidatorFn[] = [];
            validators.push(AutocompleteValidators.optionInList(groepen));
            if (!this.data.groepOptioneel) {
                validators.push(Validators.required);
            }
            this.groepControl.setValidators(validators);
            this.filteredGroepen = this.groepControl.valueChanges.pipe(
                startWith(''),
                map(groep => (groep ? this._filterGroepen(groep) : this.groepen.slice()))
            );
            this.groepControl.setValue(this.data.defaultGroep);
        });
    }

    inGroepChanged($event: MouseEvent) {
        $event.stopPropagation();
        this.inGroep = !this.inGroep;
        this.getMedewerkers();
    }

    private getMedewerkers() {
        this.medewerkers = [];
        let observable: Observable<Medewerker[]>;
        if (this.inGroep && this.groepControl.value) {
            observable = this.identityService.listMedewerkersInGroep(this.groepControl.value.id);
        } else {
            observable = this.identityService.listMedewerkers();
        }
        observable.subscribe(medewerkers => {
            this.medewerkers = medewerkers;
            this.medewerkerControl.setValidators(AutocompleteValidators.optionInList(medewerkers));
            this.filteredMedewerkers = this.medewerkerControl.valueChanges.pipe(
                startWith(''),
                map(medewerker => (medewerker ? this._filterMedewerkers(medewerker) : this.medewerkers.slice()))
            );
        });
    }

    displayFn(obj: Medewerker | Groep): string {
        return obj && obj.naam ? obj.naam : '';
    }

    private _filterGroepen(value: string | Groep): Groep[] {
        if (typeof value === 'object') {
            return [value];
        }
        const filterValue = value.toLowerCase();
        return this.groepen.filter(groep => groep.naam.toLowerCase().includes(filterValue));
    }

    private _filterMedewerkers(value: string | Medewerker): Medewerker[] {
        if (typeof value === 'object') {
            return [value];
        }
        const filterValue = value.toLowerCase();
        return this.medewerkers.filter(medewerker => medewerker.naam.toLowerCase().includes(filterValue));
    }

    groepBlur(): void {
        const val = this.groepControl.value;
        if (typeof val === 'string') {
            const result = this.groepen.find(groep => groep.naam === val);
            if (result) {
                this.groepControl.setValue(result);
            }
        }
    }

    medewerkerBlur(): void {
        const val = this.medewerkerControl.value;
        if (typeof val === 'string') {
            const result = this.medewerkers.find(medewerker => medewerker.naam === val);
            if (result) {
                this.medewerkerControl.setValue(result);
            }
        }
    }
}
