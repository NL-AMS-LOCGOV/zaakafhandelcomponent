import { TestBed } from "@angular/core/testing";

import { ZaakFormulierenService } from "./zaak-formulieren.service";

describe("ZaakFormulierenService", () => {
  let service: ZaakFormulierenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ZaakFormulierenService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
