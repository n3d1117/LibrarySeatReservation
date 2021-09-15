import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {first} from 'rxjs/internal/operators/first';
import {Library} from 'src/app/models/library.model';
import {LibraryService} from 'src/app/services/library.service';
import {AuthenticationService} from "../../services/authentication.service";
import {AdminMonitorService} from "../../services/admin-monitor.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  searchBarValueHome = '';
  loading = false;
  libraries: Library[] = [];

  ngOnDestroy(): void {
    this.adminMonitorService.stopMonitoring();
  }

  ngOnInit(): void {
    this.loading = true;
    this.libraryService.all().pipe(first()).subscribe(libraries => {
      this.loading = false;
      this.libraries = libraries;

      if (this.authenticationService.isAdmin()) {
        this.adminMonitorService.startMonitoring((receivedMessage => {
          const json = JSON.parse(receivedMessage);
          this.snackBar.open(receivedMessage, '', {duration: 3000});
        }));
      }


    }, error => {
      console.log(error);
      this.loading = false;
    });
  }

  constructor(
    private libraryService: LibraryService,
    private router: Router,
    public authenticationService: AuthenticationService,
    private adminMonitorService: AdminMonitorService,
    private snackBar: MatSnackBar
  ) {
  }

  filterLibraries(): Library[] {
    return this.libraries.filter(library => {
      return library.name.toLowerCase().includes(this.searchBarValueHome.toLowerCase())
        || library.address.toLowerCase().includes(this.searchBarValueHome.toLowerCase());
    })
  }

  goToAddLibrary(): void {
    this.router.navigate(['admin/add-library']);
  }

}
