import { Component, Input, OnInit } from '@angular/core';
import { Report } from 'src/app/models/Report';

@Component({
  selector: 'app-report-info',
  templateUrl: './report-info.component.html',
})
export class ReportInfoComponent implements OnInit {

  @Input() report: Report | null = null;

  constructor() { }

  ngOnInit(): void {
  }

}
