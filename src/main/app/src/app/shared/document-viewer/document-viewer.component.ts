import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {FileFormat, FileFormatUtil} from '../../informatie-objecten/model/file-format';
import {PdfJsViewerComponent} from 'ng2-pdfjs-viewer';
import {DomSanitizer} from '@angular/platform-browser';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';

@Component({
    selector: 'zac-document-viewer',
    templateUrl: './document-viewer.component.html',
    styleUrls: ['./document-viewer.component.less']
})
export class DocumentViewerComponent implements OnInit, OnDestroy {

    @Input() document: EnkelvoudigInformatieobject;
    @ViewChild('pdfViewer') private pdfViewer: PdfJsViewerComponent;

    img: any;
    showDocumentViewer: boolean = false;
    private documentListener: WebsocketListener;

    constructor(private informatieObjectenService: InformatieObjectenService, private sanitizer: DomSanitizer,
                private websocketService: WebsocketService) {

    }

    ngOnInit(): void {
        if (FileFormatUtil.isPreviewAvailable(this.document.formaat)) {
            this.showDocumentViewer = true;
            this.loadDocument();
        }

        this.documentListener = this.websocketService.addListener(
            Opcode.UPDATED, ObjectType.ENKELVOUDIG_INFORMATIEOBJECT, this.document.uuid,
            () => {
                this.loadInformatieObject();

                if (FileFormatUtil.isPreviewAvailable(this.document.formaat)) {
                    this.showDocumentViewer = true;
                    this.loadDocument();
                }
            });
    }

    ngOnDestroy() {
        this.websocketService.removeListener(this.documentListener);
    }

    private loadDocument(): void {
        this.informatieObjectenService.getPreviewDocument(this.document.uuid, this.document.versie).subscribe(value => {
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

    private loadInformatieObject() {
        this.informatieObjectenService.readEnkelvoudigInformatieobject(this.document.uuid)
            .subscribe(informatieObject => {
                this.document = informatieObject;
            });
    }

    isImage(): boolean {
        return FileFormatUtil.isImage(this.document.formaat);
    }

    isPDF(): boolean {
        return this.document.formaat === FileFormat.PDF;
    }
}
