/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, Input, LOCALE_ID, OnInit} from '@angular/core';
import * as moment from 'moment';
import {StaticTextComponent} from '../static-text/static-text.component';

@Component({
    selector: 'zac-datum-overschreden',
    templateUrl: './datum-overschreden.component.html',
    styleUrls: ['../static-text/static-text.component.less', './datum-overschreden.component.less']
})
export class DatumOverschredenComponent extends StaticTextComponent implements OnInit {

    @Input() ended: string;
    @Input() fatal: boolean;

    constructor() {
        super();
    }

    isOverschreden(value: string, ended: string): any {
        return DatumOverschredenComponent.isOverschreden(value, ended);
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
