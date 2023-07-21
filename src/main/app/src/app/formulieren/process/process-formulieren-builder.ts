import { PlanItem } from "../../plan-items/model/plan-item";
import { Zaak } from "../../zaken/model/zaak";
import { AbstractProcessFormulier } from "./abstract-process-formulier";

export class ProcessFormulierenBuilder {
  protected readonly _formulier: AbstractProcessFormulier;

  form(planItem: PlanItem, zaak: Zaak): ProcessFormulierenBuilder {
    return this;
  }

  build(): AbstractProcessFormulier {
    return this._formulier;
  }
}
