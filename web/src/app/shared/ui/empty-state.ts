import { Component, input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  template: `
    <div class="px-6 py-10 text-center">
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="1.5"
        class="mx-auto mb-3 h-10 w-10 text-border-strong"
      >
        <rect x="3" y="4" width="18" height="16" rx="2" />
        <path d="M3 9h18M9 9v11" />
      </svg>
      <h4 class="text-[15px] font-semibold text-text">{{ title() }}</h4>
      @if (description()) {
        <p class="mx-auto mt-1.5 max-w-xs text-[13px] text-text-2">{{ description() }}</p>
      }
      <div class="mt-4">
        <ng-content />
      </div>
    </div>
  `,
})
export class EmptyState {
  readonly title = input.required<string>();
  readonly description = input<string | undefined>(undefined);
}
