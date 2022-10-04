import { Component, OnInit } from '@angular/core';
import { TestarService } from '../api/testar.service';
import { firstValueFrom } from 'rxjs';
import { Report } from '../models/Report';
import { Iteration } from '../models/Iteration';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {

  report: Report | null = null;

  iterations: Iteration[] = [];

  constructor(
    private testar: TestarService,
  ) { }

  async ngOnInit(): Promise<void> {
    await this.getReport();
    await this.getAllIterationsInReport();
  }

  async getReport(): Promise<void> {
    const data = await firstValueFrom (this.testar.getReports());
    this.report = data[0];
  }

  async getAllIterationsInReport(): Promise<void> {
    if (this.report) {
      const data = await firstValueFrom (this.testar.getAllIterationsInReport(this.report.id));
      this.iterations = data;
    }
  }

}
