import { Injectable, signal } from '@angular/core';

type ThemeChoice = 'light' | 'dark' | null;

const STORAGE_KEY = 'sms.theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly media = window.matchMedia('(prefers-color-scheme: dark)');
  private readonly choice = signal<ThemeChoice>(this.restoreChoice());

  readonly isDark = signal(this.computeIsDark());

  constructor() {
    this.apply();
    this.media.addEventListener('change', () => {
      if (this.choice() === null) {
        this.isDark.set(this.computeIsDark());
      }
    });
  }

  toggle(): void {
    this.choice.set(this.isDark() ? 'light' : 'dark');
    localStorage.setItem(STORAGE_KEY, this.choice()!);
    this.apply();
  }

  private apply(): void {
    const choice = this.choice();
    if (choice) {
      document.documentElement.setAttribute('data-theme', choice);
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
    this.isDark.set(this.computeIsDark());
  }

  private computeIsDark(): boolean {
    return this.choice() === 'dark' || (this.choice() === null && this.media.matches);
  }

  private restoreChoice(): ThemeChoice {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored === 'light' || stored === 'dark' ? stored : null;
  }
}
