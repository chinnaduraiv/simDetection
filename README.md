# sim_detection

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/), a specialized package that includes
platform-specific implementation code for Android and/or iOS.

For help getting started with Flutter development, view the
[online documentation](https://flutter.dev/docs), which offers tutorials, samples, guidance on
mobile development, and a full API reference.

## To get the list of sim  
* var listOfActivateSim = await _simeDetectionPlugin.getActiveSim()

## To send SMS with arguments 

* await _simeDetectionPlugin.sendSMS(
    selectedSimName: customizedValues[index]
    ['displayName'], selectedSimIndex: customizedValues[index]
    ['simSlotIndex'], toMobileNumber: '+919585313659', description: 'encryptedText'); }

