import { Component, computed, input } from '@angular/core';

import { ChipColor, statusColor } from './status-color';

const BG: Record<ChipColor, string> = {
  blue: 'bg-sel-blue-bg text-sel-blue-tx',
  cyan: 'bg-sel-cyan-bg text-sel-cyan-tx',
  teal: 'bg-sel-teal-bg text-sel-teal-tx',
  green: 'bg-sel-green-bg text-sel-green-tx',
  yellow: 'bg-sel-yellow-bg text-sel-yellow-tx',
  orange: 'bg-sel-orange-bg text-sel-orange-tx',
  red: 'bg-sel-red-bg text-sel-red-tx',
  pink: 'bg-sel-pink-bg text-sel-pink-tx',
  purple: 'bg-sel-purple-bg text-sel-purple-tx',
  gray: 'bg-sel-gray-bg text-sel-gray-tx',
};

@Component({
  selector: 'app-chip',
  template: `
    <span
      class="inline-flex items-center gap-[5px] rounded-[9px] px-2 py-0.5 text-xs font-medium leading-[18px] whitespace-nowrap"
      [class]="classes()"
    >
      @if (dot()) {
        <span class="h-1.5 w-1.5 rounded-full bg-current opacity-85"></span>
      }
      <ng-content />
    </span>
  `,
})
export class Chip {
  readonly color = input<ChipColor | undefined>(undefined);
  readonly status = input<string | undefined>(undefined);
  readonly dot = input(true);

  readonly classes = computed(() => BG[this.color() ?? statusColor(this.status() ?? '')]);
}
