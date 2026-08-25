import { Injectable, signal } from '@angular/core';

/**
 * Lets a leaf page contribute the dynamic tail of the admin breadcrumb
 * (e.g. a promotion or archived cohort name) without the shell needing to
 * know about page-specific data. Pages that show one record set it on load
 * and clear it on destroy.
 */
@Injectable({ providedIn: 'root' })
export class BreadcrumbService {
  readonly label = signal<string | null>(null);

  set(label: string): void {
    this.label.set(label);
  }

  clear(): void {
    this.label.set(null);
  }
}
