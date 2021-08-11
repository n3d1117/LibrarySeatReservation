import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";
import {Reservation} from "../../models/reservation.model";
import {Library} from "../../models/library.model";
import {first} from "rxjs/operators";
import {ReservationService} from "../../services/reservation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";

@Component({
  selector: 'app-reservations-box',
  templateUrl: './reservations-box.component.html',
  styleUrls: ['./reservations-box.component.css']
})
export class ReservationsBoxComponent implements OnInit {

  reservationsSelectionForm!: FormGroup;
  @Input() library!: Library;
  @Input() selectedDate!: Date;
  @Input() dayReservations!: Reservation[];

  loading = false;
  error = '';

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private authenticationService: AuthenticationService,
    private reservationService: ReservationService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.reservationsSelectionForm = this.formBuilder.group({
      morning: false,
      afternoon: false
    }, { validators: this.checkboxValidator });
  }

  dateStringTitle(): string {
    return this.selectedDate.toLocaleDateString('it',
      {weekday: "long", month: "long", day: "numeric"}
    )
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.substring(1))
      .join(' ');
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
    const fullMorning = this.morningReservations().length >= this.library.capacity;
    const fullAfteroon = this.afternoonReservations().length >= this.library.capacity;
    const canEnable = atLeastOneCheckboxSelected && ((morning?.value && !fullMorning) || (afternoon?.value && !fullAfteroon))
    return { canEnable: canEnable };
  };

  stringToDate(date: string): Date {
    return new Date(date.replace(' ', 'T'));
  }

  createStringFromDate(morning: boolean): string {
    //yyyy-MM-dd HH:mm:ss
    const date = this.selectedDate;
    date.setHours(morning ? 8 : 13);
    return new Date(
      date.getTime() - (date.getTimezoneOffset() * 60000 )
    ).toISOString().replace('T', ' ').split('.')[0];
  }

  addReservation(): void {

    if (!this.authenticationService.currentUserValue) {
      this.snackBar.open('Devi essere autenticato per prenotarti.', '', {duration: 3000});
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.loading = true;
    this.reservationService.add(
      this.authenticationService.currentUserValue.id,
      this.library.id,
      this.createStringFromDate(this.reservationsSelectionForm.controls.morning.value)
    )
      .pipe(first())
      .subscribe(reservation => {
        if (reservation) {
          this.snackBar.open('Prenotazione per "' + this.library.name + '" in data ' + this.dateStringTitle() + ' effettuata correttamente!', '', {duration: 5000});
          this.router.navigate(['/my-reservations']);
        }
      }, error => {
        this.error = error;
        this.loading = false;
      });
  }

}
