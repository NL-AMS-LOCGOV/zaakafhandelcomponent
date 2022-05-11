import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {FileFormat, FileFormatUtil} from '../../informatie-objecten/model/file-format';
import {PdfJsViewerComponent} from 'ng2-pdfjs-viewer';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
    selector: 'zac-document-viewer',
    templateUrl: './document-viewer.component.html',
    styleUrls: ['./document-viewer.component.less']
})
export class DocumentViewerComponent implements OnInit {

    @Input() document: EnkelvoudigInformatieobject;
    @ViewChild('pdfViewer') private pdfViewer: PdfJsViewerComponent;

    img: any;
    showDocumentViewer: boolean = false;

    constructor(private informatieObjectenService: InformatieObjectenService, private sanitizer: DomSanitizer) { }

    ngOnInit(): void {
        if (FileFormatUtil.isPreviewAvailable(this.document.formaat)) {
            this.showDocumentViewer = true;
            this.loadDocument();
        }
    }

    private loadDocument(): void {
        this.informatieObjectenService.getPreviewDocument(this.document.uuid).subscribe(value => {
            if (this.isPDF()) {
                this.pdfViewer.openFile = false;
                this.pdfViewer.viewBookmark = false;
                this.pdfViewer.iframe.nativeElement.frameBorder = 0;
                this.pdfViewer.pdfSrc = value;
                this.pdfViewer.refresh();
            }

            if (this.isImage()) {
                // Create virtual url
                const imageUrl = URL.createObjectURL(value);
                // Tell angular it is safe
                this.img = this.sanitizer.bypassSecurityTrustUrl(imageUrl);
            }
        });
    }

    isImage(): boolean {
        return FileFormatUtil.isImage(this.document.formaat);
    }

    isPDF(): boolean {
        return this.document.formaat === FileFormat.PDF;
    }
}
