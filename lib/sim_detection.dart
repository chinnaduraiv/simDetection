import 'sim_detection_platform_interface.dart';

class SimDetection {
  //To get and return the list of active sim in this mobile
  Future<List<dynamic>> getActiveSim() {
    return SimDetectionPlatform.instance.getActiveSim();
  }

  //SMS will send to selected sim or First index sim if single sim
  Future<String> sendSMS({
    String? selectedSimIndex,
    String? selectedSimName,
    String? toMobileNumber,
    String? description,
  }) {
    return SimDetectionPlatform.instance.sendSMS(
        selectedSimIndex: selectedSimIndex,
        selectedSimName: selectedSimName,
        description: description,
        toMobileNumber: toMobileNumber);
  }
}
