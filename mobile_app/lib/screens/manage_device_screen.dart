import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../models/device.dart';
import 'streaming_screen.dart';
import 'edit_device_screen.dart';

import '../api_constants.dart';

class ManageDeviceScreen extends StatelessWidget {
  final Device device;

  const ManageDeviceScreen({super.key, required this.device});

  Future<void> _deleteDevice(BuildContext context) async {
    final url = Uri.http(baseUrl, "${deleteDeviceEndpoint}${device.id}");

    try {
      final response = await http.delete(url);

      // zmienic
      if (response.statusCode == 204) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Device deleted successfully')),
        );
        Navigator.pop(context);
      } else {
        print(url);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to delete the device.')),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('An error occurred: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      appBar: AppBar(
        title: Text('Manage device'),
      ),
      body: Center(
        child: Container(
          width: screenWidth * 0.8,
          height: screenHeight * 0.8,
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Text(
                device.name,
                style: TextStyle(
                  fontSize: 24,
                  color: Color(0xFF364AB8),
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text('Associated IP: ${device.associatedIP}', style: TextStyle(fontSize: 18)),
              Text('Associated MAC: ${device.associatedMAC}', style: TextStyle(fontSize: 18)),
              Text('Height Resolution: ${device.heightResolution}', style: TextStyle(fontSize: 18)),
              Text('Width Resolution: ${device.widthResolution}', style: TextStyle(fontSize: 18)),
              Text(
                'Recording Mode: ${device.recordingMode ? 'Enabled' : 'Disabled'}',
                style: TextStyle(fontSize: 18),
              ),
              Text(
                'Recording Video: ${device.recordingVideo ? 'Enabled' : 'Disabled'}',
                style: TextStyle(fontSize: 18),
              ),
              SizedBox(height: 20),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => StreamingScreen()),
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: BorderSide(color: Colors.black, width: 1),
                    ),
                    backgroundColor: Color.fromARGB(128, 107, 119, 187),
                    foregroundColor: Colors.black,
                  ),
                  child: Text('Watch live'),
                ),
              ),
              SizedBox(height: 10),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => EditDeviceScreen(device: device)),
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: BorderSide(color: Colors.black, width: 1),
                    ),
                    backgroundColor: Colors.white,
                    foregroundColor: Colors.black,
                  ),
                  child: Text('Edit device'),
                ),
              ),
              Spacer(),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    showDialog(
                      context: context,
                      builder: (BuildContext context) {
                        return AlertDialog(
                          title: Text('Delete Device'),
                          content: Text('Are you sure you want to delete this device?'),
                          actions: <Widget>[
                            TextButton(
                              onPressed: () {
                                Navigator.pop(context);
                              },
                              child: Text('Cancel', style: TextStyle (color: Color(0xFF364AB8))),
                            ),
                            TextButton(
                              onPressed: () {
                                Navigator.pop(context);
                                _deleteDevice(context);
                              },
                              child: Text('Delete', style: TextStyle (color: Color(0xFF364AB8))),
                            ),
                          ],
                        );
                      },
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: BorderSide(color: Colors.black, width: 1),
                    ),
                    backgroundColor: Color(0x30000000),
                    foregroundColor: Colors.black,
                  ),
                  child: Text('Delete device'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
