/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractControl, ValidatorFn} from '@angular/forms';

export class CustomValidators {

    static postcode = CustomValidators.postcodeVFn();
    static bsn = CustomValidators.bsnVFn();
    static kvk = CustomValidators.kvkVFn();
    static vestigingsnummer = CustomValidators.vestigingsnummerVFn();
    static rsin = CustomValidators.rsinVFn();
    static email = CustomValidators.emailVFn(false);
    static emails = CustomValidators.emailVFn(true);
    static handelsnaam = CustomValidators.handelsnaamVFn();

    private static postcodeRegex = /^[1-9][0-9]{3}(?!sa|sd|ss)[a-z]{2}$/i;

    private static ID = 'A-Za-z\\d';
    private static LCL = '[' + CustomValidators.ID + '!#$%&\'*+\\-/=?^_`{|}~]+';
    private static LBL = '[' + CustomValidators.ID + ']([' + CustomValidators.ID + '\\-]*[' + CustomValidators.ID + '])?';
    private static EMAIL = CustomValidators.LCL + '(\\.' + CustomValidators.LCL + ')*@' + CustomValidators.LBL + '(\\.' + CustomValidators.LBL + ')+';
    private static emailRegex = new RegExp('^' + CustomValidators.EMAIL + '$');
    private static emailsRegex = new RegExp('^(' + CustomValidators.EMAIL + ')(;//s*' + CustomValidators.EMAIL + ')*$');
    private static handelsnaamRegex = new RegExp('[*()]+');

    private static bsnVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (!this.isValidBSN(val)) {
                return {bsn: true};
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

    private static kvkVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (isNaN(Number(val)) || val.length !== 8) {
                return {kvk: true};
            }
        };
    }

    static vestigingsnummerVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (isNaN(Number(val)) || val.length !== 12) {
                return {vestigingsnummer: true};
            }
        };
    }

    static rsinVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (isNaN(Number(val)) || val.length !== 9) {
                return {rsin: true};
            }
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
            if (multi
                ? !CustomValidators.emailsRegex.test(val)
                : !CustomValidators.emailRegex.test(val)) {
                return {email: true};
            }
        };
    }

    private static handelsnaamVFn(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            if (CustomValidators.handelsnaamRegex.test(val)) {
                return {handelsnaam: true};
            }
        };
    }
}
