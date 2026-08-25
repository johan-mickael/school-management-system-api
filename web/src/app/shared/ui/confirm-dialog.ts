import { Component, ElementRef, HostListener, effect, inject, viewChild } from '@angular/core';

import { ConfirmDialogService } from './confirm-dialog.service';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    @if (svc.pending(); as req) {
      <div class="fixed inset-0 z-[200] flex items-center justify-center bg-[var(--alpha-dark-40)] p-4" (click)="cancel()">
        <div
          class="w-full max-w-sm rounded-lg border border-border bg-surface p-5 shadow-[var(--shadow-lg)]"
          role="alertdialog"
          aria-modal="true"
          [attr.aria-label]="req.title"
          (click)="$event.stopPropagation()"
        >
          <h2 class="mb-1.5 text-[15px] font-semibold text-text">{{ req.title }}</h2>
          <p class="mb-5 text-[13px] text-text-2">{{ req.message }}</p>
          <div class="flex justify-end gap-2">
            <button class="btn-sec btn-sm" (click)="cancel()">{{ req.cancelLabel ?? 'Cancel' }}</button>
            <button
              #confirmBtn
              class="btn-sm"
              [class]="req.destructive ? 'btn bg-[var(--red-500)] hover:bg-[var(--red-600)]' : 'btn'"
              (click)="confirm()"
            >
              {{ req.confirmLabel ?? 'Confirm' }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
})
export class ConfirmDialog {
  readonly svc = inject(ConfirmDialogService);
  private readonly confirmBtn = viewChild<ElementRef<HTMLButtonElement>>('confirmBtn');

  constructor() {
    effect(() => this.confirmBtn()?.nativeElement.focus());
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.svc.pending()) {
      this.cancel();
    }
  }

  confirm(): void {
    this.svc.resolve(true);
  }

  cancel(): void {
    this.svc.resolve(false);
  }
}
