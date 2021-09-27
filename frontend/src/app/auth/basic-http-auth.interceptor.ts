import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {AuthenticationService} from "../services/authentication.service";
import {Observable} from "rxjs";

@Injectable()
export class BasicAuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add authorization header with basic auth credentials to the HTTP request, if available
    const currentUser = this.authenticationService.currentUserValue;
    if (currentUser && currentUser.authData) {
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${currentUser.authData}`
        }
      });
    }
    return next.handle(request);
  }
}
