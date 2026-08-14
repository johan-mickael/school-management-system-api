import { Component, input } from '@angular/core';

import { Chip } from './chip';

@Component({
  selector: 'app-feedback',
  imports: [Chip],
  template: `
    <div class="mb-4 flex items-center gap-2 rounded-md border border-sel-red-bg bg-sel-red-bg/40 px-3 py-2 text-[13px]">
      <app-chip color="red" [dot]="false">Error</app-chip>
      <span class="text-text">{{ message() }}</span>
    </div>
  `,
})
export class Feedback {
  readonly message = input.required<string>();
}
