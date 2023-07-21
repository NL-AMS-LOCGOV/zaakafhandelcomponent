/*
 * SPDX-FileCopyrightText: 2021 - 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { MaterialFormBuilderService } from "../../material-form-builder/material-form-builder.service";
import { UtilService } from "../../../core/service/util.service";
import { InputFormField } from "../../material-form-builder/form-components/input/input-form-field";
import { IdentityService } from "../../../identity/identity.service";
import { EditComponent } from "../edit.component";
import { MedewerkerGroepFormField } from "../../material-form-builder/form-components/medewerker-groep/medewerker-groep-form-field";
import { FormControl } from "@angular/forms";
import { LoggedInUser } from "../../../identity/model/logged-in-user";

@Component({
  selector: "zac-edit-groep-behandelaar",
  templateUrl: "./edit-groep-behandelaar.component.html",
  styleUrls: [
    "../../static-text/static-text.component.less",
    "../edit.component.less",
    "./edit-groep-behandelaar.component.less",
  ],
})
export class EditGroepBehandelaarComponent
  extends EditComponent
  implements OnInit, OnDestroy
{
  @Input() formField: MedewerkerGroepFormField;
  @Input() reasonField: InputFormField;
  private loggedInUser: LoggedInUser;

  constructor(
    mfbService: MaterialFormBuilderService,
    utilService: UtilService,
    private identityService: IdentityService,
  ) {
    super(mfbService, utilService);
    this.identityService.readLoggedInUser().subscribe((user) => {
      this.loggedInUser = user;
    });
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  edit(): void {
    super.edit();

    if (this.reasonField) {
      this.formFields.setControl("reden", this.reasonField.formControl);
    }
  }

  release(): void {
    this.formField.medewerkerValue(null);
  }

  assignToMe(): void {
    this.formField.medewerkerValue(this.loggedInUser);
  }

  getFormControl(control: string): FormControl {
    return this.formField.formControl.get(control) as FormControl;
  }

  get showAssignToMe(): boolean {
    return (
      this.loggedInUser.id !== this.formField.medewerker.value?.id &&
      this.loggedInUser.groupIds.indexOf(this.formField.groep.value.id) >= 0
    );
  }

  get showRelease(): boolean {
    return this.formField.medewerker.value !== null;
  }
}
