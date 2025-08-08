// Base DTOs
export interface StudentDto {
  id?: number;
  firstName: string;
  lastName: string;
  serialNumber: string;
  course?: CourseDto;
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
  code: string;
  title: string;
  description: string;
  year: number;
  teacher?: TeacherDto;
  students?: StudentDto[];
}

// Association DTOs
export interface CourseStudentAssocDto {
  idStudent: number;
  idCourse: number;
}

export interface CourseTeacherAssocDto {
  idTeacher: number;
  idCourse: number;
}

export interface LongIdDto {
  id: number;
}

// Filter and Pagination DTOs
export interface Pagination {
  pageNumber: number;
  pageSize: number;
  sort?: Sorting[];
}

export interface Sorting {
  property: string;
  direction: 'ASC' | 'DESC';
}

export interface SimpleStringFilterDto {
  filter?: string;
  pagination: Pagination;
}

// Response DTOs
export interface ServerStatus {
  code: string;
  message: string;
  action: string;
  severity: 'OK' | 'ERROR' | 'FATAL';
}

export interface ResponseWithStatusDto {
  status: ServerStatus[];
}

export interface RespSingleDto<T> extends ResponseWithStatusDto {
  data?: T;
}

export interface RespSliceDto<T> extends ResponseWithStatusDto {
  slice?: {
    content: T[];
    hasNext: boolean;
    pageable: {
      pageNumber: number;
      pageSize: number;
    };
  };
}

// Error codes enum
export enum ServerErrorEnum {
  OK = 'OK',
  UNKNOWN_ERROR = 'UNKNOWN_ERROR',
  TEACHER_MISSING = 'TEACHER_MISSING',
  COURSE_MISSING = 'COURSE_MISSING',
  COURSE_NOT_FOUND = 'COURSE_NOT_FOUND',
  TEACHER_NOT_FOUND = 'TEACHER_NOT_FOUND',
  STUDENT_NOT_FOUND = 'STUDENT_NOT_FOUND',
  FILTER_MISSING = 'FILTER_MISSING',
  STUDENT_MISSING = 'STUDENT_MISSING',
  DELETE_COURSE_NOT_ALLOWED = 'DELETE_COURSE_NOT_ALLOWED',
  DELETE_TEACHER_NOT_ALLOWED = 'DELETE_TEACHER_NOT_ALLOWED',
  DELETE_STUDENT_NOT_ALLOWED = 'DELETE_STUDENT_NOT_ALLOWED'
} 