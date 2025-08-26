import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { ApiService } from './api.service';
import { 
  TeacherDto, 
  SimpleStringFilterDto, 
  LongIdDto, 
  RespSingleDto, 
  RespSliceDto,
  CourseTeacherAssocDto
} from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class TeacherService extends ApiService {

  // Get all teachers with pagination and filtering
  filterTeachers(filter: SimpleStringFilterDto): Observable<RespSliceDto<TeacherDto>> {
    console.log('TeacherService: Making request to /teacher/filter with filter:', filter);
    return this.post<RespSliceDto<TeacherDto>>('/teacher/filter', filter).pipe(
      tap(response => console.log('TeacherService: Received response:', response)),
      catchError(error => {
        console.error('TeacherService: Error in filterTeachers:', error);
        throw error;
      })
    );
  }

  // Get a single teacher by ID
  getTeacher(request: LongIdDto): Observable<RespSingleDto<TeacherDto>> {
    return this.post<RespSingleDto<TeacherDto>>('/teacher/get', request);
  }

  // Create or update a teacher
  upsertTeacher(teacher: TeacherDto): Observable<RespSingleDto<TeacherDto>> {
    return this.post<RespSingleDto<TeacherDto>>('/teacher/upsert', teacher);
  }

  // Delete a teacher
  deleteTeacher(id: number): Observable<RespSingleDto<void>> {
    return this.delete<RespSingleDto<void>>(`/teacher/${id}`);
  }

  // Associate teacher to course
  associateTeacherToCourse(assoc: CourseTeacherAssocDto): Observable<RespSingleDto<void>> {
    return this.post<RespSingleDto<void>>('/associateTeacherToCourse', assoc);
  }

  // Remove teacher from course
  removeTeacherFromCourse(assoc: CourseTeacherAssocDto): Observable<RespSingleDto<void>> {
    return this.post<RespSingleDto<void>>('/removeTeacherFromCourse', assoc);
  }
}
