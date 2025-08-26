import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ServerErrorEnum } from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  protected post<T>(endpoint: string, data: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, data)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.log('API Error:', error);
          return this.handleError(error);
        })
      );
  }

  protected delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.log('API Error:', error);
          return this.handleError(error);
        })
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.log('handleError called with:', error);
    console.log('error.status:', error.status);
    console.log('error.error:', error.error);
    console.log('error.error type:', typeof error.error);
    
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.error?.error?.code) {
        const errorCode = error.error.error.code;
        errorMessage = this.getErrorMessage(errorCode);
      } else {
        errorMessage = `Server error: ${error.status}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }

  private getErrorMessage(errorCode: string): string {
    switch (errorCode) {
      case ServerErrorEnum.VALIDATION_ERROR:
        return 'Validation error. Please check your input.';
      case ServerErrorEnum.STUDENT_NOT_FOUND:
        return 'Student not found.';
      case ServerErrorEnum.TEACHER_NOT_FOUND:
        return 'Teacher not found.';
      case ServerErrorEnum.COURSE_NOT_FOUND:
        return 'Course not found.';
      case ServerErrorEnum.DELETE_STUDENT_NOT_ALLOWED:
        return 'Cannot delete student. They are enrolled in courses.';
      case ServerErrorEnum.DELETE_TEACHER_NOT_ALLOWED:
        return 'Cannot delete teacher. They are assigned to courses.';
      case ServerErrorEnum.DELETE_COURSE_NOT_ALLOWED:
        return 'Cannot delete course. It has enrolled students.';
      default:
        return 'An unexpected error occurred.';
    }
  }
}
