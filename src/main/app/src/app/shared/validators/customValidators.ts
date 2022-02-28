/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractControl, ValidatorFn} from '@angular/forms';

export class CustomValidators {

    static postcode = CustomValidators.postcodeVFn();
    static bsn = CustomValidators.bsnVFn(false);
    static bsnPrefixed = CustomValidators.bsnVFn(true);

    private static postcodeRegex = /^[1-9][0-9]{3}(?!sa|sd|ss)[a-z]{2}$/i;

    static bsnVFn(allowPrefix = false): ValidatorFn {
        return (control: AbstractControl): { [key: string]: boolean } | null => {
            if (!control.value) {
                return null;
            }
            const val = control.value;
            let bsn;
            if (allowPrefix) {
                const prefix = val.indexOf('|');
                bsn = val.substring(0, prefix !== -1 ? prefix : val.length).trim();
            } else {
                bsn = control.value;
            }
            if (isNaN(bsn) || bsn.length !== 9) {
                return {bsn: true};
            }
            let checksum = 0;
            for (let i = 0; i < 8; i++) {
                checksum += (bsn.charAt(i) * (9 - i));
            }
            checksum -= bsn.charAt(8);
            if (checksum % 11 !== 0) {
                return {bsn: true};
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

}
