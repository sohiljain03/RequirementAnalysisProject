import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RequirementsService } from '../../services/requirements.service';

@Component({
  selector: 'app-requirements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './requirements.component.html',
  styleUrls: ['./requirements.component.css']
})
export class RequirementsComponent {
  requirementsText = '';
  result: any;

  constructor(private reqService: RequirementsService) {}

  analyze() {
    this.reqService.analyzeRequirements(this.requirementsText)
      .subscribe(res => this.result = res);
  }
}
