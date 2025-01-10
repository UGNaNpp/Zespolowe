import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:mobile_app/screens/manage_device_screen.dart';
import 'package:mobile_app/screens/add_device_screen.dart';
import 'package:mobile_app/screens/login_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

import '../models/device.dart';
import '../api_constants.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool _isLoading = false;
  List<dynamic> _devices = [];
  String? _username;

  void _deleteToken() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.remove('tokenKey');
  }

  String? _extractUsernameFromToken(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) {
        return null;
      }

      final payload = utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
      final Map<String, dynamic> payloadData = jsonDecode(payload);

      final sub = jsonDecode(payloadData['sub']);
      return sub['username'];
    } catch (e) {
      return null;
    }
  }

  Future<void> _loadUsername() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('tokenKey');
    if (token != null) {
      setState(() {
        _username = _extractUsernameFromToken(token);
      });
    }
  }

  Future<void> _fetchDevices() async {
    setState(() {
      _isLoading = true;
    });

    try {
      final uri = Uri.http(baseUrl, devicesEndpoint);
      final response = await http.get(uri);

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = jsonDecode(response.body);
        setState(() {
          _devices = data.entries.toList();
        });
      } else {
        _deleteToken();
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => LoginScreen()),
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
  void initState() {
    super.initState();
    _loadUsername();
    _fetchDevices();
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
          child: Container(
            width: screenWidth * 0.8,
            height: screenHeight * 1,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                SizedBox(height: screenHeight * 0.1),

                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text(
                      'Hello ${_username ?? ""}',
                      style: TextStyle(
                        fontSize: screenWidth * 0.09,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF364AB8),
                      ),
                    ),
                  ],
                ),

                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text(
                      'Notifications: 0',
                      style: TextStyle(
                        fontSize: screenWidth * 0.05,
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 60),

                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text(
                      'Your devices:',
                      style: TextStyle(
                        fontSize: screenWidth * 0.06,
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 10),

                Expanded(
                  child: _isLoading
                      ? Center(child: CircularProgressIndicator())
                      : _devices.isEmpty
                          ? Center(
                              child: Text(
                                "No devices found",
                                style: TextStyle(fontSize: 16),
                              ),
                            )
                          : Container(
                              margin: const EdgeInsets.only(bottom: 20.0),
                              child: Scrollbar(
                                child: ListView.builder(
                                  itemCount: _devices.length,
                                  itemBuilder: (context, index) {
                                    final entry = _devices[index];
                                    final id = entry.key;
                                    final device = entry.value;
                                    
                                    return GestureDetector(
                                      onTap: () {
                                        final deviceObj = Device(
                                          id: id,
                                          name: device['name'],
                                          associatedIP: device['AssociatedIP'],
                                          associatedMAC: device['AssociatedMAC'],
                                          heightResolution: device['heightResolution'],
                                          widthResolution: device['widthResolution'],
                                          recordingMode: device['recordingMode'],
                                          recordingVideo: device['recordingVideo'],
                                        );

                                        Navigator.push(
                                          context,
                                          MaterialPageRoute(
                                            builder: (context) => ManageDeviceScreen(device: deviceObj),
                                          ),
                                        ).then((_) {
                                          _fetchDevices();
                                        });
                                      },
                                      child: ListTile(
                                        title: Text(
                                          device['name'] ?? "Unknown name",
                                          style: TextStyle(fontSize: 16),
                                        ),
                                        trailing: Padding(
                                          padding: const EdgeInsets.only(right: 8.0),
                                          child: Icon(Icons.arrow_forward_ios),
                                        ),
                                        contentPadding: EdgeInsets.zero,
                                      ),
                                    );
                                  },
                                ),
                              ),
                            ),
                ),

                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    IconButton(
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (context) => AddDeviceScreen()),
                        );
                      },
                      icon: Icon(
                        Icons.add,
                        color: Color(0xFF364AB8),
                        size: 40,
                      ),
                      iconSize: 30,
                      padding: EdgeInsets.all(12),
                      splashRadius: 24,
                      constraints: BoxConstraints(
                        minWidth: 60,
                        minHeight: 60,
                      ),
                    ),
                  ],
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
