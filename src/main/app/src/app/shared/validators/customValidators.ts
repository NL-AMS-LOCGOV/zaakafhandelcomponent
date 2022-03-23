/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractControl, ValidatorFn} from '@angular/forms';

export class CustomValidators {

    static postcode = CustomValidators.postcodeVFn();
    static bsn = CustomValidators.bsnVFn(false);
    static kvk = CustomValidators.kvkVFn();
    static vestigingsnummer = CustomValidators.vestigingsnummerVFn();
    static bsnPrefixed = CustomValidators.bsnVFn(true);
    static bsnOrVesPrefixed = CustomValidators.bsnOfVestigingVFn(true);
    static email = CustomValidators.emailVFn(false);
    static emails = CustomValidators.emailVFn(true);

    private static postcodeRegex = /^[1-9][0-9]{3}(?!sa|sd|ss)[a-z]{2}$/i;

    private static ID = 'A-Za-z\\d';
    private static LCL = '[' + CustomValidators.ID + '!#$%&\'*+\\-/=?^_`{|}~]+';
    private static LBL = '[' + CustomValidators.ID + ']([' + CustomValidators.ID + '\\-]*[' + CustomValidators.ID + '])?';
    private static EMAIL = CustomValidators.LCL + '(\\.' + CustomValidators.LCL + ')*@' + CustomValidators.LBL + '(\\.' + CustomValidators.LBL + ')+';
    private static emailRegex = new RegExp('^' + CustomValidators.EMAIL + '$');
    private static emailsRegex = new RegExp('^(' + CustomValidators.EMAIL + ')(;//s*' + CustomValidators.EMAIL + ')*$');

    private static bsnVFn(allowPrefix = false): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            const bsn = this.getValue(control, allowPrefix);
            if (bsn === null) {
                return null;
            }
            if (!this.isValidBSN(bsn)) {
                return {bsn: true};
            }
        };
    }

    private static bsnOfVestigingVFn(allowPrefix = false): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            const value = this.getValue(control, allowPrefix);
            if (value === null) {
                return null;
            }
            if (!(this.isValidBSN(value) || this.isValidVestigingsnummer(value))) { return {bsnOrVestiging: true}; }
        };
    }

    private static kvkVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            const kvk = this.getValue(control, false);
            if (kvk === null) {
                return null;
            }
            return isNaN(Number(kvk)) || kvk.length !== 8 ? {kvk: true} : null;
        };
    }

    static vestigingsnummerVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            const nummer = this.getValue(control, false);
            if (nummer === null) {
                return null;
            }
            return isNaN(Number(nummer)) || nummer.length !== 12 ? {vestigingsnummer: true} : null;
        };
    }

    private static postcodeVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (!CustomValidators.postcodeRegex.test(val)) {
                return {postcode: true};
            }
        };
    }

    private static emailVFn(multi: boolean): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (multi ? !CustomValidators.emailsRegex.test(val) : !CustomValidators.emailRegex.test(val)) {
                return {email: true};
            }
        };
    }

    private static isValidBSN(bsn: string): boolean {
        if (isNaN(Number(bsn)) || bsn.length !== 9) {
            return false;
        }
        let checksum: number = 0;
        for (let i = 0; i < 8; i++) {
            checksum += (Number(bsn.charAt(i)) * (9 - i));
        }
        checksum -= Number(bsn.charAt(8));
        return checksum % 11 === 0;
    }

    private static isValidVestigingsnummer(nummer: string): boolean {
        return !(isNaN(Number(nummer)) || nummer.length !== 12);
    }

    private static getValue(control: AbstractControl, allowPrefix = false) {
        if (!control.value) {
            return null;
        }
        const val = control.value;
        if (allowPrefix) {
            const prefix = val.indexOf('|');
            return val.substring(0, prefix !== -1 ? prefix : val.length).trim();
        } else {
            return control.value;
        }
    }

}
