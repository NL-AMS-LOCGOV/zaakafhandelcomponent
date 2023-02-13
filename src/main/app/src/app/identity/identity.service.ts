/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Group} from './model/group';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {User} from './model/user';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';
import {UserWithGroups} from './model/user-with-groups';

@Injectable({
    providedIn: 'root'
})
export class IdentityService {

    private basepath: string = '/rest/identity';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    listGroups(): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.basepath}/groups`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listUsersInGroup(groupId: string): Observable<User[]> {
        return this.http.get<User[]>(`${this.basepath}/groups/${groupId}/users`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.basepath}/users`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    readLoggedInUser(): Observable<UserWithGroups> {
        const loggedInUser = SessionStorageUtil.getItem('loggedInUser') as UserWithGroups;
        if (loggedInUser) {
            return of(loggedInUser);
        }
        return this.http.get<UserWithGroups>(`${this.basepath}/loggedInUser`).pipe(
            tap(user => {
                SessionStorageUtil.setItem('loggedInUser', user);
            }),
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
