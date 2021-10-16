/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import * as ZaakVerkortActions from './zaak-verkort.actions';
import {State} from './zaken.state';

export interface ZaakVerkortState {
    collapsed: boolean,
    loaded: boolean
}

const initialState: ZaakVerkortState = {
    collapsed: false,
    loaded: false
};

const getZakenState = createFeatureSelector<State>('zaken');
const getZaakVerkortState = createSelector(getZakenState, state => state.zaakVerkort);
export const isZaakVerkortCollapsed = createSelector(getZaakVerkortState, state => state.collapsed);

export const zaakVerkortReducer = createReducer<ZaakVerkortState>(
    initialState,
    on(ZaakVerkortActions.toggleCollapseZaakVerkort, (state): ZaakVerkortState => {
        return {
            ...state,
            collapsed: !state.collapsed
        };
    })
);
