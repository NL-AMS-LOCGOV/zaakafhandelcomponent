/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Zaak } from "../../zaken/model/zaak";
import { AbstractZaakFormulier } from "./abstract-zaak-formulier";

export class ZaakFormulierBuilder {
  protected readonly _formulier: AbstractZaakFormulier;

  constructor(formulier: AbstractZaakFormulier) {
    this._formulier = formulier;
  }

  form(zaak: Zaak): ZaakFormulierBuilder {
    this._formulier.dataElementen = zaak.zaakdata;
    this._formulier.zaak = zaak;
    this._formulier.initForm();
    return this;
  }

  build(): AbstractZaakFormulier {
    return this._formulier;
  }
}
