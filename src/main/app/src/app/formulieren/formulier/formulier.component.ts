/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {FormulierVeldtype} from '../../admin/model/formulieren/formulier-veld-type.enum';
import {SelectionModel} from '@angular/cdk/collections';
import {IdentityService} from '../../identity/identity.service';
import {User} from '../../identity/model/user';
import {Group} from '../../identity/model/group';
import {Zaak} from '../../zaken/model/zaak';
import * as moment from 'moment/moment';

@Component({
    selector: 'zac-formulier',
    templateUrl: './formulier.component.html',
    styleUrls: ['./formulier.component.less']
})
export class FormulierComponent implements OnInit {

    @Input() definitie: FormulierDefinitie;
    @Input() zaak: Zaak;
    @Output() data = new EventEmitter<{}>();

    formGroup: FormGroup;
    FormulierVeldtype = FormulierVeldtype;

    checked: Map<string, SelectionModel<string>> = new Map<string, SelectionModel<string>>();
    referentietabellen: Map<string, string[]> = new Map<string, string[]>();

    medewerkers: User[];
    groepen: Group[];

    bezigMetOpslaan = false;

    constructor(public formBuilder: FormBuilder, public identityService: IdentityService) {
    }

    ngOnInit(): void {
        this.identityService.listUsers().subscribe(u => {
            this.medewerkers = u;
        });
        this.identityService.listGroups().subscribe(g => {
            this.groepen = g;
        });
        this.createForm();
    }

    createForm() {
        this.formGroup = this.formBuilder.group({});
        this.definitie.veldDefinities.forEach(vd => {
            let control: FormControl;
            control = new FormControl(vd.defaultWaarde, vd.verplicht ? Validators.required : null);
            switch (vd.veldtype) {
                case FormulierVeldtype.NUMMER:
                    control = new FormControl<string>(vd.defaultWaarde,
                            vd.verplicht ?
                                    [Validators.required, Validators.min(0), Validators.max(2147483647)] :
                                    [Validators.min(0), Validators.max(2147483647)]);
                    break;
                case FormulierVeldtype.EMAIL:
                    control = new FormControl<string>(vd.defaultWaarde,
                            vd.verplicht ? [Validators.required, Validators.email] : Validators.email);
                    break;
                case FormulierVeldtype.CHECKBOXES:
                    this.checked.set(vd.systeemnaam, new SelectionModel<string>(true));
                    break;
                case FormulierVeldtype.DATUM:
                    control.setValue(this.toDate(vd.defaultWaarde));
                    break;
                default:
                    break;
            }
            this.formGroup.addControl(vd.systeemnaam, control);
        });
    }


    toggleCheckboxes(systeemnaam: string, optie: string) {
        this.checked.get(systeemnaam).toggle(optie);
        this.formGroup.get(systeemnaam).setValue(this.checked.get(systeemnaam).selected.join(';'));
    }

    getControl(systeemnaam: string): FormControl<string> {
        return this.formGroup.get(systeemnaam) as FormControl<string>;
    }

    days(systeemnaam) {
        const datum = this.formGroup.get(systeemnaam).value;
        if (datum) {
            return moment(datum).diff(moment().startOf('day'), 'days');
        }
    }

    toDate(dateStr): Date {
        if (dateStr) {
            const [day, month, year] = dateStr.split('-');
            return new Date(year, month - 1, day);
        }
        return new Date();
    }

    hasValue(systeemnaam: string) {
        return this.formGroup.get(systeemnaam).value !== null && this.formGroup.get(systeemnaam).value !== '';
    }

    opslaan() {
        this.bezigMetOpslaan = true;
        this.data.emit(this.formGroup.value);
    }

    cancel() {
        this.bezigMetOpslaan = true;
        this.data.emit(null);
    }
}
