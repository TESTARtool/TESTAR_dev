import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Action } from '../models/Action';
import { ActionsDto } from '../models/ActionsDto';
import { Iteration } from '../models/Iteration';
import { IterationsDto } from '../models/IterationsDto';
import { Report } from '../models/Report';
import { ReportsDto } from '../models/ReportsDto';
import { SequenceItem } from '../models/SequenceItem';
import { SequenceItemsDto } from '../models/SequenceItemsDto';

@Injectable({
  providedIn: 'root'
})
export class TestarService {

  baseUrl = '/api';

  constructor(
    private http: HttpClient
  ) { }

  getReports(): Observable<Report[]> {
    return this.http
    .get<ReportsDto>(`${this.baseUrl}/reports`)
    .pipe(
      map((data: ReportsDto) => data._embedded.reports)
    );
  }

  getAllIterationsInReport(reportId: number): Observable<Iteration[]> {
    return this.http
    .get<IterationsDto>(`${this.baseUrl}/iterations?reportIds=${reportId}`)
    .pipe(
      map((data: IterationsDto) => data._embedded.iterations)
    );
  }

  getIterationInReport(reportId: number, iterationId: number): Observable<Iteration> {
    return this.http
    .get<IterationsDto>(`${this.baseUrl}/iterations?reportIds=${reportId}&ids=${iterationId}`)
    .pipe(
      map((data: IterationsDto) => data._embedded.iterations[0])
    );
  }

  getSelectedActionsInIteration(iterationId: number): Observable<Action[]> {
    return this.http
    .get<ActionsDto>(`${this.baseUrl}/actions?iterationIds=${iterationId}&selected=true`)
    .pipe(
      map((data: ActionsDto) => data._embedded.actions)
    );
  }

  getState(sequenceItemId: number): Observable<SequenceItem> {
    return this.http
    .get<SequenceItemsDto>(`${this.baseUrl}/states?ids=${sequenceItemId}`)
    .pipe(
      map((data: SequenceItemsDto) => data._embedded.sequenceItems[0])
    );
  }

}
