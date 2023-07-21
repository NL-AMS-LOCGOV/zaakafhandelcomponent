/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { TaakDocumentUploadFormField } from "./taak-document-upload-form-field";
import { FormComponent } from "../../model/form-component";
import {
  HttpClient,
  HttpEvent,
  HttpEventType,
  HttpHeaders,
} from "@angular/common/http";
import { Observable, Subscription } from "rxjs";
import { FoutAfhandelingService } from "../../../../fout-afhandeling/fout-afhandeling.service";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from "@angular/forms";
import { InformatieObjectenService } from "../../../../informatie-objecten/informatie-objecten.service";
import { Informatieobjecttype } from "../../../../informatie-objecten/model/informatieobjecttype";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./taak-document-upload.component.html",
  styleUrls: ["./taak-document-upload.component.less"],
})
export class TaakDocumentUploadComponent
  extends FormComponent
  implements OnInit
{
  @ViewChild("fileInput") fileInput: ElementRef;
  data: TaakDocumentUploadFormField;
  progress = 0;
  subscription: Subscription;
  formGroup: FormGroup;
  uploadControl: FormControl;
  titelControl: FormControl;
  typeControl: FormControl;
  types$: Observable<Informatieobjecttype[]>;
  UploadStatus = {
    SELECTEER_BESTAND: "SELECTEER_BESTAND",
    BEZIG: "BEZIG",
    GEREED: "GEREED",
  };
  status = this.UploadStatus.SELECTEER_BESTAND;

  constructor(
    public translate: TranslateService,
    private informatieObjectenService: InformatieObjectenService,
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
    private formBuilder: FormBuilder,
  ) {
    super();
  }

  ngOnInit(): void {
    this.types$ =
      this.informatieObjectenService.listInformatieobjecttypesForZaak(
        this.data.zaakUUID,
      );
    this.uploadControl = new FormControl(null, this.data.formControl.validator);
    this.titelControl = new FormControl(this.data.defaultTitel, [
      Validators.required,
    ]);
    this.typeControl = new FormControl(null, [Validators.required]);
    this.formGroup = this.formBuilder.group({
      bestandsnaam: this.uploadControl,
      documentTitel: this.titelControl,
      documentType: this.typeControl,
    });
  }

  uploadFile(file: File) {
    this.data.uploadError = null;
    if (file) {
      if (!this.data.isBestandstypeToegestaan(file)) {
        this.data.uploadError = `Het bestandstype is niet toegestaan (${this.data.getBestandsextensie(
          file,
        )})`;
        return;
      }
      if (!this.data.isBestandsgrootteToegestaan(file)) {
        this.data.uploadError = `Het bestand is te groot (${this.data.getBestandsgrootteMB(
          file,
        )}MB)`;
        return;
      }
      this.uploadControl.setValue(file.name);
      this.updateValue();
      this.subscription = this.createRequest(file).subscribe({
        next: (event: HttpEvent<any>) => {
          switch (event.type) {
            case HttpEventType.Sent:
              this.status = this.UploadStatus.BEZIG;
              break;
            case HttpEventType.ResponseHeader:
              break;
            case HttpEventType.UploadProgress:
              this.progress = Math.round((event.loaded / event.total) * 100);
              this.uploadControl.setValue(`${file.name} | ${this.progress}%`);
              break;
            case HttpEventType.Response:
              this.fileInput.nativeElement.value = null;
              this.uploadControl.setValue(file.name);
              this.status = this.UploadStatus.GEREED;
              setTimeout(() => {
                this.progress = 0;
              }, 1500);
          }
        },
        error: (error) => {
          this.uploadControl.setValue(null);
          this.updateValue();
          this.status = this.UploadStatus.SELECTEER_BESTAND;
          this.fileInput.nativeElement.value = null;
          this.data.uploadError = this.foutAfhandelingService.getFout(error);
        },
      });
    } else {
      this.uploadControl.setValue(null);
      this.updateValue();
    }
  }

  reset($event: MouseEvent) {
    $event.stopPropagation();
    if (!this.subscription.closed) {
      this.subscription.unsubscribe();
    }
    this.status = this.UploadStatus.SELECTEER_BESTAND;
    this.fileInput.nativeElement.value = null;
    this.uploadControl.setValue(null);
    this.updateValue();
  }

  createRequest(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append("filename", file.name);
    formData.append("filesize", file.size.toString());
    formData.append("type", file.type);
    formData.append("file", file, file.name);
    const httpHeaders = new HttpHeaders();
    httpHeaders.append("Content-Type", "multipart/form-data");
    httpHeaders.append("Accept", "application/json");
    return this.http.post(this.data.uploadURL, formData, {
      reportProgress: true,
      observe: "events",
      headers: httpHeaders,
    });
  }

  updateValue() {
    if (!this.data.required) {
      if (this.uploadControl.value) {
        this.data.formControl.setValidators(Validators.required);
      } else {
        this.data.formControl.setValidators([]);
      }
    }
    if (this.uploadControl.value && this.formGroup.valid) {
      this.data.formControl.setValue(JSON.stringify(this.formGroup.value));
    } else {
      this.data.formControl.setValue(null);
    }
  }

  compareInfoObjectType(
    object1: Informatieobjecttype,
    object2: Informatieobjecttype,
  ): boolean {
    if (object1 && object2) {
      return object1.uuid === object2.uuid;
    }
    return false;
  }

  getErrorMessage(): string {
    if (this.data.uploadError) {
      return this.data.uploadError;
    }
    return super.getErrorMessage();
  }
}
