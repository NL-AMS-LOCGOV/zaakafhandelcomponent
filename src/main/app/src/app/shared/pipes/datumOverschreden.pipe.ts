/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Inject, LOCALE_ID, Pipe} from '@angular/core';
import * as moment from 'moment';
import {DatumPipe} from './datum.pipe';

@Pipe({name: 'datumOverschreden'})
export class DatumOverschredenPipe extends DatumPipe {

    constructor(@Inject(LOCALE_ID) public locale: string) {
        super(locale);
    }

    transform(value: Date | moment.Moment | string, ended: Date | moment.Moment | string): any {
        var datum: string = super.transform(value);
        if (DatumOverschredenPipe.isOverschreden(value, ended)) {
            return datum + ' !';
        }
        return datum;
    }

    static isOverschreden(value: Date | moment.Moment | string, actual: Date | moment.Moment | string): boolean {
        if (value) {
            var limit: moment.Moment = moment(value, moment.ISO_8601);
            if (actual) {
                return limit.isBefore(moment(actual, moment.ISO_8601));
            } else {
                return limit.isBefore();
            }
        }
        return false;
    }

}
