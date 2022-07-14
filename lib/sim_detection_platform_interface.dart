import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'sim_detection_method_channel.dart';

abstract class SimDetectionPlatform extends PlatformInterface {
  /// Constructs a SimDetectionPlatform.
  SimDetectionPlatform() : super(token: _token);

  static final Object _token = Object();

  static SimDetectionPlatform _instance = MethodChannelSimDetection();

  /// The default instance of [SimDetectionPlatform] to use.
  ///
  /// Defaults to [MethodChannelSimDetection].
  static SimDetectionPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SimDetectionPlatform] when
  /// they register themselves.
  static set instance(SimDetectionPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<List<dynamic>> getActiveSim() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String> sendSMS({
    String? selectedSimIndex,
    String? selectedSimName,
    String? toMobileNumber,
    String? description,
  }) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
