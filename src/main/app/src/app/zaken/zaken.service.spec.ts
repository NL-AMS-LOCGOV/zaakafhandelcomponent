/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ZakenService } from "./zaken.service";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";

describe("ZaakService", () => {
  let service: ZakenService;
  let mockHttpClient;
  let mockFoutAfhandelingService;
  let mockRouter;
  let mockSnackbar;
  let mockTranslate;

  beforeEach(() => {
    mockHttpClient = jasmine.createSpyObj(["get", "post", "patch"]);
    mockRouter = jasmine.createSpyObj(["navigate"]);
    mockSnackbar = jasmine.createSpyObj(["open"]);
    mockFoutAfhandelingService = new FoutAfhandelingService(
      mockRouter,
      mockSnackbar,
      mockTranslate,
    );

    service = new ZakenService(mockHttpClient, mockFoutAfhandelingService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
