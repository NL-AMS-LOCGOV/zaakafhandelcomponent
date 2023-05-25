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
        this.mergeDeep(this.zaak.zaakdata, this.form.value);
        this.bezigMetOpslaan = true;
        this.zakenService.updateZaakdata(this.zaak).subscribe(() => {
            this.bezigMetOpslaan = false;
            this.sideNav.close();
        });
    }

    mergeDeep(dest: {}, src: {}): void {
        Object.keys(src).forEach(key => {
            if (src.hasOwnProperty(key)) {
                const destVal = dest[key];
                const srcVal = src[key];
                if (this.isArray(destVal) && this.isArray(srcVal)) {
                    dest[key] = destVal.concat(...srcVal);
                } else if (dest.hasOwnProperty(key) && this.isObject(destVal) && this.isObject(srcVal)) {
                    this.mergeDeep(destVal, srcVal);
                } else {
                    dest[key] = srcVal;
                }
            }
        });
    }
}
