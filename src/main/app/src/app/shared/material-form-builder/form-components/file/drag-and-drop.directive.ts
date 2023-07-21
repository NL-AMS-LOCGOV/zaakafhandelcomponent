import {
  Directive,
  EventEmitter,
  HostBinding,
  HostListener,
  Output,
} from "@angular/core";

@Directive({
  selector: "[DropZone]",
})
export class DragAndDropDirective {
  @Output() onFileDropped = new EventEmitter<any>();
  @HostBinding("style.border") private border = "solid transparent";

  @HostListener("dragover", ["$event"]) public onDragOver(evt): any {
    evt.preventDefault();
    evt.stopPropagation();
    this.border = "dotted #FF4D2A";
  }

  @HostListener("dragleave", ["$event"]) public onDragLeave(evt): any {
    evt.preventDefault();
    evt.stopPropagation();
    this.border = "solid transparent";
  }

  @HostListener("drop", ["$event"]) public ondrop(evt): any {
    evt.preventDefault();
    evt.stopPropagation();
    this.border = "solid transparent";
    const files = evt.dataTransfer.files;
    if (files.length > 0) {
      this.onFileDropped.emit(files[0]);
    }
  }
}
