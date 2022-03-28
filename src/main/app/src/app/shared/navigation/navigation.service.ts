/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterEvent} from '@angular/router';
import {Location} from '@angular/common';
import {BehaviorSubject, Observable} from 'rxjs';
import {UtilService} from '../../core/service/util.service';
import {filter} from 'rxjs/operators';
import {SessionStorageUtil} from '../storage/session-storage.util';

@Injectable({
    providedIn: 'root'
})
export class NavigationService {

    private backDisabled: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    public backDisabled$: Observable<boolean> = this.backDisabled.asObservable();

    private static NAVIGATION_HISTORY: string = 'navigationHistory';

    constructor(private router: Router, private location: Location, private utilService: UtilService) {
        router.events.pipe(
            filter(
                (e: RouterEvent): e is RouterEvent => e instanceof NavigationStart || e instanceof NavigationEnd || e instanceof NavigationCancel || e instanceof NavigationError)
        ).subscribe((e: RouterEvent) => this.handleRouterEvents(e));
    }

    private handleRouterEvents(e: RouterEvent): void {
        if (e instanceof NavigationStart) {
            this.utilService.setLoading(true);
        } else {
            if (e instanceof NavigationEnd) {
                const history = SessionStorageUtil.getSessionStorage(NavigationService.NAVIGATION_HISTORY, []);
                if (history.length === 0 || history[history.length - 1] !== e.urlAfterRedirects) {
                    history.push(e.urlAfterRedirects);
                }
                SessionStorageUtil.setSessionStorage(NavigationService.NAVIGATION_HISTORY, history);
                this.backDisabled.next(history.length <= 1);
            }

            this.utilService.setLoading(false);
        }
    }

    back(): void {
        const history = SessionStorageUtil.getSessionStorage(NavigationService.NAVIGATION_HISTORY, []);
        history.pop();
        if (history.length > 0) {
            this.location.back();
        } else {
            this.router.navigate(['..']);
        }
        SessionStorageUtil.setSessionStorage(NavigationService.NAVIGATION_HISTORY, history);
        this.backDisabled.next(history.length <= 1);
    }
}
