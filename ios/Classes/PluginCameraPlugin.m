#import "PluginCameraPlugin.h"
#if __has_include(<plugin_camera/plugin_camera-Swift.h>)
#import <plugin_camera/plugin_camera-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "plugin_camera-Swift.h"
#endif

@implementation PluginCameraPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPluginCameraPlugin registerWithRegistrar:registrar];
}
@end
