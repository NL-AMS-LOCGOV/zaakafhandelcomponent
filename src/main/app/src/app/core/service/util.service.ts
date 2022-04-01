/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Inject, Injectable, Optional} from '@angular/core';
import {BehaviorSubject, iif, Observable, of, Subject} from 'rxjs';
import {delay, map, shareReplay, switchMap} from 'rxjs/operators';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DOCUMENT} from '@angular/common';
import {Router} from '@angular/router';
import {ActionBarAction} from '../actionbar/model/action-bar-action';
import {ActionIcon} from '../../shared/edit/action-icon';

@Injectable({
    providedIn: 'root'
})
export class UtilService {

    private headerTitle: BehaviorSubject<string> = new BehaviorSubject<string>('zaakafhandelcomponent');
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    public headerTitle$: Observable<string> = this.headerTitle.asObservable();
    public addAction$ = new Subject<ActionBarAction>();

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
                private router: Router,
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
        const _prefix = this.translate.instant('title.prefix', {gemeente: this.readGemeenteNaam()});
        const _title = this.translate.instant(title, params);
        this.titleService.setTitle(_prefix + _title);
        this.headerTitle.next(_title);
    }

    setLoading(loading: boolean): void {
        this.loading.next(loading);
    }

    getEnumAsSelectList(prefix: string, enumValue: any): { label: string, value: string }[] {
        const list: { label: string, value: string }[] = [];
        Object.keys(enumValue).forEach(value => {
            this.translate.get(`${prefix}.${enumValue[value]}`).subscribe(result => {
                list.push({label: result, value: value});
            });
        });
        return list;
    }

    openSnackbar(message: string, params?: {}) {
        this.snackbar.open(this.translate.instant(message, params), this.translate.instant('actie.sluiten'), {duration: 3000});
    }

    addAction(text: string, subText: string, action: ActionIcon, dismissClicked: Subject<any>, actionEnabled?: () => boolean) {
        this.addAction$.next(new ActionBarAction(text, subText, action, dismissClicked, actionEnabled));
    }

    reloadRoute(): void {
        const currentUrl = this.router.url;
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
            this.router.navigate([currentUrl]);
        });
    }
}
