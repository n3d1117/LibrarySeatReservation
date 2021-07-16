import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {first} from "rxjs/operators";
import {LibraryService} from "../../services/library.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";

@Component({
  selector: 'app-add-library',
  templateUrl: './add-library.component.html',
  styleUrls: ['./add-library.component.css']
})
export class AddLibraryComponent implements OnInit {

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required]),
    capacity: new FormControl('', [Validators.required, Validators.min(10), Validators.max(200)])
  });

  loading = false;
  submitted = false;
  error = '';

  constructor(private router: Router,
              private libraryService: LibraryService,
              private snackBar: MatSnackBar) {
  }

  ngOnInit(): void { }

  // convenience getter for easy access to form fields
  get f() { return this.form.controls; }

  addLibrary(): void {
    this.submitted = true;

    if (this.form.invalid) {
      this.error = 'Form non valido'
      return;
    }

    this.loading = true;
    this.libraryService.add(this.f.name.value, this.f.address.value, this.f.capacity.value)
      .pipe(first())
      .subscribe(library => {
        if (library) {
          this.snackBar.open('Biblioteca "' + library.name + '" aggiunta correttamente!', '', { duration: 3000 });
          this.router.navigate(['/home']);
        }
      }, error => {
        this.error = error;
        this.loading = false;
      });
  }

}
