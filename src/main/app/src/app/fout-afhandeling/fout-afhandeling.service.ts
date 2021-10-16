/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
    providedIn: 'root'
})
export class FoutAfhandelingService {

    foutmelding: string;
    bericht: string;
    stack: string;
    exception: string;

    constructor(private router: Router, private snackbar: MatSnackBar) {
    }

    public redirect(err: HttpErrorResponse): Observable<never> {
        console.log(err);
        this.foutmelding = err.message;
        if (err.error instanceof ErrorEvent) {
            //Client-side / network error .
            this.foutmelding = `Er is een fout opgetreden`;
            this.bericht = err.error.message;
            this.exception = '';
            this.stack = '';
        } else {
            // Server-side error.
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
            this.snackbar.open(melding, 'Sluiten');
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
