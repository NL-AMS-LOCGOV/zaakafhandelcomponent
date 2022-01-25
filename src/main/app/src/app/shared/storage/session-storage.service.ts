/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class SessionStorageService {

    constructor() {
    }

    getSessionStorage(key: string, defaultValue?: any): any {
        let item = JSON.parse(sessionStorage.getItem(key));
        if (defaultValue && !item) {
            // Kopieren om referentie te breken
            item = JSON.parse(JSON.stringify(defaultValue));
            this.setSessionStorage(key, item);
        }

        return item;
    }

    setSessionStorage(key: string, value: any): any {
        sessionStorage.setItem(key, JSON.stringify(value));
        return value;
    }

    clearSessionStorage(): void {
        sessionStorage.clear();
    }
}

