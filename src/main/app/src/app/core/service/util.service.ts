/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Inject, Injectable, Optional} from '@angular/core';
import {BehaviorSubject, forkJoin, iif, Observable, of} from 'rxjs';
import {delay, map, shareReplay, switchMap} from 'rxjs/operators';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DOCUMENT} from '@angular/common';

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

    constructor(@Optional() @Inject(DOCUMENT) private document: any,
                private breakpointObserver: BreakpointObserver,
                private translate: TranslateService,
                private titleService: Title,
                private snackbar: MatSnackBar) {
    }

    /**
     * Check whether or not there is an active edit overlay on the screen eg. autocomplete or datepicker
     */
    hasEditOverlay(): boolean {
        const overlayElements: any[] = this.getOverlayElements('mat-autocomplete-panel', 'mat-datepicker-popup');
        return overlayElements.length > 0;
    }

    private getOverlayElements(...classList): any[] {
        const overlayElements: any[] = [];
        for (const styleClass of classList) {
            overlayElements.push(...this.document.getElementsByClassName(styleClass));
        }
        return overlayElements;
    }

    readGemeenteNaam(): string {
        // TODO Configuratie uitlezen
        return 'zaakafhandelcomponent';
    }

    setTitle(title: string, params?: {}): void {
        forkJoin({
            prefix: this.translate.get('title.prefix', {gemeente: this.readGemeenteNaam()}),
            title: this.translate.get(title, params)
        }).subscribe(result => {
            this.titleService.setTitle(result.prefix + result.title);
            this.headerTitle.next(result.title);
        });
    }

    setLoading(loading: boolean): void {
        this.loading.next(loading);
    }

    getEnumAsSelectList(prefix: string, enumValue: any): Observable<{ label: string, value: string }[]> {
        const list: { label: string, value: string }[] = [];
        Object.keys(enumValue).forEach(value => {
            this.translate.get(`${prefix}.${value}`).subscribe(result => {
                list.push({label: result, value: value});
            });
        });

        return of(list);
    }

    openSnackbar(message: string, params?: {}) {
        forkJoin({
            message: this.translate.get(message, params),
            action: this.translate.get('actie.sluiten')
        }).subscribe(result => {
            this.snackbar.open(result.message, result.action, {duration: 3000});
        });
    }
}
