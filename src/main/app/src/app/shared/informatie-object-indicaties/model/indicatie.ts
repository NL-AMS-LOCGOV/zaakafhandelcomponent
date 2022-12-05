/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TranslateService} from '@ngx-translate/core';

export interface ListViewContent {
    list?: string,
    view?: string
}

export interface IndicatieDefinitie {
    type: string;
    primary?: boolean;
    tooltipOverride?: ListViewContent;
    tooltipSuffix?: ListViewContent;
    contentOverride?: ListViewContent;
}

export class Indicatie {

    private readonly INDICATIE_PREFIX: string = 'indicatie';

    constructor(private translateService: TranslateService, private definitie: IndicatieDefinitie) {}

    public get primary(): boolean {
        return this.definitie.primary;
    }

    public get tooltip(): ListViewContent {
        const result = {
            list: this.translateService.instant(`${this.INDICATIE_PREFIX}.${this.definitie.type}`),
            view: this.translateService.instant(`${this.INDICATIE_PREFIX}.${this.definitie.type}`)
        };
        if (this.definitie.tooltipOverride?.list) {
            result.list = this.definitie.tooltipOverride.list;
        }
        if (this.definitie.tooltipOverride?.view) {
            result.view = this.definitie.tooltipOverride.view;
        }
        if (this.definitie.tooltipSuffix?.list) {
            result.list = `${result.list}: ${this.definitie.tooltipSuffix.list}`;
        }
        if (this.definitie.tooltipSuffix?.view) {
            result.view = `${result.view}: ${this.definitie.tooltipSuffix.view}`;
        }
        return result;
    }

    public get content(): ListViewContent {
        const result = {
            list: this.definitie.type.toString().charAt(0),
            view: this.translateService.instant(`${this.INDICATIE_PREFIX}.${this.definitie.type}`)
        };
        if (this.definitie.contentOverride?.list) {
            result.list = this.definitie.contentOverride.list;
        }
        if (this.definitie.contentOverride?.view) {
            result.view = this.definitie.contentOverride.view;
        }
        return result;
    }
}
