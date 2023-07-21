/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { TestBed } from "@angular/core/testing";

import { TaakFormulierenService } from "./taak-formulieren.service";

describe("TaakFormulierenService", () => {
  let service: TaakFormulierenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaakFormulierenService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
