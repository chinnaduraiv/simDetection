import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'sim_detection_platform_interface.dart';

/// An implementation of [SimDetectionPlatform] that uses method channels.
class MethodChannelSimDetection extends SimDetectionPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('sime_detection_plugin');

  @override
  Future<List<dynamic>> getActiveSim() async {
    final version = await methodChannel
        .invokeMethod<List<dynamic>>('activeSubscriptionInfoList');
    return version ?? [];
  }

  @override
  Future<String> sendSMS({
    String? selectedSimIndex,
    String? selectedSimName,
    String? toMobileNumber,
    String? description,
  }) async {
    final result = await methodChannel.invokeMethod<String>('SMS', {
      'selectedSimSlotNumber': selectedSimIndex,
      'selectedSimSlotName': selectedSimName,
      'toMobileNumber': toMobileNumber,
      'descriptionText': description,
    });
    return result!;
  }
}
