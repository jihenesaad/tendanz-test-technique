import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { QuoteRequest, QuoteResponse } from '../models/quote.model';

/**
 * Service for managing quotes
 * This service handles all API communication with the backend pricing engine
 *
 * TODO: Candidate must implement the following methods:
 * - createQuote(request: QuoteRequest): Observable<QuoteResponse>
 * - getQuote(id: number): Observable<QuoteResponse>
 * - getQuotes(filters?: {productId?: number, minPrice?: number}): Observable<QuoteResponse[]>
 *
 * Requirements:
 * - Use HttpClient for HTTP requests
 * - Use catchError operator to handle errors
 * - Base URL should be configurable via environment.apiUrl
 * - Handle error responses appropriately (log errors, throw user-friendly messages)
 */
@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private readonly apiUrl = environment.apiUrl;
  private readonly endpoint = '/quotes';

  constructor(private http: HttpClient) {}

  /**
   * Create a new quote
   * POST /api/quotes
   *
   * @param request Quote request data
   * @returns Observable of the created quote response with calculated pricing
   *
   * TODO: Implement this method
   */
  createQuote(request: QuoteRequest): Observable<QuoteResponse> {
    return this.http.post<QuoteResponse>(`${this.apiUrl}${this.endpoint}`, request).pipe(
      catchError((error) => this.handleError(error))
    );
  }

  /**
   * Get a single quote by ID
   * GET /api/quotes/:id
   *
   * @param id Quote ID
   * @returns Observable of the quote details
   *
   * TODO: Implement this method
   */
  getQuote(id: number): Observable<QuoteResponse> {
    return this.http.get<QuoteResponse>(`${this.apiUrl}${this.endpoint}/${id}`).pipe(
      catchError((error) => this.handleError(error))
    );
    
  }

  /**
   * Get all quotes with optional filtering
   * GET /api/quotes?productId=X&minPrice=Y
   *
   * @param filters Optional filter criteria
   * @param filters.productId Filter by product ID
   * @param filters.minPrice Filter by minimum price
   * @returns Observable of array of quotes
   *
   * TODO: Implement this method
   */
  getQuotes(filters: any = {}): Observable<any> {
    let params = new HttpParams();
    if (filters.productId != null) params = params.set('productId', filters.productId);
    if (filters.minPrice != null) params = params.set('minPrice', filters.minPrice);
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 5);
    return this.http.get<any>(`${this.apiUrl}/quotes`, { params });
  }

  downloadPdf(id: number): Observable<Blob> {
  return this.http.get(`${this.apiUrl}/quotes/${id}/pdf`, {
    responseType: 'blob'
  });
  }

  /**
   * Handle HTTP errors
   *
   * @param error The error object from HttpClient
   * @returns Observable that throws a user-friendly error message
   *
   * TODO: Implement error handling
   * - Log error to console for debugging
   * - Extract error message from backend response or use default
   * - Return Observable error with appropriate message
   */
  private handleError(error: any): Observable<never> {
    console.error('Quote service error:', error);
    const message = error.error?.message || 'Failed to process quote';
    return throwError(() => new Error(message));
  }
}
