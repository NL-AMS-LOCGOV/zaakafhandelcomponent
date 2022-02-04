/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FileFormField} from './file-form-field';
import {IFormComponent} from '../../model/iform-component';
import {HttpClient, HttpEvent, HttpEventType, HttpHeaders} from '@angular/common/http';
import {Observable, Subscription} from 'rxjs';
import {UploadStatus} from './upload-status.enum';
import {FoutAfhandelingService} from '../../../../fout-afhandeling/fout-afhandeling.service';

@Component({
    templateUrl: './file.component.html',
    styleUrls: ['./file.component.less']
})
export class FileComponent implements OnInit, IFormComponent {

    @ViewChild('fileInput') fileInput: ElementRef;

    data: FileFormField;
    progress = 0;
    subscription: Subscription;
    status: string = UploadStatus.SELECTEER_BESTAND;

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService, private changeDetector: ChangeDetectorRef) {
    }

    ngOnInit(): void {
    }

    uploadFile(file: File) {
        this.data.uploadError = null;
        if (file) {

            if (!this.isBestandstypeToegestaan(file)) {
                this.data.uploadError = `Het bestandtype is niet toegestaan (${this.getBestandsextensie(file)})`;
                return;
            }

            if (file.size > this.getMaxSizeBytes()) {
                this.data.uploadError = `Bestand is te groot (${this.formatFileSizeMB(file.size)})`;
                return;
            }

            this.subscription = this.createRequest(file).subscribe({
                next: (event: HttpEvent<any>) => {
                    switch (event.type) {
                        case HttpEventType.Sent:
                            this.status = UploadStatus.BEZIG;
                            break;
                        case HttpEventType.ResponseHeader:
                            break;
                        case HttpEventType.UploadProgress:
                            this.progress = Math.round(event.loaded / event.total * 100);
                            this.updateInput(`${file.name} | ${this.progress}%`);
                            break;
                        case HttpEventType.Response:
                            this.fileInput.nativeElement.value = null;
                            this.updateInput(file.name);
                            this.status = UploadStatus.GEREED;
                            setTimeout(() => {
                                this.progress = 0;
                            }, 1500);
                    }
                },
                error: (error) => {
                    this.status = UploadStatus.SELECTEER_BESTAND;
                    this.fileInput.nativeElement.value = null;
                    this.data.uploadError = this.foutAfhandelingService.getFout(error);
                }
            });
        } else {
            this.updateInput(null);
        }
    }

    getMaxSizeBytes() {
        return this.data.fileSizeMB * 1024 * 1024;
    }

    formatFileSizeMB(bytes: number): string {
        return parseFloat(String(bytes / 1024 / 1024)).toFixed(2);
    }

    isBestandstypeToegestaan(file: File): boolean {
        const extensies = this.data.fileTypes;
        const extensiesArray = extensies.split(',').map(s => s.trim().toLowerCase());
        return extensiesArray.indexOf('.' + this.getBestandsextensie(file)) > -1;
    }

    getBestandsextensie(file: File) {
        if (file.name.indexOf('.') < 1) {
            return '-';
        }
        return file.name.split('.').pop().toLowerCase();
    }

    reset($event: MouseEvent) {
        $event.stopPropagation();
        if (!this.subscription.closed) {
            this.subscription.unsubscribe();
        }
        this.status = UploadStatus.SELECTEER_BESTAND;
        this.fileInput.nativeElement.value = null;
        this.updateInput(null);
    }

    createRequest(file: File): Observable<any> {
        this.updateInput(file.name);
        const formData: FormData = new FormData();
        formData.append('filename', file.name);
        formData.append('filesize', file.size.toString());
        formData.append('type', file.type);
        formData.append('file', file, file.name);
        const httpHeaders = new HttpHeaders();
        httpHeaders.append('Content-Type', 'multipart/form-data');
        httpHeaders.append('Accept', 'application/json');
        return this.http.post(this.data.uploadURL, formData, {
            reportProgress: true,
            observe: 'events',
            headers: httpHeaders
        });
    }

    updateInput(inputValue: string) {
        this.data.formControl.setValue(inputValue);
        this.changeDetector.detectChanges();
    }
}
