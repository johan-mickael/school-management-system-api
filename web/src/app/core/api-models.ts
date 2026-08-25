export interface StudentResponse {
  id: string;
  number: string;
  firstName: string;
  lastName: string;
  email: string;
  status: 'ACTIVE' | 'ARCHIVED';
  enrolledAt: string;
  promotionId: string | null;
}

export interface TeacherResponse {
  id: string;
  number: string;
  firstName: string;
  lastName: string;
  email: string;
  status: 'ACTIVE' | 'ARCHIVED';
  hiredAt: string;
}

export interface CourseResponse {
  id: string;
  code: string;
  title: string;
  coefficient: number;
  promotionId: string;
  teacherId: string | null;
}

export interface PromotionResponse {
  id: string;
  name: string;
  academicYear: string;
  capacity: number;
  occupancy: number;
  status: 'ACTIVE' | 'ARCHIVED';
}

export interface SessionResponse {
  id: string;
  courseId: string;
  promotionId: string;
  teacherId: string;
  start: string;
  end: string;
  gracePeriodSeconds: number;
  status: 'SCHEDULED' | 'SIGNING_OPEN' | 'SIGNING_CLOSED' | 'CANCELLED';
}

export interface AttendanceRecordResponse {
  id: string;
  sessionId: string;
  studentId: string;
  status: 'PRESENT' | 'LATE' | 'ABSENT' | 'EXCUSED';
  signedAt: string | null;
  justification: string | null;
}

export interface GradeResponse {
  id: string;
  studentId: string;
  courseId: string;
  examId: string | null;
  score: number;
  coefficient: number;
}

export interface CourseAverageResponse {
  courseId: string;
  average: number;
}

export interface StudentAveragesResponse {
  studentId: string;
  courseAverages: CourseAverageResponse[];
  overallAverage: number | null;
}

export interface PromotionRankingEntryResponse {
  studentId: string;
  average: number;
  rank: number;
}

export interface PromotionSummaryResponse {
  promotionId: string;
  studentCount: number;
  averageAttendanceRate: number | null;
  averageGrade: number | null;
}

export interface StudentSummaryResponse {
  studentId: string;
  attendanceRate: number | null;
  averageGrade: number | null;
}

export interface AtRiskStudentResponse {
  studentId: string;
  attendanceRate: number | null;
  averageGrade: number | null;
}

export interface UserResponse {
  id: string;
  username: string;
  role: string;
  personId: string | null;
}
