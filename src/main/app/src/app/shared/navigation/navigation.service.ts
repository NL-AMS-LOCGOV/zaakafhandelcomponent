/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterEvent} from '@angular/router';
import {Location} from '@angular/common';
import {SessionStorageService} from '../storage/session-storage.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {UtilService} from '../../core/service/util.service';
import {filter} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class NavigationService {

    private backDisabled: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    public backDisabled$: Observable<boolean> = this.backDisabled.asObservable();

    private static NAVIGATION_HISTORY: string = 'navigationHistory';

    constructor(private router: Router, private location: Location, private storage: SessionStorageService, private utilService: UtilService) {
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
                let history = this.storage.getSessionStorage(NavigationService.NAVIGATION_HISTORY, []);
                if (history.length == 0 || history[history.length - 1] !== e.urlAfterRedirects) {
                    history.push(e.urlAfterRedirects);
                }
                this.storage.setSessionStorage(NavigationService.NAVIGATION_HISTORY, history);
                this.backDisabled.next(history.length <= 1);
            }

            this.utilService.setLoading(false);
        }
    }

    back(): void {
        let history = this.storage.getSessionStorage(NavigationService.NAVIGATION_HISTORY, []);
        history.pop();
        if (history.length > 0) {
            this.location.back();
        } else {
            this.router.navigate(['..']);
        }
        this.storage.setSessionStorage(NavigationService.NAVIGATION_HISTORY, history);
        this.backDisabled.next(history.length <= 1);
    }
}
