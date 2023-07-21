/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ConfiguratieService } from "./configuratie.service";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";

describe("InformatieObjectService", () => {
  let service: ConfiguratieService;
  let mockHttpClient;
  let mockFoutAfhandelingService;
  let mockRouter;
  let mockSnackbar;
  let mockTranslate;

  beforeEach(() => {
    mockHttpClient = jasmine.createSpyObj(["get", "put"]);
    mockRouter = jasmine.createSpyObj(["navigate"]);
    mockSnackbar = jasmine.createSpyObj(["open"]);
    mockFoutAfhandelingService = new FoutAfhandelingService(
      mockRouter,
      mockSnackbar,
      mockTranslate,
    );

    service = new ConfiguratieService(
      mockHttpClient,
      mockFoutAfhandelingService,
    );
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
