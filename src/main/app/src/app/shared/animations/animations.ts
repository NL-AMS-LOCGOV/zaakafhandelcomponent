/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";

export const rotate180 = trigger("rotated180", [
  state("closed", style({ transform: "rotate(0deg)" })),
  state("open", style({ transform: "rotate(180deg)" })),
  transition("auto => open", animate("300ms ease-out")),
  transition("open => closed", animate("300ms ease-out")),
]);

export const sideNavToggle = trigger("sideNavToggle", [
  state("closed", style({ width: "65px" })),
  state("open", style({ "min-width": "200px" })),
  transition("closed <=> open", animate("200ms ease-in")),
]);

export const detailExpand = trigger("detailExpand", [
  state("collapsed", style({ height: "0px", minHeight: "0" })),
  state("expanded", style({ height: "*" })),
  transition(
    "expanded <=> collapsed",
    animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"),
  ),
]);

export const loadingFadeIn = trigger("loadingFadeIn", [
  transition(":enter", [style({ opacity: 0 }), animate("400ms 150ms ease-in")]),
]);
