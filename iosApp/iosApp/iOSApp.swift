import SwiftUI
import FirebaseCore
import FirebaseCrashlytics

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
