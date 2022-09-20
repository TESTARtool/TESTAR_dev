import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TestarService } from 'src/app/api/testar.service';
import { firstValueFrom } from 'rxjs';
import { SequenceItem } from 'src/app/models/SequenceItem';

@Component({
  selector: 'app-state',
  templateUrl: './state.component.html',
})
export class StateComponent implements OnInit {
  sequenceItemId = 0;

  sequenceItem: SequenceItem | null = null;

  constructor(
    private route: ActivatedRoute,
    private testar: TestarService,
  ) { }

  async ngOnInit(): Promise<void> {
    window.scrollTo(0, 0);

    this.route.params.subscribe(params => {
      this.sequenceItemId = params['sequenceItemId'];
    });

    await this.getSequenceItem();
  }

  async getSequenceItem(): Promise<void> {
    const data = await firstValueFrom (this.testar.getState(this.sequenceItemId));
    this.sequenceItem = data;
  }

}
