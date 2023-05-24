/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {MatDrawer} from '@angular/material/sidenav';

@Component({
    selector: 'zac-zaakdata',
    templateUrl: './zaakdata.component.html',
    styleUrls: ['./zaakdata.component.less']
})
export class ZaakdataComponent implements OnInit {

    @Input() zaak: Zaak;
    @Input() sideNav: MatDrawer;
    @Input() readonly = false;
    bezigMetOpslaan = false;
    form: FormGroup;

    constructor(private formBuilder: FormBuilder, private zakenService: ZakenService) { }

    ngOnInit(): void {
        this.form = this.buildForm(this.zaak.zaakdata, this.formBuilder.group({}));
        if (this.readonly) {
            this.form.disable();
        }
    }

    buildForm(data: {}, formData: FormGroup): FormGroup {
        for (const [k, v] of Object.entries(data)) {
            formData.addControl(k, this.getControl(v));
        }
        return formData;
    }

    buildArray(values: []): FormArray {
        if (!values?.length) {
            return this.formBuilder.array([[]]);
        }
        return this.formBuilder.array(values.map(value => this.getControl(value)));
    }

    getControl(value: any): AbstractControl {
        if (this.isValue(value)) {
            return new FormControl(value);
        } else if (this.isFile(value)) {
            return new FormControl({value: value['originalName'], disabled: true});
        } else if (this.isArray(value)) {
            return this.buildArray(value);
        } else if (this.isObject(value)) {
            return this.buildForm(value, this.formBuilder.group({}));
        } else {
            return new FormControl(JSON.stringify(value)); // wat dit dan ook mag zijn
        }
    }

    isFile(data): boolean {
        return data?.hasOwnProperty('originalName');
    }

    isArray(data): boolean {
        return Array.isArray(data);
    }

    isObject(data): boolean {
        return typeof data === 'object' && !Array.isArray(data) && data !== null;
    }

    isValue(data): boolean {
        return !this.isObject(data) && !this.isArray(data);
    }

    opslaan(): void {
        this.zaak.zaakdata = this.mergeDeep(this.zaak.zaakdata, this.form.value);
        this.bezigMetOpslaan = true;
        this.zakenService.updateZaakdata(this.zaak).subscribe(() => {
            this.bezigMetOpslaan = false;
            this.sideNav.close();
        });
    }

    mergeDeep(object1: {}, object2: {}): {} {
        Object.keys(object2).forEach(key => {
            const val1 = object1[key];
            const val2 = object2[key];
            if (this.isArray(val1) && this.isArray(val2)) {
                object1[key] = val1.concat(...val2);
            } else if (this.isObject(val1) && this.isObject(val2)) {
                object1[key] = this.mergeDeep(val1, val2);
            } else {
                object1[key] = val2;
            }
        });
        return object1;
    }
}
