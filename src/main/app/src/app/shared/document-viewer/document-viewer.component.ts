import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {FileFormat} from '../../informatie-objecten/model/file-format';
import {PdfJsViewerComponent} from 'ng2-pdfjs-viewer';

@Component({
    selector: 'zac-document-viewer',
    templateUrl: './document-viewer.component.html',
    styleUrls: ['./document-viewer.component.less']
})
export class DocumentViewerComponent implements OnInit {

    @Input() document: EnkelvoudigInformatieobject;
    @ViewChild('pdfViewer') private pdfViewer: PdfJsViewerComponent;
    showDocumentViewer: boolean = false;

    constructor(private informatieObjectenService: InformatieObjectenService) { }

    ngOnInit(): void {
        if (this.document.formaat === FileFormat.PDF) {
            this.showDocumentViewer = true;
            this.loadDocument();
        }
    }

    private loadDocument(): void {
        this.informatieObjectenService.getPreviewDocument(this.document.uuid).subscribe(value => {
            this.pdfViewer.openFile = false;
            this.pdfViewer.viewBookmark = false;
            this.pdfViewer.iframe.nativeElement.frameBorder = 0;
            this.pdfViewer.pdfSrc = value;
            this.pdfViewer.refresh();
        });
    }
}
