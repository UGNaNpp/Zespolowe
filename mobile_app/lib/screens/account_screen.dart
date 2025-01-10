import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'login_screen.dart';

class AccountScreen extends StatelessWidget {
  const AccountScreen({super.key});

  Future<void> _logout(BuildContext context) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.remove('tokenKey');
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (context) => LoginScreen()),
    );
  }

  Future<Map<String, String?>> _loadUserData() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('tokenKey');

    if (token != null) {
      try {
        final parts = token.split('.');
        if (parts.length != 3) return {};

        final payload = utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
        final Map<String, dynamic> payloadData = jsonDecode(payload);

        final sub = jsonDecode(payloadData['sub']);
        return {
          'username': sub['username'],
          'email': sub['email'],
        };
      } catch (e) {
        return {};
      }
    }

    return {};
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      appBar: AppBar(
        title: Text('Account'),
      ),
      body: FutureBuilder<Map<String, String?>>(
        future: _loadUserData(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(
              child: CircularProgressIndicator(),
            );
          } else if (snapshot.hasError) {
            return Center(
              child: Text('Error loading user data'),
            );
          } else {
            final userData = snapshot.data ?? {};
            final username = userData['username'] ?? 'Unknown';
            final email = userData['email'] ?? 'Unknown';

            return Center(
              child: Container(
                width: screenWidth * 0.8,
                height: screenHeight * 0.8,
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Text(
                      username,
                      style: TextStyle(
                        fontSize: screenWidth * 0.09,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF364AB8),
                      ),
                    ),
                    SizedBox(height: 8),
                    Text(
                      email,
                      style: TextStyle(fontSize: screenWidth * 0.05,),
                    ),
                    Spacer(),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: () {
                          _logout(context);
                        },
                        style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                            side: BorderSide(color: Colors.black, width: 1),
                          ),
                          backgroundColor: Color.fromARGB(128, 107, 119, 187),
                          foregroundColor: Colors.black,
                        ),
                        child: Text('Log Out'),
                      ),
                    ),
                  ],
                ),
              ),
            );
          }
        },
      ),
    );
  }
}
