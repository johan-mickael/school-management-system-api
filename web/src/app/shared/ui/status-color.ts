export type ChipColor =
  | 'blue'
  | 'cyan'
  | 'teal'
  | 'green'
  | 'yellow'
  | 'orange'
  | 'red'
  | 'pink'
  | 'purple'
  | 'gray';

const STATUS_COLOR: Record<string, ChipColor> = {
  PRESENT: 'green',
  LATE: 'orange',
  ABSENT: 'red',
  EXCUSED: 'blue',
  ACTIVE: 'green',
  ARCHIVED: 'gray',
  SCHEDULED: 'cyan',
  SIGNING_OPEN: 'green',
  SIGNING_CLOSED: 'gray',
  CANCELLED: 'red',
  OPEN: 'green',
  CLOSED: 'gray',
  ADMIN: 'purple',
  TEACHER: 'teal',
  STUDENT: 'blue',
  AT_RISK: 'yellow',
  FLAGGED: 'red',
  ON_TRACK: 'green',
};

export function statusColor(status: string): ChipColor {
  return STATUS_COLOR[status] ?? 'gray';
}
