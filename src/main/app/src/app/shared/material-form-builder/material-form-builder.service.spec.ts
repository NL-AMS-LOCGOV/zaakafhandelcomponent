/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { TestBed } from "@angular/core/testing";

import { MaterialFormBuilderService } from "./material-form-builder.service";

describe("MaterialFormBuilderService", () => {
  let service: MaterialFormBuilderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MaterialFormBuilderService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
