import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Product } from '../models/product.model';

/**
 * Service for managing products (insurance products)
 *
 * TODO: Candidate must implement the following method:
 * - getProducts(): Observable<Product[]>
 *
 * Requirements:
 * - Use HttpClient for HTTP requests
 * - Use catchError operator to handle errors
 * - Base URL should be configurable via environment.apiUrl
 */
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = environment.apiUrl;
  private readonly endpoint = '/products';

  constructor(private http: HttpClient) {}

  /**
   * Get all available products
   * GET /api/products
   *
   * @returns Observable of array of products
   *
   * TODO: Implement this method
   * - GET from ${this.apiUrl}${this.endpoint}
   * - Handle errors with catchError
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}${this.endpoint}`).pipe(
      catchError ((error) => this.handleError(error))
    );
  }

  /**
   * Handle HTTP errors
   *
   * @param error The error object from HttpClient
   * @returns Observable that throws a user-friendly error message
   *
   * TODO: Implement error handling if needed
   */
  private handleError(error: any): Observable<never> {
    console.error('Quote service error:', error);
    const message = error.error?.message || 'Failed to fetch products';
    return throwError(() => new Error(message));
  }
}
