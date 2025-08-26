import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { 
  CourseDto, 
  SimpleStringFilterDto, 
  LongIdDto, 
  RespSingleDto, 
  RespSliceDto,
  CourseStudentAssocDto
} from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class CourseService extends ApiService {

  // Get all courses with pagination and filtering
  filterCourses(filter: SimpleStringFilterDto): Observable<RespSliceDto<CourseDto>> {
    return this.post<RespSliceDto<CourseDto>>('/course/filter', filter);
  }

  // Get a single course by ID
  getCourse(request: LongIdDto): Observable<RespSingleDto<CourseDto>> {
    return this.post<RespSingleDto<CourseDto>>('/course/get', request);
  }

  // Create or update a course
  upsertCourse(course: CourseDto): Observable<RespSingleDto<CourseDto>> {
    return this.post<RespSingleDto<CourseDto>>('/course/upsert', course);
  }

  // Delete a course
  deleteCourse(id: number): Observable<RespSingleDto<void>> {
    return this.delete<RespSingleDto<void>>(`/course/${id}`);
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

