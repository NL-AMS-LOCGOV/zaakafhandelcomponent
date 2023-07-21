/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
} from "@angular/core";
import { TextIcon } from "../edit/text-icon";
import { FormControl } from "@angular/forms";

@Component({
  selector: "zac-static-text",
  templateUrl: "./static-text.component.html",
  styleUrls: ["./static-text.component.less"],
})
export class StaticTextComponent implements OnInit, OnChanges {
  @Input() label: string;
  @Input() value: any;
  @Input() icon: TextIcon;
  @Input() maxLength: number;
  @Output() iconClicked = new EventEmitter<void>();

  showIcon: boolean;

  constructor() {}

  ngOnInit(): void {
    this.setIcon();
  }

  ngOnChanges(): void {
    this.setIcon();
  }

  get hasIcon(): boolean {
    return this.showIcon;
  }

  setIcon(): void {
    this.showIcon = this.icon?.showIcon(new FormControl(this.value));
  }
}
