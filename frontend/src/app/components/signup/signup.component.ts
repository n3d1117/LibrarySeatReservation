import { Component, OnInit } from '@angular/core';
import {AuthenticationService} from "../../services/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {first} from "rxjs/operators";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    surname: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  returnUrl = '/';
  hide = true;
  loading = false;
  submitted = false;
  error = '';

  constructor(private route: ActivatedRoute,
              private router: Router,
              private authenticationService: AuthenticationService,
              private snackBar: MatSnackBar) {
    // redirect to home if already logged in
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  // convenience getter for easy access to form fields
  get f() { return this.form.controls; }

  onSignup(): void {
    this.submitted = true;

    if (this.form.invalid) {
      this.error = 'Form non valido'
      return;
    }

    this.loading = true;
    this.authenticationService.signup(
      this.f.name.value,
      this.f.surname.value,
      this.f.email.value,
      this.f.password.value,
      false
    )
      .pipe(first())
      .subscribe(user => {
        if (user) {
          this.snackBar.open('Utente registrato correttamente!', '', { duration: 3000 });
          this.router.navigate(['/login']);
        }
      }, error => {
        this.error = error;
        this.loading = false;
      });
  }

}
