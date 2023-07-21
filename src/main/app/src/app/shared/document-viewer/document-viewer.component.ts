import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from "@angular/core";
import { EnkelvoudigInformatieobject } from "../../informatie-objecten/model/enkelvoudig-informatieobject";
import { InformatieObjectenService } from "../../informatie-objecten/informatie-objecten.service";
import {
  FileFormat,
  FileFormatUtil,
} from "../../informatie-objecten/model/file-format";
import { DomSanitizer, SafeUrl } from "@angular/platform-browser";

@Component({
  selector: "zac-document-viewer",
  templateUrl: "./document-viewer.component.html",
  styleUrls: ["./document-viewer.component.less"],
})
export class DocumentViewerComponent implements OnInit, OnChanges {
  @Input() document: EnkelvoudigInformatieobject;

  previewSrc: SafeUrl = null;
  showPreview = false;

  constructor(
    private informatieObjectenService: InformatieObjectenService,
    private sanitizer: DomSanitizer,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    this.document = changes.document.currentValue;
    if (this.document && !changes.document.isFirstChange()) {
      this.loadDocument();
    }
  }

  ngOnInit(): void {
    this.loadDocument();
  }

  private loadDocument(): void {
    if (FileFormatUtil.isPreviewAvailable(this.document.formaat)) {
      this.showPreview = true;
      const url = this.informatieObjectenService.getPreviewUrl(
        this.document.uuid,
        this.document.versie,
      );
      this.previewSrc = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    } else {
      this.showPreview = false;
      this.previewSrc = null;
    }
  }

  isImage(): boolean {
    return FileFormatUtil.isImage(this.document.formaat);
  }

  isPDF(): boolean {
    return this.document.formaat === FileFormat.PDF;
  }
}
