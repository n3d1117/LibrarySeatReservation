import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";
import {Reservation} from "../../models/reservation.model";
import {Library} from "../../models/library.model";
import {first} from "rxjs/operators";
import {ReservationService} from "../../services/reservation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {DateUtilityService} from "../../services/date-utility.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent, ConfirmDialogModel} from "../confirm-dialog/confirm-dialog.component";
import { MatRadioChange } from '@angular/material/radio';

@Component({
  selector: 'app-reservations-box',
  templateUrl: './reservations-box.component.html',
  styleUrls: ['./reservations-box.component.css']
})
export class ReservationsBoxComponent implements OnInit {

  reservationsSelectionForm!: FormGroup;
  isMorngingSelected = true;
  confirmButtonEnable: boolean = false;
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
    private dateService: DateUtilityService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.checkIfButtonEnable();
    this.reservationsSelectionForm = this.formBuilder.group({
      morning: false,
      afternoon: false
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    this.checkIfButtonEnable();
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

  /*checkboxValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    console.log("Calcolo...");
    const morning = control.get('morning');
    const afternoon = control.get('afternoon');
    const atLeastOneCheckboxSelected = ((morning?.value || afternoon?.value) && (morning?.value != afternoon?.value));
    const fullMorning = this.morningReservations().length >= this.library.capacity;
    const fullAfteroon = this.afternoonReservations().length >= this.library.capacity;
    const canEnable = ((this.isMorngingSelected && !fullMorning) || (!this.isMorngingSelected && !fullAfteroon));
    return {canEnable: canEnable};
  };*/

  checkIfButtonEnable() {
    console.log("Cambio radio!");
    const fullMorning = this.morningReservations().length >= this.library.capacity;
    const fullAfteroon = this.afternoonReservations().length >= this.library.capacity;
    this.confirmButtonEnable = ((this.isMorngingSelected && !fullMorning) || (!this.isMorngingSelected && !fullAfteroon));
  }

  createStringFromDate(morning: boolean): string {
    const date = this.selectedDate;
    return this.dateService.prepareDateForBackend(date, morning);
  }

  addReservation(): void {
    
    //check if user is logged in
    if (!this.authenticationService.currentUserValue) {
      this.snackBar.open('Devi essere autenticato per prenotarti.', '', {duration: 3000});
      this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
      return;
    }
    //show confirm dialog
    const dialogData = new ConfirmDialogModel("Conferma prenotazione", `Sei sicuro di voler prenotare per ${this.library.name} in data ${this.dateStringTitle()} (${this.isMorngingSelected ? "fascia 8.00 - 13.00" : "fascia 13:00 - 19.00"})?`);
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: dialogData
    });
    //if reservation confirmed
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.loading = true;
        this.reservationService.add(
          this.authenticationService.currentUserValue.id,
          this.library.id,
          this.createStringFromDate(this.isMorngingSelected)
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
    });
  }
}
