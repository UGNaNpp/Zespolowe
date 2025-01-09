import 'package:flutter/material.dart';
import '../models/device.dart';

class ManageDeviceScreen extends StatelessWidget {
  final Device device;

  const ManageDeviceScreen({super.key, required this.device});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Add Device'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Text('Associated IP: ${device.associatedIP}', style: TextStyle(fontSize: 18)),
            Text('Associated MAC: ${device.associatedMAC}', style: TextStyle(fontSize: 18)),
            Text('Height Resolution: ${device.heightResolution}', style: TextStyle(fontSize: 18)),
            Text('Width Resolution: ${device.widthResolution}', style: TextStyle(fontSize: 18)),
            Text('Recording Mode: ${device.recordingMode ? 'Enabled' : 'Disabled'}', style: TextStyle(fontSize: 18)),
            Text('Recording Video: ${device.recordingVideo ? 'Enabled' : 'Disabled'}', style: TextStyle(fontSize: 18)),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
              },
              child: Text('Go Back'),
            ),
          ],
        ),
      ),
    );
  }
}
