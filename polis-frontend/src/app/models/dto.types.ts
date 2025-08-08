export interface Pagination {
  pageNumber: number;
  pageSize: number;
}

export interface Sorting {
  field: string;
  direction: 'ASC' | 'DESC';
}

export interface SimpleStringFilterDto {
  filter?: string;
  pagination: Pagination;
  sorting?: Sorting;
}

export interface LongIdDto {
  id: number;
}

export interface StudentDto {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  course?: CourseDto; // Changed from courses to course to match backend
}

export interface TeacherDto {
  id?: number;
  firstName: string;
  lastName: string;
  title: string;
  courses?: CourseDto[];
}

export interface CourseDto {
  id?: number;
  title: string;
  code: string;
  description?: string;
  year?: number;
  teacher?: TeacherDto;
  students?: StudentDto[];
}

export interface CourseStudentAssocDto {
  idStudent: number;
  idCourse: number;
}

export interface CourseTeacherAssocDto {
  idTeacher: number;
  idCourse: number;
}

export interface RespSingleDto<T> {
  data: T;
  status: ServerStatus | any[];
  error?: ErrorContext;
}

export interface RespSliceDto<T> {
  slice: {
    content: T[];
    hasNext: boolean;
    pageable: any;
  };
  status: ServerStatus | any[];
  error?: ErrorContext;
}

export interface ServerStatus {
  code: number;
  message: string;
}

export interface ErrorContext {
  code: ServerErrorEnum;
  message: string;
  severity: ErrorSeverityEnum;
}

export enum ServerErrorEnum {
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  STUDENT_NOT_FOUND = 'STUDENT_NOT_FOUND',
  TEACHER_NOT_FOUND = 'TEACHER_NOT_FOUND',
  COURSE_NOT_FOUND = 'COURSE_NOT_FOUND',
  DELETE_STUDENT_NOT_ALLOWED = 'DELETE_STUDENT_NOT_ALLOWED',
  DELETE_TEACHER_NOT_ALLOWED = 'DELETE_TEACHER_NOT_ALLOWED',
  DELETE_COURSE_NOT_ALLOWED = 'DELETE_COURSE_NOT_ALLOWED',
  UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}

export enum ErrorSeverityEnum {
  ERROR = 'ERROR',
  WARNING = 'WARNING',
  INFO = 'INFO'
}
