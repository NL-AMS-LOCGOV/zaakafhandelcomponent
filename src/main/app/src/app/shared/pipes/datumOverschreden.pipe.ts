/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Inject, LOCALE_ID, Pipe, PipeTransform} from '@angular/core';
import * as moment from 'moment';
import {DatumPipe} from './datum.pipe';
import {DatumOverschredenComponent} from '../datum-overschreden/datum-overschreden.component';

@Pipe({name: 'datumOverschreden'})
export class DatumOverschredenPipe extends DatumPipe {

    constructor(@Inject(LOCALE_ID) public locale: string) {
        super(locale);
    }

    transform(value: Date | moment.Moment | string, ended: Date | moment.Moment | string): any {
        var datum: string = super.transform(value);
        if (DatumOverschredenComponent.isOverschreden(value, ended)) {
            return datum + ' !';
        }
        return datum;
    }
}
