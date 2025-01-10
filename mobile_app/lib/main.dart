import 'package:flutter/material.dart';
import 'screens/main_screen.dart';

void main() {
  runApp(App());
}

const MaterialColor mainPalette = MaterialColor(
  0xFF364AB8,
  <int, Color>{
    50: Color(0xFFE0E6FF),
    100: Color(0xFFB3C2FF),
    200: Color(0xFF8099FF), 
    300: Color(0xFF4D72FF), 
    400: Color(0xFF1A4CFF),
    500: Color(0xFF364AB8),
    600: Color(0xFF2C3F91), 
    700: Color(0xFF1E316A), 
    800: Color(0xFF14244C), 
    900: Color(0xFF0A1226),
  },
);

class App extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Navigation Example',
      theme: ThemeData(
        primarySwatch: mainPalette,
      ),
      home: MainScreen(),
    );
  }
}
