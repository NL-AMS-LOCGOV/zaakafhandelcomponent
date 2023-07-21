/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { TakenService } from "./taken.service";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";

describe("TaakService", () => {
  let service: TakenService;
  let mockHttpClient;
  let mockFoutAfhandelingService;
  let mockRouter;
  let mockSnackbar;
  let mockTranslate;

  beforeEach(() => {
    mockHttpClient = jasmine.createSpyObj(["get", "patch"]);
    mockRouter = jasmine.createSpyObj(["navigate"]);
    mockSnackbar = jasmine.createSpyObj(["open"]);
    mockFoutAfhandelingService = new FoutAfhandelingService(
      mockRouter,
      mockSnackbar,
      mockTranslate,
    );

    service = new TakenService(mockHttpClient, mockFoutAfhandelingService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
