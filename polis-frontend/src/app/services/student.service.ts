import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { 
  StudentDto, 
  SimpleStringFilterDto, 
  LongIdDto, 
  RespSingleDto, 
  RespSliceDto,
  CourseStudentAssocDto
} from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class StudentService extends ApiService {

  // Get all students with pagination and filtering
  filterStudents(filter: SimpleStringFilterDto): Observable<RespSliceDto<StudentDto>> {
    return this.post<RespSliceDto<StudentDto>>('/student/filter', filter);
  }

  // Get a single student by ID
  getStudent(request: LongIdDto): Observable<RespSingleDto<StudentDto>> {
    return this.post<RespSingleDto<StudentDto>>('/student/get', request);
  }

  // Create or update a student
  upsertStudent(student: StudentDto): Observable<RespSingleDto<StudentDto>> {
    return this.post<RespSingleDto<StudentDto>>('/student/upsert', student);
  }

  // Delete a student
  deleteStudent(request: LongIdDto): Observable<RespSingleDto<StudentDto>> {
    return this.post<RespSingleDto<StudentDto>>('/student/delete', request);
  }

  // Associate student to course
  associateStudentToCourse(assoc: CourseStudentAssocDto): Observable<RespSingleDto<void>> {
    return this.post<RespSingleDto<void>>('/associateStudentToCourse', assoc);
  }

  // Remove student from course
  removeStudentFromCourse(assoc: CourseStudentAssocDto): Observable<RespSingleDto<void>> {
    return this.post<RespSingleDto<void>>('/removeStudentFromCourse', assoc);
  }
}
