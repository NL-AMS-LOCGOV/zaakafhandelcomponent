import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {catchError} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {MailObject} from './model/mailobject';

@Injectable({
    providedIn: 'root'
})
export class MailService {

    private basepath = '/rest/mail';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    sendMail(mailObject: MailObject): Observable<number> {
        return this.http.post<number>(`${this.basepath}/send`, mailObject).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
