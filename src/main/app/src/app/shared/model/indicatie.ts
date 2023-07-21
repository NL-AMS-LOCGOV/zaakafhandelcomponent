/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class Indicatie {
  naam: string;
  icon: string;
  outlined: boolean;
  primary: boolean;
  toelichting: string;

  constructor(naam: string, icon: string, toelichting: string) {
    this.naam = naam;
    this.icon = icon;
    this.toelichting = toelichting;
  }

  temporary(): Indicatie {
    this.primary = true;
    return this;
  }

  alternate(): Indicatie {
    this.outlined = true;
    return this;
  }
}
