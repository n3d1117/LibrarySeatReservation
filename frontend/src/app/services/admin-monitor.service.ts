import {environment} from './../../environments/environment';
import {Injectable} from '@angular/core';
import {RSocketClient} from "rsocket-core";
import RSocketWebsocketClient from "rsocket-websocket-client";
import {Payload, Responder} from 'rsocket-types';
import {Single, Flowable} from 'rsocket-flowable';

@Injectable({
  providedIn: 'root'
})
export class AdminMonitorService {

  // Callback used upon receiving a notification
  private onReceivedNotification: (message: string) => void;

  private rSocketUrl = environment.ADMIN_NOTIFICATION_RSOCKET_URL;
  private client: RSocketClient<string, string> | null = null;
  private setupMessage = "setup";

  constructor() {
    // Provide a default callback
    this.onReceivedNotification = (message) => console.log(message);
  }

  /**
   * Close the RSocket connection with the server
   */
  stopMonitoring(): void {
    this.client?.close();
  }

  /**
   * Start the RSocket connection and wait for messages
   */
  startMonitoring(onReceivedNotification: (message: string) => void): void {

    this.onReceivedNotification = onReceivedNotification;

    const transportOptions = {
      url: this.rSocketUrl,
      wsCreator: (url: string) => {
        return new WebSocket(url);
      }
    };
    const setup = {
      keepAlive: 60000, // ms
      lifetime: 180000, // ms
      dataMimeType: 'application/json',
      metadataMimeType: 'application/json'
    };

    const transport = new RSocketWebsocketClient(transportOptions);
    const responder = new RSocketResponder(this.onReceivedNotification, this.setupMessage);

    this.client = new RSocketClient({setup: setup, transport: transport, responder: responder});

    this.client.connect().subscribe({
      onComplete: (rsocket) => {
        // Upon successful connection, send a FNF setup message to the server
        rsocket.fireAndForget({data: this.setupMessage});
      }
    })
  }
}

/**
 * The RSocket responder class specifies how the client reacts upon receiving a notification
 */
class RSocketResponder implements Responder<string, string> {

  private readonly callback: (message: string) => void;
  private readonly setupMessage: string;

  constructor(callback: (message: string) => void, setupMessage: string) {
    this.callback = callback;
    this.setupMessage = setupMessage;
  }

  /**
   * Called when the client receives a Fire-And-Forget message from the server.
   * @param payload the message's payload
   */
  fireAndForget(payload: Payload<string, string>): void {
    // When invoking the callback, skip initial FNF setup message
    if (payload.data != null && payload.data != this.setupMessage) {
      this.callback(payload.data);
    }
  }

  // Unused
  metadataPush(payload: Payload<string, string>): Single<void> {
    return Single.error(new Error());
  }

  // Unused
  requestChannel(payloads: Flowable<Payload<string, string>>): Flowable<Payload<string, string>> {
    return Flowable.error(new Error());
  }

  // Unused
  requestResponse(payload: Payload<string, string>): Single<Payload<string, string>> {
    return Single.error(new Error());
  }

  // Unused
  requestStream(payload: Payload<string, string>): Flowable<Payload<string, string>> {
    return Flowable.error(new Error());
  }
}
