import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";
import {AuthenticationService} from "../services/authentication.service";

@Injectable()
export class AdminSkipQueueInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // If the user has ADMIN privileges, communicate directly with the backend (skip gateway/queue)
    if (this.authenticationService.isAdmin()) {
      request = request.clone({
        url: `${request.url.replace(environment.GATEWAY_API_URL, environment.REST_API_URL)}`
      });
    }
    return next.handle(request);
  }
}
