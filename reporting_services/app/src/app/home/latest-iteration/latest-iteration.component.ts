import { Component, Input, OnInit } from '@angular/core';
import { Iteration } from 'src/app/models/Iteration';

@Component({
  selector: 'app-latest-iteration',
  templateUrl: './latest-iteration.component.html',
})
export class LatestIterationComponent implements OnInit {

  @Input() iteration: Iteration | null = null;

  constructor() { }

  ngOnInit(): void {
  }

}
