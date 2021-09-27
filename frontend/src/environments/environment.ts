// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  REST_API_URL: 'http://localhost:8080/lsr/api',
  GATEWAY_API_URL: 'http://localhost:8080/gateway/api',
  ESTIMATED_WAITING_TIME_SECONDS_PER_USER: 15,
  MAX_TIME_SECONDS_FOR_RESERVATION: 120,
  QUEUE_SOCKET_URL: 'ws://localhost:8080/gateway/queue',
  CONCURRENT_USERS_SOCKET_URL: 'ws://localhost:8080/gateway/concurrent-users',
  ADMIN_NOTIFICATION_RSOCKET_URL: 'ws://localhost:7878'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
