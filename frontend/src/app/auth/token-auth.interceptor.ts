import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {AuthenticationService} from "../services/authentication.service";
import {Observable} from "rxjs";

@Injectable()
export class TokenAuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add authorization header with JWT token the HTTP request, if available
    const currentUser = this.authenticationService.currentUserValue;
    if (currentUser) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser.jwt}`
        }
      });
    }
    return next.handle(request);
  }
}
