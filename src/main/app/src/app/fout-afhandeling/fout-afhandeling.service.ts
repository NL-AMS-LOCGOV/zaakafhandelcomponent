/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable, isDevMode} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TranslateService} from '@ngx-translate/core';
import {MatDialog} from '@angular/material/dialog';
import {FoutDialogComponent} from './dialog/fout-dialog.component';

@Injectable({
    providedIn: 'root'
})
export class FoutAfhandelingService {

    foutmelding: string;
    bericht: string;
    stack: string;
    exception: string;

    constructor(private router: Router, private snackbar: MatSnackBar,
                private translate: TranslateService, private dialog: MatDialog) {
    }

    public foutAfhandelen(err: HttpErrorResponse): Observable<any> {
        if (err.status === 400) {
            return this.openFoutDialog(err);
        } else {
            return this.redirect(err);
        }
    }

    private openFoutDialog(err: HttpErrorResponse): Observable<any> {
        this.dialog.open(FoutDialogComponent, {
            data: err.error,
        });

        return throwError(() => 'Fout!' );
    }

    private redirect(err: HttpErrorResponse): Observable<never> {
        this.foutmelding = err.message;
        if (err.error instanceof ErrorEvent) {
            // Client-side
            this.foutmelding = `Er is een fout opgetreden`;
            this.bericht = err.error.message;
            this.exception = '';
            this.stack = '';
        } else if (err.status === 0 && err.url.startsWith('/rest/')) { // status 0, niet meer ingelogd
            if (!isDevMode()) {
                window.location.reload();
                return;
            }
            this.foutmelding = 'Helaas! Je bent uitgelogd.';
            this.bericht = '';
        } else {
            this.foutmelding = `De server heeft code ${err.status} geretourneerd`;
            if (err.error) {
                this.stack = err.error.stackTrace;
                this.bericht = err.error.message;
                this.exception = err.error.exception;
            } else {
                this.exception = '';
                this.stack = '';
                this.bericht = err.message;
            }
        }
        this.router.navigate(['/fout-pagina']);
        return throwError(() => `${this.foutmelding}: ${this.bericht}`);
    }

    public log(melding): (error: HttpErrorResponse) => Observable<any> {
        return (error: any): Observable<never> => {
            console.error(error); // log to console instead
            this.translate.get('actie.sluiten').subscribe(action => {
                this.snackbar.open(melding, action);
            });
            return throwError(error);
        };
    }

    public getFout(e: HttpErrorResponse) {
        if (e.error instanceof ErrorEvent) {
            return `Er is een fout opgetreden. (${e.error.message})`;
        } else {
            if (e.error) {
                return `De server heeft code ${e.status} geretourneerd. (${e.error.exception})`;
            } else {
                return e.message;
            }
        }
    }
}
