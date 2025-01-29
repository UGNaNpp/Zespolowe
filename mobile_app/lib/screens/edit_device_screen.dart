import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../models/device.dart';

import '../api_constants.dart';
import 'main_screen.dart';

class EditDeviceScreen extends StatefulWidget {
  final Device device;


  const EditDeviceScreen({super.key, required this.device});

  @override
  _EditDeviceScreenState createState() => _EditDeviceScreenState();
}

class _EditDeviceScreenState extends State<EditDeviceScreen> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _IPController = TextEditingController();
  final TextEditingController _MACController = TextEditingController();
  final TextEditingController _HeightController = TextEditingController();
  final TextEditingController _WidthController = TextEditingController();
  bool _recordingMode = false;
  bool _recordingVideo = false;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();

    _nameController.text = widget.device.name;
    _IPController.text = widget.device.associatedIP;
    _MACController.text = widget.device.associatedMAC;
    _HeightController.text = widget.device.heightResolution.toString();
    _WidthController.text = widget.device.widthResolution.toString();
    _recordingMode = widget.device.recordingMode;
    _recordingVideo = widget.device.recordingVideo;
  }

  String validateBody(String name, String ip, String mac, String height, String width) {
    if (name.isEmpty) return "Device name cannot be empty";
    if (ip.isEmpty || !RegExp(r'^(25[0-5]|2[0-4]\d|1\d{2}|\d{1,2})(\.(25[0-5]|2[0-4]\d|1\d{2}|\d{1,2})){3}$').hasMatch(ip)) return "Invalid IP address";
    if (mac.isEmpty || !RegExp(r'^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$').hasMatch(mac)) return "Invalid MAC address";
    if (height.isEmpty || int.tryParse(height) == null || int.parse(height) <= 0) return "Invalid height resolution";
    if (width.isEmpty || int.tryParse(width) == null || int.parse(width) <= 0) return "Invalid width resolution";
    return "";
  }

  Future<void> _handleAddDevice() async {
    String name = _nameController.text;
    String ip = _IPController.text;
    String mac = _MACController.text;
    String heightResolution = _HeightController.text;
    String widthResolution = _WidthController.text;

    String error = validateBody(name, ip, mac, heightResolution, widthResolution);

    if (error != '') {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(error)),
      );

      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      SharedPreferences prefs = await SharedPreferences.getInstance();
      String? baseUrl = prefs.getString('api-ip');

      if (baseUrl == null || baseUrl.isEmpty) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => MainScreen()),
        );
        return;
      }

      final uri = Uri.http(baseUrl, "$editDeviceEndpoint${widget.device.id}");

      final response = await http.put(
        uri,
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'name': name,
          'AssociatedIP': ip,
          'AssociatedMAC': mac,
          'heightResolution': int.parse(heightResolution),
          'widthResolution': int.parse(widthResolution),
          'recordingMode': _recordingMode,
          'recordingVideo': _recordingVideo,
        }),
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Device has been edited")),
        );

        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => MainScreen()),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response.body)),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("An error occurred: $e")),
      );
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      appBar: AppBar(
        title: Text('Edit Device'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
          child: Container(
            width: screenWidth * 0.8,
            height: screenHeight * 0.8,
            child: SingleChildScrollView(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Text(
                        'Edit device',
                        style: TextStyle(
                          fontSize: screenWidth * 0.07,
                          fontWeight: FontWeight.bold,
                          color: Color(0xFF364AB8),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 40),

                  TextField(
                    controller: _nameController,
                    decoration: InputDecoration(
                      labelText: 'Name',
                      hintText: 'Enter device name',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                      ),
                      prefixIcon: Icon(Icons.camera_alt),
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                        borderRadius: BorderRadius.circular(14),
                      ),
                    ),
                  ),
                  SizedBox(height: 20),

                  TextField(
                    controller: _IPController,
                    decoration: InputDecoration(
                      labelText: 'IP',
                      hintText: 'Enter IP address',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                      ),
                      prefixIcon: Icon(Icons.settings_ethernet),
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                        borderRadius: BorderRadius.circular(14),
                      ),
                    ),
                  ),
                  SizedBox(height: 20),

                  TextField(
                    controller: _MACController,
                    decoration: InputDecoration(
                      labelText: 'MAC',
                      hintText: 'Enter MAC address',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                      ),
                      prefixIcon: Icon(Icons.perm_device_information),
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                        borderRadius: BorderRadius.circular(14),
                      ),
                    ),
                  ),
                  SizedBox(height: 20),

                  TextField(
                    controller: _HeightController,
                    decoration: InputDecoration(
                      labelText: 'Height Resolution',
                      hintText: 'Enter height resolution',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                      ),
                      prefixIcon: Icon(Icons.arrow_upward),
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                        borderRadius: BorderRadius.circular(14),
                      ),
                    ),
                  ),
                  SizedBox(height: 20),

                  TextField(
                    controller: _WidthController,
                    decoration: InputDecoration(
                      labelText: 'Width Resolution',
                      hintText: 'Enter width resolution',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                      ),
                      prefixIcon: Icon(Icons.arrow_forward),
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                        borderRadius: BorderRadius.circular(14),
                      ),
                    ),
                  ),
                  SizedBox(height: 20),

                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Recording Mode',
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500)),
                      Switch(
                        value: _recordingMode,
                        onChanged: (value) {
                          setState(() {
                            _recordingMode = value;
                          });
                        },
                        activeColor: Colors.white,
                        activeTrackColor: Color(0xFF364AB8),
                      ),
                    ],
                  ),
                  SizedBox(height: 20),

                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Recording Video',
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500)),
                      Switch(
                        value: _recordingVideo,
                        onChanged: (value) {
                          setState(() {
                            _recordingVideo = value;
                          });
                        },
                        activeColor: Colors.white,
                        activeTrackColor: Color(0xFF364AB8),
                      ),
                    ],
                  ),
                  SizedBox(height: 20),

                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      ElevatedButton(
                        onPressed: _isLoading ? null : _handleAddDevice,
                        style: ElevatedButton.styleFrom(
                          padding: EdgeInsets.symmetric(vertical: 12, horizontal: 40),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                            side: BorderSide(color: Colors.black, width: 1),
                          ),
                          backgroundColor: Color(0x80364AB8),
                          foregroundColor: Colors.black,
                        ),
                        child: _isLoading
                            ? CircularProgressIndicator(color: Colors.white)
                            : Text('Edit device'),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
