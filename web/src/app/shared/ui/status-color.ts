export type ChipColor = 'blue' | 'green' | 'orange' | 'red' | 'purple' | 'gray';

const STATUS_COLOR: Record<string, ChipColor> = {
  PRESENT: 'green',
  LATE: 'orange',
  ABSENT: 'red',
  EXCUSED: 'blue',
  ACTIVE: 'green',
  ARCHIVED: 'gray',
  SCHEDULED: 'blue',
  SIGNING_OPEN: 'green',
  SIGNING_CLOSED: 'gray',
  CANCELLED: 'red',
  OPEN: 'green',
  CLOSED: 'gray',
  ADMIN: 'purple',
  TEACHER: 'green',
  STUDENT: 'blue',
  AT_RISK: 'orange',
  FLAGGED: 'red',
  ON_TRACK: 'green',
};

export function statusColor(status: string): ChipColor {
  return STATUS_COLOR[status] ?? 'gray';
}
