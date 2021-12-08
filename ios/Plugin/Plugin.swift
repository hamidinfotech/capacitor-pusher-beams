import Foundation
import Capacitor
import PushNotifications

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(PusherBeams)
public class PusherBeams: CAPPlugin {

    let beamUrl = "test.com"
    let beamsClient = PushNotifications.shared
    
    @objc public func deviceId(instanceId: String) -> String? {
        let userDefaults = UserDefaults(suiteName: "PushNotifications." + instanceId)
        return userDefaults?.string(forKey: "com.pusher.sdk.deviceId")
    }
    
    @objc public func emitDeviceId(instanceId: String) {
        let deviceId = self.deviceId(instanceId: instanceId)
        
        self.notifyListeners("pusherDeviceId", data: ["deviceId":deviceId])
    }
    
    @objc func start(_ call: CAPPluginCall) {
        call.success()
    }
    
    @objc func getDeviceId(_ call: CAPPluginCall) {
        let instanceId = call.getString("instanceId") ?? ""
        let deviceId = self.deviceId(instanceId: instanceId)
        
        guard let deviceId = deviceId else {
            call.reject("There is no device ID!")
            return
        }
        
        call.success([
            "deviceId": deviceId
        ])
    }
    
    @objc func addDeviceInterest(_ call: CAPPluginCall) {
        let interest = call.getString("interest") ?? ""
        
        try? self.beamsClient.addDeviceInterest(interest: interest)
        call.success([
            "interest": interest
        ])
    }
    
    @objc func removeDeviceInterest(_ call: CAPPluginCall) {
        let interest = call.getString("interest") ?? ""
        try? self.beamsClient.removeDeviceInterest(interest: interest)
        call.success()
    }
    
    @objc func getDeviceInterests(_ call: CAPPluginCall) {
        let interests: [String] = self.beamsClient.getDeviceInterests() ?? []
        call.success([
            "interests": interests
        ])
    }
    
    @objc func setDeviceInterests(_ call: CAPPluginCall) {
        print(call.options["interests"])
        guard let interests = call.options["interests"] as? [String] else {
            call.reject("Interests must be provided in array type")
            return
        }
        
        try? self.beamsClient.setDeviceInterests(interests: interests)
        call.success([
            "interests": interests,
            "success": true
        ])
    }
    
    @objc func clearDeviceInterests(_ call: CAPPluginCall) {
        try? self.beamsClient.clearDeviceInterests()
        print("Cleared device interests!")
        call.success()
    }
    
    func correctAges(headers:[String:Any])->[String:String] {
        var result:[String:String] = [:]
        for (key, val) in headers {
            result[key] = String(describing: val)
        }
        return result
    }
    
    @objc func setUserID(_ call: CAPPluginCall) {
        let beamsAuthURl = call.getString("beamsAuthURL") ?? "";
        let userId = call.getString("userID") ?? "";
        let headersParams = call.getObject("headers") ?? [:];
        print(headersParams);
        
        let tokenProvider = BeamsTokenProvider(authURL: beamsAuthURl) { () -> AuthData in
            let headers: [String: String] = self.correctAges(headers: headersParams);
            let queryParams: [String: String] = [:] // URL query params your auth endpoint needs
            return AuthData(headers: headers, queryParams: queryParams)
        }

        self.beamsClient.setUserId(userId, tokenProvider: tokenProvider, completion: { error in
            guard error == nil else {
                print(error.debugDescription)
                return
            }

            print("Successfully authenticated with Pusher Beams")
            call.success([
                "associatedUser": userId
            ])
        })
    }
    
    @objc func clearAllState(_ call: CAPPluginCall) {
        self.beamsClient.clearAllState {
            print("state cleared")
        }
        call.success()
    }

    @objc func stop(_ call: CAPPluginCall) {
        self.beamsClient.stop{
            print("Stopped!")
        }
        call.success()
    }
}
