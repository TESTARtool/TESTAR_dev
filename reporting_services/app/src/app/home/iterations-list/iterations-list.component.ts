import { Component, Input, OnInit } from '@angular/core';
import { Iteration } from 'src/app/models/Iteration';

@Component({
  selector: 'app-iterations-list',
  templateUrl: './iterations-list.component.html',
})
export class IterationsListComponent implements OnInit {

  @Input() iterations: Iteration[] = [];

  constructor() { }

  ngOnInit(): void {
  }

}
