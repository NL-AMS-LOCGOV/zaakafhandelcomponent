/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {SharedState} from './shared.state';
import * as SideNavActions from './side-nav.actions';

export interface SideNavState {
    fixedMenu: boolean;
}

const initialState: SideNavState = {
    fixedMenu: false
};

const getSharedState = createFeatureSelector<SharedState>('shared');
const getSideNavState = createSelector(getSharedState, state => state.sideNav);
export const isFixedMenu = createSelector(getSideNavState, state => state.fixedMenu);

export const sideNavReducer = createReducer<SideNavState>(
    initialState,
    on(SideNavActions.toggleFixedSideNav, (state): SideNavState => {
        return {
            ...state,
            fixedMenu: !state.fixedMenu
        };
    })
);

