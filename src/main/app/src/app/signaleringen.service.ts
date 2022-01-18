import {Injectable} from '@angular/core';
import {SignaleringType} from './shared/signaleringen/signalering-type';
import {Observable} from 'rxjs';
import {ZaakOverzicht} from './zaken/model/zaak-overzicht';
import {catchError} from 'rxjs/operators';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from './fout-afhandeling/fout-afhandeling.service';

@Injectable({
    providedIn: 'root'
})
export class SignaleringenService {

    private basepath = '/rest/signaleringen';

    numberOfSignaleringenMedewerker$: Observable<number> = this.http.get<number>(`${this.basepath}/medewerker/amount`);

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) { }

    listZakenSignalering(signaleringType: SignaleringType): Observable<ZaakOverzicht[]> {
        return this.http.get<ZaakOverzicht[]>(`${this.basepath}/zaken/${signaleringType}`).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }

}
