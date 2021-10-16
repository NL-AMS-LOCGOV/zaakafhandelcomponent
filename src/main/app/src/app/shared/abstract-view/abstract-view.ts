/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component} from '@angular/core';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {isFixedMenu} from '../state/side-nav.reducer';
import {UtilService} from '../../core/service/util.service';
import {toggleFixedSideNav} from '../state/side-nav.actions';

@Component({template: ''})
export abstract class AbstractView implements AfterViewInit {

    abstract sideNavContainer: MatSidenavContainer;
    private isFixedMenu: boolean;
    collapsed: boolean = true;

    constructor(protected store: Store<State>, protected utilService: UtilService) {
    }

    ngAfterViewInit(): void {
        this.store.select(isFixedMenu).subscribe(isFixedMenu => {
            this.isFixedMenu = isFixedMenu;
            this.updateMargins();
        });

        this.utilService.isTablet$.subscribe(isTablet => {
            if (isTablet && !this.isFixedMenu) {
                this.store.dispatch(toggleFixedSideNav());
            }
        });
    }

    collapseSideNav(collapsed: boolean): void {
        if (!this.isFixedMenu) {
            this.collapsed = collapsed;
        }
    }

    protected updateMargins(): void {
        setTimeout(() => this.sideNavContainer.updateContentMargins(), 250);
    }

}
