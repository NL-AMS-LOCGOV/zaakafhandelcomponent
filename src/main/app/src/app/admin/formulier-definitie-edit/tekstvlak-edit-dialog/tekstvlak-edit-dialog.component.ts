import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import { DialogData } from "../../../shared/dialog/dialog-data";

@Component({
  selector: "zac-tekstvlak-edit-dialog",
  templateUrl: "./tekstvlak-edit-dialog.component.html",
  styleUrls: ["./tekstvlak-edit-dialog.component.less"],
})
export class TekstvlakEditDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
}
