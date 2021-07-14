import { Component, OnInit } from '@angular/core';
import {AuthenticationService} from "../../services/authentication.service";
import {User} from "../../models/user.model";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  //loading = false;
  //users: User[] = [];
  //error = '';

  constructor(/*private s: AuthenticationService*/) { }

  ngOnInit(): void {
    // this.loading = true;
    // this.s.all().pipe(first()).subscribe(users => {
    //   this.loading = false;
    //   this.users = users;
    // }, error => {
    //   this.loading = false;
    //   this.error = error;
    // });
  }

}
