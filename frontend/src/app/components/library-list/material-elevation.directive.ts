import { Directive, ElementRef, HostListener, Renderer2, OnChanges, SimpleChanges } from '@angular/core';

@Directive({
  selector: '[appMaterialElevation]'
})
export class MaterialElevationDirective implements OnChanges {

  defaultElevation = 2;

  raisedElevation = 8;

  constructor(
    private element: ElementRef,
    private renderer: Renderer2
  ) {
    this.setElevation(this.defaultElevation);
  }

  ngOnChanges(_changes: SimpleChanges): void {
    this.setElevation(this.defaultElevation);
  }

  @HostListener('mouseenter')
  onMouseEnter(): void {
    this.setElevation(this.raisedElevation);
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.setElevation(this.defaultElevation);
  }

  setElevation(amount: number): void {
    // remove all elevation classes
    const classesToRemove = Array.from((<HTMLElement>this.element.nativeElement).classList).filter(c => c.startsWith('mat-elevation-z'));
    classesToRemove.forEach((c) => {
      this.renderer.removeClass(this.element.nativeElement, c);
    });

    // add the given elevation class
    const newClass = `mat-elevation-z${amount}`;
    this.renderer.addClass(this.element.nativeElement, newClass);
  }
}
