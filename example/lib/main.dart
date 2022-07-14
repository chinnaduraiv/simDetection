import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:sim_detection/sim_detection.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<dynamic> _platformVersion = [];
  final _simeDetectionPlugin = SimDetection();
  final List<Map<String, String>> customizedValues = [];

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    List<dynamic> platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _simeDetectionPlugin.getActiveSim();
    } on PlatformException {
      platformVersion = [];
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
      final List<dynamic> listValue = List<dynamic>.from(platformVersion);
      if (listValue.isNotEmpty) {
        for (var element in listValue) {
          debugPrint('element--> ${element}');
          customizedValues.add(Map<String, String>.from(element));
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              if (customizedValues.isNotEmpty)
                Expanded(
                  child: ListView.builder(
                      itemCount: customizedValues.length,
                      itemBuilder: (BuildContext context, int index) {
                        return GestureDetector(
                          onTap: () async {
                            await _simeDetectionPlugin.sendSMS(
                                selectedSimName: customizedValues[index]
                                    ['displayName'],
                                selectedSimIndex: customizedValues[index]
                                    ['simSlotIndex'],
                                toMobileNumber: '+919585313659',
                                description: 'encryptedText');
                          },
                          child: Container(
                            padding: EdgeInsets.only(
                                left: 15.0, right: 15.0, top: 20),
                            child: Card(
                              child: ListTile(
                                  title: Text(customizedValues[index]
                                          ['displayName']
                                      .toString())),
                            ),
                          ),
                        );
                      }),
                )
            ],
          ),
        ),
      ),
    );
  }
}
