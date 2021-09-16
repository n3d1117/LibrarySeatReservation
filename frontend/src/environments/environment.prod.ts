export const environment = {
  production: true,
  REST_API_URL: 'http://localhost:8080/lsr/api',
  GATEWAY_API_URL: 'http://localhost:8080/gateway/api',
  ESTIMATED_WAITING_TIME_SECONDS_PER_USER: 15,
  MAX_TIME_SECONDS_FOR_RESERVATION: 120,
  QUEUE_SOCKET_URL: 'ws://localhost:8080/gateway/queue',
  CONCURRENT_USERS_SOCKET_URL: 'ws://localhost:8080/gateway/concurrent-users',
  ADMIN_NOTIFICATION_RSOCKET_URL: 'ws://localhost:7878'
};
