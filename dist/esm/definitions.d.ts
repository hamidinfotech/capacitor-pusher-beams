export interface TokenProviderHeader {
    apikey: string;
    appkey: string;
}
export interface PusherBeamsPlugin {
    start(options: {
        instanceId: string;
    }): Promise<{
        success: boolean;
    }>;
    getDeviceId(): Promise<{
        deviceId: string;
    }>;
    addDeviceInterest(options: {
        interest: string;
    }): Promise<{
        message: string;
    }>;
    removeDeviceInterest(options: {
        interest: string;
    }): Promise<{
        success: boolean;
    }>;
    setDeviceInterests(options: {
        interests: string[];
    }): Promise<{
        interests: string[];
    }>;
    getDeviceInterests(): Promise<{
        interests: string[];
    }>;
    clearDeviceInterests(): Promise<{
        success: boolean;
    }>;
    setUserID(options: {
        beamsAuthURL: string;
        userID: string;
        headers: TokenProviderHeader;
    }): Promise<{
        message: string;
    } | string>;
    clearAllState(): Promise<{
        success: boolean;
    }>;
    stop(): Promise<{
        success: boolean;
    }>;
}
