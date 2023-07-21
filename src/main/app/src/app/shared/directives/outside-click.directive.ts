import {
  Directive,
  ElementRef,
  EventEmitter,
  Inject,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  PLATFORM_ID,
} from "@angular/core";
import { fromEvent, Subscription } from "rxjs";
import { DOCUMENT, isPlatformBrowser } from "@angular/common";
import { filter } from "rxjs/operators";
import { UtilService } from "../../core/service/util.service";

@Directive({
  selector: "[zacOutsideClick]",
})
export class OutsideClickDirective implements OnInit, OnDestroy {
  private static inclusions: string[] = [
    "mat-option-text",
    "mdc-list-item__primary-text",
  ];

  @Output("zacOutsideClick") outsideClick = new EventEmitter<MouseEvent>();

  private subscription: Subscription;

  constructor(
    private element: ElementRef,
    @Optional() @Inject(DOCUMENT) private document: any,
    @Inject(PLATFORM_ID) private platformId: {},
    private utilService: UtilService,
  ) {}

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    setTimeout(() => {
      this.subscription = fromEvent<MouseEvent>(this.document, "click")
        .pipe(
          filter((event) => {
            if (this.utilService.hasEditOverlay()) {
              return false;
            }
            const clickTarget = event.target as HTMLElement;
            return !OutsideClickDirective.isOrContainsClickTarget(
              this.element.nativeElement,
              clickTarget,
            );
          }),
        )
        .subscribe((event) => this.outsideClick.emit(event));
    }, 0);
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private static isOrContainsClickTarget(
    element: HTMLElement,
    clickTarget: HTMLElement,
  ) {
    const included: boolean = this.inclusions.some((value) =>
      clickTarget.classList.contains(value),
    );
    return element === clickTarget || element.contains(clickTarget) || included;
  }
}
