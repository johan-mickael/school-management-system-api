import { Component, input } from '@angular/core';

export type NavIcon =
  | 'dashboard'
  | 'attendance'
  | 'grades'
  | 'sessions'
  | 'grading'
  | 'correct'
  | 'promotions'
  | 'teachers'
  | 'users'
  | 'reporting'
  | 'archive';

@Component({
  selector: 'app-nav-icon',
  template: `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="h-[18px] w-[18px]" aria-hidden="true">
      @switch (name()) {
        @case ('dashboard') {
          <rect x="3" y="3" width="7" height="9" rx="1.5" />
          <rect x="14" y="3" width="7" height="5" rx="1.5" />
          <rect x="14" y="12" width="7" height="9" rx="1.5" />
          <rect x="3" y="16" width="7" height="5" rx="1.5" />
        }
        @case ('attendance') {
          <circle cx="12" cy="12" r="9" />
          <path d="M8 12l3 3 5-6" />
        }
        @case ('grades') {
          <path d="M6 3l-1 18M19 3l-1 18M4 9h16M3 15h16" />
        }
        @case ('sessions') {
          <rect x="3" y="4" width="18" height="16" rx="2" />
          <path d="M3 9h18M8 3v4M16 3v4" />
        }
        @case ('grading') {
          <path d="M12 5v14M5 12h14" />
        }
        @case ('correct') {
          <rect x="4" y="4" width="16" height="16" rx="3" />
          <path d="M8 12l3 3 5-6" />
        }
        @case ('promotions') {
          <path d="M10 13a5 5 0 007 0l2-2a5 5 0 00-7-7l-1 1" />
          <path d="M14 11a5 5 0 00-7 0l-2 2a5 5 0 007 7l1-1" />
        }
        @case ('teachers') {
          <circle cx="12" cy="8" r="4" />
          <path d="M4 20c0-4 4-6 8-6s8 2 8 6" />
        }
        @case ('users') {
          <circle cx="9" cy="8" r="3.5" />
          <path d="M2 20c0-3.5 3-5.5 7-5.5s7 2 7 5.5" />
          <path d="M16 4.5a3.5 3.5 0 010 7M22 20c0-3-2-4.7-4.5-5.3" />
        }
        @case ('reporting') {
          <path d="M4 20V8a3 3 0 016 0M5 12h6" />
          <path d="M13 8h5l-2 12" />
        }
        @case ('archive') {
          <rect x="3" y="4" width="18" height="5" rx="1.5" />
          <path d="M5 9v9a2 2 0 002 2h10a2 2 0 002-2V9M10 13h4" />
        }
      }
    </svg>
  `,
})
export class NavIconComponent {
  readonly name = input.required<NavIcon>();
}
