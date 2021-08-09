import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:plugin_camera/plugin_camera.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _getStringBarcode = 'Unknown';

  Future<void> _getResultBarcode() async {
    String platformVersion;
    try {
      platformVersion =
          await PluginCamera.getResultBarcode ?? 'Unknown Barcode';
    } on PlatformException {
      platformVersion = 'Failed to get result barcode.';
    }

    if (!mounted) return;

    setState(() {
      _getStringBarcode = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    final String viewType = 'plugin_camera_view';
    final Map<String, dynamic> creationParams = <String, dynamic>{};
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Expanded(
              flex: 2,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: SingleChildScrollView(
                  child: Text(
                    'Result: $_getStringBarcode',
                  ),
                ),
              ),
            ),
            Expanded(
              flex: 1,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  IconButton(
                    onPressed: () =>
                        PluginCamera.pauseCamera.then((value) => print(value)),
                    icon: const Icon(Icons.pause),
                  ),
                  ElevatedButton(
                    onPressed: () async {
                      PluginCamera.platformVersion
                          .then((value) => print(value));
                      await _getResultBarcode();
                    },
                    child: const Text('Start Scan'),
                  ),
                  IconButton(
                    onPressed: () =>
                        PluginCamera.resumeCamera.then((value) => print(value)),
                    icon: const Icon(Icons.play_arrow),
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 7,
              child: AndroidView(
                viewType: viewType,
                // layoutDirection: TextDirection.ltr,
                creationParams: creationParams,
                creationParamsCodec: const StandardMessageCodec(),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
