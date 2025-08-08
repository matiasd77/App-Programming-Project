import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ResponseWithStatusDto, ServerStatus } from '../models/dto.types';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  protected post<T>(endpoint: string, data: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, data)
      .pipe(
        catchError(this.handleError.bind(this))
      );
  }

  protected handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.error && error.error.status) {
        const serverStatus = error.error.status as ServerStatus[];
        if (serverStatus.length > 0) {
          errorMessage = serverStatus[0].message;
        }
      } else {
        errorMessage = `Server error: ${error.status} ${error.statusText}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }

  protected hasErrors(response: ResponseWithStatusDto): boolean {
    return response.status && response.status.some(status => 
      status.severity === 'ERROR' || status.severity === 'FATAL'
    );
  }

  protected getFirstError(response: ResponseWithStatusDto): string | null {
    if (response.status && response.status.length > 0) {
      const errorStatus = response.status.find(status => 
        status.severity === 'ERROR' || status.severity === 'FATAL'
      );
      return errorStatus ? errorStatus.message : null;
    }
    return null;
  }
} 