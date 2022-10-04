import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-content-container',
  templateUrl: './content-container.component.html',
})
export class ContentContainerComponent implements OnInit {
  @Input() label = '';

  @Input() critical = false;

  constructor() { }

  ngOnInit(): void {
  }

}
