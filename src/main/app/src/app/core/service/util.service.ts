/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {BehaviorSubject, iif, Observable, of} from 'rxjs';
import {delay, map, shareReplay, switchMap} from 'rxjs/operators';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class UtilService {

    private headerTitle: BehaviorSubject<string> = new BehaviorSubject<string>('zaakafhandelcomponent');
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    public headerTitle$: Observable<string> = this.headerTitle.asObservable();
    public loading$: Observable<boolean> = this.loading.pipe(switchMap((loading) =>
        iif(() => loading,
            of(loading).pipe(delay(700)),
            of(loading)
        )
    ));

    public isHandset$: Observable<boolean> = this.breakpointObserver
                                                 .observe(Breakpoints.Handset)
                                                 .pipe(
                                                     map(({matches}) => matches),
                                                     shareReplay()
                                                 );

    public isTablet$: Observable<boolean> = this.breakpointObserver
                                                .observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
                                                .pipe(
                                                    map(({matches}) => matches),
                                                    shareReplay()
                                                );

    constructor(private breakpointObserver: BreakpointObserver, private translate: TranslateService) {
    }

    setHeaderTitle(headerTitle: string): void {
        this.headerTitle.next(headerTitle);
    }

    setLoading(loading: boolean): void {
        this.loading.next(loading);
    }

    getEnumAsSelectList(prefix: string, enumValue: any): { label: string, value: string }[] {
        let list: { label: string, value: string }[] = [];
        Object.keys(enumValue).forEach(value => {
            this.translate.get(`${prefix}.${value}`).subscribe(result => {
                list.push({label: result, value: value});
            });
        });

        return list;
    }
}
