import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";
import {Reservation} from "../../models/reservation.model";
import {Library} from "../../models/library.model";
import {first} from "rxjs/operators";
import {ReservationService} from "../../services/reservation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {DateUtilityService} from "../../services/date-utility.service";

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
    private snackBar: MatSnackBar,
    private dateService: DateUtilityService
  ) { }

  ngOnInit(): void {
    this.reservationsSelectionForm = this.formBuilder.group({
      morning: false,
      afternoon: false
    }, { validators: this.checkboxValidator });
  }

  dateStringTitle(): string {
    return this.dateService.dateToHumanReadableString(this.selectedDate)
  }

  morningReservations(): Reservation[] {
    if (!this.dayReservations)
      return []
    return this.dayReservations.filter(r => this.dateService.stringToDate(r.datetime).getHours() == 8);
  }

  afternoonReservations(): Reservation[] {
    if (!this.dayReservations)
      return []
    return this.dayReservations.filter(r => this.dateService.stringToDate(r.datetime).getHours() == 13);
  }

  checkboxValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const morning = control.get('morning');
    const afternoon = control.get('afternoon');
    const atLeastOneCheckboxSelected = ((morning?.value || afternoon?.value) && (morning?.value != afternoon?.value));
    const fullMorning = this.morningReservations().length >= this.library.capacity;
    const fullAfteroon = this.afternoonReservations().length >= this.library.capacity;
    const canEnable = atLeastOneCheckboxSelected && ((morning?.value && !fullMorning) || (afternoon?.value && !fullAfteroon));
    return { canEnable: canEnable };
  };

  createStringFromDate(morning: boolean): string {
    const date = this.selectedDate;
    return this.dateService.prepareDateForBackend(date, morning);
  }

  addReservation(): void {
    if (!this.authenticationService.currentUserValue) {
      this.snackBar.open('Devi essere autenticato per prenotarti.', '', {duration: 3000});
      this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
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
        this.loading = false;
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