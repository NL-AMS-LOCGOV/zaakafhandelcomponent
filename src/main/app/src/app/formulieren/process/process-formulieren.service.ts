import { Injectable } from "@angular/core";
import { ProcessFormulierenBuilder } from "./process-formulieren-builder";

@Injectable({
  providedIn: "root",
})
export class ProcessFormulierenService {
  constructor() {}

  public getFormulierBuilder(): ProcessFormulierenBuilder {
    return null;
  }
}
