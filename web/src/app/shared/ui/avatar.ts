import { Component, computed, input } from '@angular/core';

const PALETTE = ['#2d7ff9', '#8b46ff', '#20c933', '#18bfff', '#ff6f2c', '#fcb400', '#d91f44', '#0a8f88'];

function colorFor(seed: string): string {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash * 31 + seed.charCodeAt(i)) | 0;
  }
  return PALETTE[Math.abs(hash) % PALETTE.length];
}

function initialsFrom(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) {
    return '?';
  }
  if (parts.length === 1) {
    return parts[0].slice(0, 2).toUpperCase();
  }
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

@Component({
  selector: 'app-avatar',
  template: `
    <span
      class="inline-grid flex-none place-items-center rounded-full text-[10px] font-semibold text-white"
      [style.background]="color()"
      [style.width.px]="size()"
      [style.height.px]="size()"
      [style.fontSize.px]="size() > 22 ? 11 : 9"
    >
      {{ initials() }}
    </span>
  `,
})
export class Avatar {
  readonly name = input.required<string>();
  readonly size = input(22);

  readonly initials = computed(() => initialsFrom(this.name()));
  readonly color = computed(() => colorFor(this.name()));
}
