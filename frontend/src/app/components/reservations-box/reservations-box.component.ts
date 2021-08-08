import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";
import {Reservation} from "../../models/reservation.model";
import {Library} from "../../models/library.model";

@Component({
  selector: 'app-reservations-box',
  templateUrl: './reservations-box.component.html',
  styleUrls: ['./reservations-box.component.css']
})
export class ReservationsBoxComponent implements OnInit {

  reservationsSelectionForm: FormGroup;
  @Input() library!: Library;
  @Input() dayReservations!: Reservation[] | null;

  constructor(private formBuilder: FormBuilder) {
    this.reservationsSelectionForm = formBuilder.group({
      morning: false,
      afternoon: false
    }, { validators: this.checkboxValidator });
  }

  morningReservations(): Reservation[] {
    if (!this.dayReservations)
      return []
    return this.dayReservations.filter(r => this.stringToDate(r.datetime).getHours() == 8);
  }

  afternoonReservations(): Reservation[] {
    if (!this.dayReservations)
      return []
    return this.dayReservations.filter(r => this.stringToDate(r.datetime).getHours() == 13);
  }

  checkboxValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const morning = control.get('morning');
    const afternoon = control.get('afternoon');
    const atLeastOneCheckboxSelected = ((morning?.value || afternoon?.value) && (morning?.value != afternoon?.value));
    return { atLeastOneCheckboxSelected: atLeastOneCheckboxSelected };
  };

  ngOnInit(): void {
  }

  stringToDate(date: string): Date {
    return new Date(date.replace(' ', 'T'));
  }

}
