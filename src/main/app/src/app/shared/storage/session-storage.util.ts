/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class SessionStorageUtil {
  static getItem<ANY>(key: string, defaultValue?: ANY): ANY {
    let item = JSON.parse(sessionStorage.getItem(key));
    if (defaultValue && !item) {
      // Kopieren om referentie te breken
      item = JSON.parse(JSON.stringify(defaultValue));
      this.setItem(key, item);
    }
    return item;
  }

  static setItem<ANY>(key: string, value: ANY): ANY {
    sessionStorage.setItem(key, JSON.stringify(value));
    return value;
  }

  static removeItem(key: string): any {
    sessionStorage.removeItem(key);
  }

  static clearSessionStorage(): void {
    sessionStorage.clear();
  }
}
