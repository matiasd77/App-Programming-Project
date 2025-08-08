import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from './api.service';
import { 
  TeacherDto, 
  RespSingleDto, 
  RespSliceDto, 
  SimpleStringFilterDto, 
  LongIdDto 
} from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class TeacherService extends ApiService {

  // Create or update teacher
  upsertTeacher(teacher: TeacherDto): Observable<TeacherDto> {
    return this.post<RespSingleDto<TeacherDto>>('/upsertTeacher', teacher)
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to save teacher');
          }
          return response.data!;
        })
      );
  }

  // Get teachers with pagination and search
  filterTeachers(filter: SimpleStringFilterDto): Observable<RespSliceDto<TeacherDto>> {
    return this.post<RespSliceDto<TeacherDto>>('/filterTeachers', filter)
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to fetch teachers');
          }
          return response;
        })
      );
  }

  // Delete teacher
  deleteTeacher(id: number): Observable<void> {
    return this.post<RespSingleDto<void>>('/deleteTeacher', { id })
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to delete teacher');
          }
        })
      );
  }

  // Get single teacher by ID
  getTeacher(id: number): Observable<TeacherDto> {
    return this.post<RespSingleDto<TeacherDto>>('/getTeacher', { id })
      .pipe(
        map(response => {
          if (this.hasErrors(response)) {
            throw new Error(this.getFirstError(response) || 'Failed to fetch teacher');
          }
          return response.data!;
        })
      );
  }
} 