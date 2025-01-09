import 'package:flutter/material.dart';
import 'package:mobile_app/screens/login_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'api_constants.dart';
import 'main_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  _RegisterScreenState createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final TextEditingController _userController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _repeatPasswordController = TextEditingController();

  bool _isLoading = false;

  bool isValidEmail(String email) {
  final regex = RegExp(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');
  return regex.hasMatch(email);
}

  String validateUser(String username, String email, String password, String repeatPassword) {
      if (username.isEmpty) {
        return "Username is empty";
      }

      if (username.length < 4) {
        return "Username is too short (min 4 characters)";
      }

      if (!isValidEmail(email)) {
        return "Email not valid";
      }

      if (password.length < 4) {
        return "Username is too short (min 4 characters)";
      }

      if (repeatPassword.isEmpty) {
        return "Repeat password";
      }

      if (repeatPassword != password) {
        return "Passwords don't match";
      }

      return "";
  }

    void _saveToken(String token) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('tokenKey', token);
  }

  Future<void> _handleRegister() async {
  String username = _userController.text;
  String email = _emailController.text;
  String password = _passwordController.text;
  String repeatPassword = _repeatPasswordController.text;

  String error = validateUser(username, email, password, repeatPassword);

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
    final uri = Uri.http(
      '192.168.0.251:8080',
      '/api/auth/login',
      {
        'identifier': username,
        'password': password,
      },
    );

    final response = await http.post(uri);

    if (response.statusCode == 200) {
      print('Response body: ${response.body}');

      _saveToken(response.body);

      Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => MainScreen()),
        );
    } else {
      final error = jsonDecode(response.body);
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
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
          child: Container(
            width: screenWidth * 0.8,
            height: screenHeight * 0.6,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Text(
                  'Smart Security',
                  style: TextStyle(
                    fontSize: screenWidth * 0.08,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF364AB8),
                  ),
                ),
                SizedBox(height: 20),

                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text(
                      'Register',
                      style: TextStyle(
                        fontSize: screenWidth * 0.07,
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 10),

                TextField(
                  controller: _userController,
                  decoration: InputDecoration(
                    labelText: 'User',
                    hintText: 'Enter your username',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                    prefixIcon: Icon(Icons.person),
                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),
                ),
                SizedBox(height: 20),

                TextField(
                  controller: _emailController,
                  decoration: InputDecoration(
                    labelText: 'Email',
                    hintText: 'Enter your email',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                    prefixIcon: Icon(Icons.email),
                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),
                ),
                SizedBox(height: 20),

                TextField(
                  controller: _passwordController,
                  obscureText: true,
                  decoration: InputDecoration(
                    labelText: 'Password',
                    hintText: 'Enter your password',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                    prefixIcon: Icon(Icons.lock),
                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),
                ),
                SizedBox(height: 20),

                TextField(
                  controller: _repeatPasswordController,
                  obscureText: true,
                  decoration: InputDecoration(
                    labelText: 'Repeat password',
                    hintText: 'Repeat your password',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                    prefixIcon: Icon(Icons.lock),
                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Color(0xFF364AB8), width: 2),
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),
                ),
                SizedBox(height: 30),

                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    ElevatedButton(
                      onPressed: _isLoading ? null : _handleRegister,
                      child: _isLoading
                          ? CircularProgressIndicator(color: Colors.white)
                          : Text('Register'),
                      style: ElevatedButton.styleFrom(
                        padding: EdgeInsets.symmetric(vertical: 12, horizontal: 40),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                          side: BorderSide(color: Colors.black, width: 1),
                        ),
                        backgroundColor: Color(0x80364AB8),
                        foregroundColor: Colors.black,
                      ),
                    ),
                  ],
                ),

                Spacer(),
                TextButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => LoginScreen()),
                    );
                  },
                  child: Text(
                    "I already have an account.",
                    style: TextStyle(
                      color: Color(0xFF364AB8),
                      fontSize: 16,
                    ),
                  ),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
