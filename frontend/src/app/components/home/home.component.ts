import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/authentication.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent{

  constructor(
    private router: Router,
    public authenticationService: AuthenticationService,
  ) { }

  goToAddLibrary(): void {
    this.router.navigate(['admin/add-library']);
  }

}
