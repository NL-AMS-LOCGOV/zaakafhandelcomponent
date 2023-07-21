import { Component, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { TranslateService } from "@ngx-translate/core";
import { HiddenFormField } from "./hidden-form-field";

@Component({
  templateUrl: "./hidden.component.html",
  styleUrls: ["./hidden.component.less"],
})
export class HiddenComponent extends FormComponent implements OnInit {
  data: HiddenFormField;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {}
}
