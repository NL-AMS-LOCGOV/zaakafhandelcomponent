import { Component, Input } from "@angular/core";
import { FileIcon } from "../../informatie-objecten/model/file-icon";
import { TranslateService } from "@ngx-translate/core";

@Component({
  standalone: true,
  selector: "zac-document-icon",
  templateUrl: "./document-icon.component.html",
  styleUrls: ["./document-icon.component.less"],
})
export class DocumentIconComponent {
  @Input() bestandsnaam: string;

  constructor(private translate: TranslateService) {}

  getFileIcon(filename) {
    return FileIcon.getIconByBestandsnaam(filename);
  }

  getFileTooltip(filetype: string): string {
    return filetype === "unknown"
      ? this.translate.instant("bestandstype.onbekend")
      : this.translate.instant("bestandstype", {
          type: filetype.toUpperCase(),
        });
  }
}
