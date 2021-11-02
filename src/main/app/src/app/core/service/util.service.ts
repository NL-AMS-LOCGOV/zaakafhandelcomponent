/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {BehaviorSubject, forkJoin, iif, Observable, of} from 'rxjs';
import {delay, map, shareReplay, switchMap} from 'rxjs/operators';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {MatSnackBar} from '@angular/material/snack-bar';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';

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

    constructor(private breakpointObserver: BreakpointObserver, private translate: TranslateService, private titleService: Title, private snackbar: MatSnackBar) {
    }

    getGemeenteNaam(): string {
        // TODO Configuratie uitlezen
        return 'zaakafhandelcomponent';
    }

    setTitle(title: string, params?: Object): void {
        forkJoin({
            prefix: this.translate.get('title.prefix', {gemeente: this.getGemeenteNaam()}),
            title: this.translate.get(title, params)
        }).subscribe(result => {
            this.titleService.setTitle(result.prefix + result.title);
            this.headerTitle.next(result.title);
        });
    }

    setLoading(loading: boolean): void {
        this.loading.next(loading);
    }

    getFormConfig(actie) {
        return new FormConfig(this.translate.get(actie), this.translate.get('actie.annuleren'));
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

    openSnackbar(message: string, params?: Object) {
        forkJoin({
            message: this.translate.get(message, params),
            action: this.translate.get('actie.sluiten')
        }).subscribe(result => {
            this.snackbar.open(result.message, result.action, {duration: 3000});
        });
    }
}
