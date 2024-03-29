/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {FormulierVeldtype} from '../../admin/model/formulieren/formulier-veld-type.enum';
import {SelectionModel} from '@angular/cdk/collections';
import {IdentityService} from '../../identity/identity.service';
import {User} from '../../identity/model/user';
import {Group} from '../../identity/model/group';
import {Zaak} from '../../zaken/model/zaak';
import * as moment from 'moment/moment';
import {FormulierVeldDefinitie} from '../../admin/model/formulieren/formulier-veld-definitie';

@Component({
    selector: 'zac-formulier',
    templateUrl: './formulier.component.html',
    styleUrls: ['./formulier.component.less']
})
export class FormulierComponent implements OnInit {

    @Input() definitie: FormulierDefinitie;
    @Input() readonly: boolean;
    @Input() submitButtonLabel: 'actie.starten' | 'actie.opslaan.afronden' | 'actie.opslaan' = 'actie.starten';
    @Input() toonAnnulerenButton = true;
    @Input() zaak: Zaak;
    @Output() submit = new EventEmitter<{}>();

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
        if (this.readonly) {
            this.formGroup.disable();
        }
    }

    createForm() {
        this.formGroup = this.formBuilder.group({});
        this.definitie.veldDefinities.forEach(vd => {

            if (vd.veldtype === FormulierVeldtype.CHECKBOXES) {
                this.checked.set(vd.systeemnaam, new SelectionModel<string>(true));
            }

            if (FormulierVeldDefinitie.isOpschorten(vd)) {
                const control = FormulierVeldDefinitie.asControl(vd);
                if (!this.isOpgeschortenMogelijk()) {
                    control.setValue(false);
                    control.disable();
                }
                this.formGroup.addControl(vd.systeemnaam, control);
            } else if (FormulierVeldDefinitie.isHervatten(vd)) {
                const control = FormulierVeldDefinitie.asControl(vd);
                if (!this.isHervatenMogelijk()) {
                    control.setValue(false);
                    control.disable();
                }
                this.formGroup.addControl(vd.systeemnaam, control);
            } else {
                this.formGroup.addControl(vd.systeemnaam, FormulierVeldDefinitie.asControl(vd));
            }
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


    hasValue(systeemnaam: string) {
        return this.formGroup.get(systeemnaam).value !== null && this.formGroup.get(systeemnaam).value !== '';
    }

    opslaan() {
        this.bezigMetOpslaan = true;
        this.submit.emit(this.formGroup.value);
        this.formGroup.disable();
    }

    isOpgeschortenMogelijk() {
        return !this.zaak.isOpgeschort && this.zaak.isOpen && !this.zaak.isHeropend;
    }

    isHervatenMogelijk() {
        return this.zaak.isOpgeschort;
    }

    cancel() {
        this.bezigMetOpslaan = true;
        this.submit.emit(null);
    }

    toonVeld(veldDefinitie: FormulierVeldDefinitie): boolean {
        if (FormulierVeldDefinitie.isOpschorten(veldDefinitie)) {
            return this.isOpgeschortenMogelijk();
        }
        if (FormulierVeldDefinitie.isHervatten(veldDefinitie)) {
            return this.isHervatenMogelijk();
        }
        return true;
    }
}
