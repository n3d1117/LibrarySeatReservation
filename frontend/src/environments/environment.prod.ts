export const environment = {
  production: true,
  REST_API_URL: 'http://localhost:8080/lsr/api',
  GATEWAY_API_URL: 'http://localhost:8080/gateway/api',
  ESTIMATED_WAITING_TIME_FOR_USER: 15,
  MAX_TIME_FOR_RESERVATION: 120,
  QUEUE_SOCKET: 'ws://localhost:8080/gateway/queue',
  CONCURRENT_USERS_SOCKET: 'ws://localhost:8080/gateway/concurrent-users',
  ADMIN_NOTIFICATION_RSOCKET: 'ws://localhost:7878'
};
