/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZaakVerkortState} from './zaak-verkort.reducer';
import * as AppState from '../../state/app.state';

//lazyloading syntax
export interface State extends AppState.State {
    zaakVerkort: ZaakVerkortState;
}
