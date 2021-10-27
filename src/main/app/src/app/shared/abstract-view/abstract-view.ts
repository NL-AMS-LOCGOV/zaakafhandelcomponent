/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy} from '@angular/core';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {isFixedMenu} from '../state/side-nav.reducer';
import {UtilService} from '../../core/service/util.service';
import {toggleFixedSideNav} from '../state/side-nav.actions';
import {Subscription} from 'rxjs';

@Component({template: ''})
export abstract class AbstractView implements AfterViewInit, OnDestroy {

    abstract sideNavContainer: MatSidenavContainer;
    private isFixedMenu: boolean;
    protected subscriptions$: Subscription[] = [];
    collapsed: boolean = true;

    constructor(protected store: Store<State>, protected utilService: UtilService) {
    }

    ngAfterViewInit(): void {
        this.subscriptions$.push(this.store.select(isFixedMenu).subscribe(isFixedMenu => {
            this.isFixedMenu = isFixedMenu;
            this.updateMargins();
        }));

        this.subscriptions$.push(this.utilService.isTablet$.subscribe(isTablet => {
            if (isTablet && !this.isFixedMenu) {
                this.store.dispatch(toggleFixedSideNav());
            }
        }));
    }

    collapseSideNav(collapsed: boolean): void {
        if (!this.isFixedMenu) {
            this.collapsed = collapsed;
        }
    }

    protected updateMargins(): void {
        setTimeout(() => this.sideNavContainer.updateContentMargins(), 250);
    }

    ngOnDestroy(): void {
        this.subscriptions$.forEach(subscription$ => subscription$.unsubscribe());
    }
}
