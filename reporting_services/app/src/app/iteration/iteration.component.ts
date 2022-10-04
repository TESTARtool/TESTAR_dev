import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TestarService } from '../api/testar.service';
import { firstValueFrom } from 'rxjs';
import { Iteration } from '../models/Iteration';
import { Action } from '../models/Action';

@Component({
  selector: 'app-iteration',
  templateUrl: './iteration.component.html',
})
export class IterationComponent implements OnInit {

  iterationId = 0;

  iteration: Iteration | null = null;

  selectedActions: Action[] = [];

  constructor(
    private route: ActivatedRoute,
    private testar: TestarService,
  ) { }

  async ngOnInit(): Promise<void> {
    window.scrollTo(0, 0);

    this.route.params.subscribe(params => {
      this.iterationId = params['iterationId'];
    });

    await this.getIteration();
    await this.getSelectedActions();
  }

  async getIteration(): Promise<void> {
    const data = await firstValueFrom (this.testar.getIterationInReport(1, this.iterationId));
    this.iteration = data;
  }

  async getSelectedActions(): Promise<void> {
    const data = await firstValueFrom (this.testar.getSelectedActionsInIteration(this.iterationId));
    this.selectedActions = data;
  }

}
