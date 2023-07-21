import { AbstractProcessFormulier } from "./abstract-process-formulier";

export class ProcessFormulierenBuilder {
  protected readonly _formulier: AbstractProcessFormulier;

  form(): ProcessFormulierenBuilder {
    return this;
  }

  build(): AbstractProcessFormulier {
    return this._formulier;
  }
}
