/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { PlanItemsService } from "./plan-items.service";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";

describe("PlanItemServiceService", () => {
  let service: PlanItemsService;
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

    service = new PlanItemsService(mockHttpClient, mockFoutAfhandelingService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
