/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TranslateService} from '@ngx-translate/core';

export interface ListViewContent {
    list?: string,
    view?: string
}

export class Indicatie {

    private readonly INDICATIE_PREFIX: string = 'indicatie';

    type: string;
    visible: boolean;
    primary?: boolean;
    tooltipOverride?: ListViewContent;
    tooltipSuffix?: ListViewContent;
    contentOverride?: ListViewContent;

    constructor(private translateService: TranslateService) {}

    public tooltip(): ListViewContent {
        const result = {
            list: this.translateService.instant(`${this.INDICATIE_PREFIX}.${this.type}`),
            view: this.translateService.instant(`${this.INDICATIE_PREFIX}.${this.type}`)
        };
        if (this.tooltipOverride?.list) {
            result.list = this.tooltipOverride.list;
        }
        if (this.tooltipOverride?.view) {
            result.view = this.tooltipOverride.view;
        }
        if (this.tooltipSuffix?.list) {
            result.list = `${result.list}: ${this.tooltipSuffix.list}`;
        }
        if (this.tooltipSuffix?.view) {
            result.view = `${result.view}: ${this.tooltipSuffix.view}`;
        }
        return result;
    }

    public content(): ListViewContent {
        return {
            list: this.type.toString().charAt(0),
            view: this.translateService.instant(`${this.INDICATIE_PREFIX}.${this.type}`)
        };
    }
}

export class IndicatieBuiler {

    readonly indicatie: Indicatie;

    constructor(private translateService: TranslateService) {
        this.indicatie = new Indicatie(this.translateService);
    }

    type(type: string): this {
        this.indicatie.type = type;
        return this;
    }

    visible(visible: boolean): this {
        this.indicatie.visible = visible;
        return this;
    }

    primary(primary: boolean): this {
        this.indicatie.primary = primary;
        return this;
    }

    tooltipOverride(tooltipOverride: ListViewContent): this {
        this.indicatie.tooltipOverride = tooltipOverride;
        return this;
    }

    tooltipSuffix(tooltipSuffix: ListViewContent): this {
        this.indicatie.tooltipSuffix = tooltipSuffix;
        return this;
    }

    contentOverride(contentOverride: ListViewContent): this {
        this.indicatie.contentOverride = contentOverride;
        return this;
    }

    build(): Indicatie {
        return this.indicatie;
    }
}
