import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from './api.service';
import { 
  StudentDto, 
  RespSingleDto, 
  RespSliceDto, 
  SimpleStringFilterDto, 
  LongIdDto, 
  CourseStudentAssocDto 
} from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class StudentService extends ApiService {

  // Create or update student
  upsertStudent(student: StudentDto): Observable<StudentDto> {
    return this.post<RespSingleDto<StudentDto>>('/upsertStudent', student)
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to save student');
          }
          return response.data!;
        })
      );
  }

  // Get students with pagination and search
  filterStudents(filter: SimpleStringFilterDto): Observable<RespSliceDto<StudentDto>> {
    return this.post<RespSliceDto<StudentDto>>('/filterStudents', filter)
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to fetch students');
          }
          return response;
        })
      );
  }

  // Delete student
  deleteStudent(id: number): Observable<void> {
    return this.post<RespSingleDto<void>>('/deleteStudent', { id })
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to delete student');
          }
        })
      );
  }

  // Get single student by ID
  getStudent(id: number): Observable<StudentDto> {
    return this.post<RespSingleDto<StudentDto>>('/getStudent', { id })
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to fetch student');
          }
          return response.data!;
        })
      );
  }

  // Associate student to course
  associateStudentToCourse(studentId: number, courseId: number): Observable<void> {
    const assoc: CourseStudentAssocDto = { idStudent: studentId, idCourse: courseId };
    return this.post<RespSingleDto<void>>('/associateStudentToCourse', assoc)
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to associate student to course');
          }
        })
      );
  }

  // Remove student from course
  removeStudentFromCourse(studentId: number, courseId: number): Observable<void> {
    const assoc: CourseStudentAssocDto = { idStudent: studentId, idCourse: courseId };
    return this.post<RespSingleDto<void>>('/removeStudentFromCourse', assoc)
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to remove student from course');
          }
        })
      );
  }
} 