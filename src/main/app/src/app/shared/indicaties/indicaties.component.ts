import { Component, Input } from "@angular/core";
import { Indicatie } from "../model/indicatie";

export enum IndicatiesLayout {
  ZOEKEN = "ZOEKEN",
  WERKLIJST = "WERKLIJST",
  VIEW = "VIEW",
}

@Component({ template: "" })
export abstract class IndicatiesComponent {
  Layout = IndicatiesLayout;
  @Input() layout: IndicatiesLayout;
  indicaties: Indicatie[] = [];
}
