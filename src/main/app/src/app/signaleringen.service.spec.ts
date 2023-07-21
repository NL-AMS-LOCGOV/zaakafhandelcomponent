/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { TestBed } from "@angular/core/testing";

import { SignaleringenService } from "./signaleringen.service";

describe("SignaleringenService", () => {
  let service: SignaleringenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SignaleringenService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
