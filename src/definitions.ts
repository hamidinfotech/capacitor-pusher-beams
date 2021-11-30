import type { PluginListenerHandle } from '@capacitor/core';

export interface TokenProviderHeader {
  apikey: string;
  appkey: string;
}

export interface PusherBeamsPlugin {
  start(options: { instanceId: string }): Promise<{ success: boolean }>;
  getDeviceId(options: { instanceId: string }): Promise<{ deviceId: string }>;
  addDeviceInterest(options: { interest: string }): Promise<{ message: string }>;
  removeDeviceInterest(options: { interest: string }): Promise<{ success: boolean }>;
  setDeviceInterests(options: { interests: string[] }): Promise<{ interests: string[] }>;
  getDeviceInterests(): Promise<{ interests: string[] }>;
  clearDeviceInterests(): Promise<{ success: boolean }>;
  setUserID(options: { beamsAuthURL: string, userID: string, headers: TokenProviderHeader }): Promise<{message: string } | string>;
  clearAllState(): Promise<{ success: boolean }>;
  stop(): Promise<{ success: boolean }>;
  addListener(
      eventName: 'pushNotificationActionPerformed',
      listenerFunc: (notification: ActionPerformed) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export interface ActionPerformed {
  /**
   * The action performed on the notification.
   *
   * @since 1.0.0
   */
  actionId: string;

  /**
   * Text entered on the notification action.
   *
   * Only available on iOS.
   *
   * @since 1.0.0
   */
  inputValue?: string;

  /**
   * The notification in which the action was performed.
   *
   * @since 1.0.0
   */
  notification: PushNotificationSchema;
}

export interface PushNotificationSchema {
  /**
   * The notification title.
   *
   * @since 1.0.0
   */
  title?: string;

  /**
   * The notification subtitle.
   *
   * @since 1.0.0
   */
  subtitle?: string;

  /**
   * The main text payload for the notification.
   *
   * @since 1.0.0
   */
  body?: string;

  /**
   * The notification identifier.
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The number to display for the app icon badge.
   *
   * @since 1.0.0
   */
  badge?: number;

  /**
   * It's not being returned.
   *
   * @deprecated will be removed in next major version.
   * @since 1.0.0
   */
  notification?: any;

  /**
   * Any additional data that was included in the
   * push notification payload.
   *
   * @since 1.0.0
   */
  data: any;

  /**
   * The action to be performed on the user opening the notification.
   *
   * Only available on Android.
   *
   * @since 1.0.0
   */
  click_action?: string;

  /**
   * Deep link from the notification.
   *
   * Only available on Android.
   *
   * @since 1.0.0
   */
  link?: string;

  /**
   * Set the group identifier for notification grouping.
   *
   * Only available on Android. Works like `threadIdentifier` on iOS.
   *
   * @since 1.0.0
   */
  group?: string;

  /**
   * Designate this notification as the summary for an associated `group`.
   *
   * Only available on Android.
   *
   * @since 1.0.0
   */
  groupSummary?: boolean;
}
