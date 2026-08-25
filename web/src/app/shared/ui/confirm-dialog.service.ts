import { Injectable, signal } from '@angular/core';

export interface ConfirmRequest {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  /** Styles the confirm button as destructive (red) instead of the default primary blue. */
  destructive?: boolean;
}

interface PendingConfirm extends ConfirmRequest {
  resolve: (confirmed: boolean) => void;
}

/**
 * App-wide "are you sure?" gate for destructive actions (archiving, etc.) —
 * one deliberate extra step between a click and an irreversible change.
 * Rendered by the single <app-confirm-dialog /> mounted in app.html.
 */
@Injectable({ providedIn: 'root' })
export class ConfirmDialogService {
  readonly pending = signal<PendingConfirm | null>(null);

  ask(request: ConfirmRequest): Promise<boolean> {
    return new Promise((resolve) => {
      this.pending.set({ ...request, resolve });
    });
  }

  resolve(confirmed: boolean): void {
    this.pending()?.resolve(confirmed);
    this.pending.set(null);
  }
}
