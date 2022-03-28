/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */


export class SessionStorageUtil {

    static getSessionStorage(key: string, defaultValue?: any): any {
        let item = JSON.parse(sessionStorage.getItem(key));
        if (defaultValue && !item) {
            // Kopieren om referentie te breken
            item = JSON.parse(JSON.stringify(defaultValue));
            this.setSessionStorage(key, item);
        }

        return item;
    }

    static setSessionStorage(key: string, value: any): any {
        sessionStorage.setItem(key, JSON.stringify(value));
        return value;
    }

    static clearSessionStorage(): void {
        sessionStorage.clear();
    }
}

