/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {EmptyPipe} from './empty.pipe';
import {DatumPipe} from './datum.pipe';
import {DatumOverschredenPipe} from './datumOverschreden.pipe';

@NgModule({
    declarations: [
        EmptyPipe,
        DatumPipe,
        DatumOverschredenPipe
    ],
    exports: [
        EmptyPipe,
        DatumPipe,
        DatumOverschredenPipe
    ]
})
export class PipesModule {
}
