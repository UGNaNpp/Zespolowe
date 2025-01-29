import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'login_screen.dart';
import 'home_screen.dart';
import 'notifications_screen.dart';
import 'files_screen.dart';
import 'account_screen.dart';
import 'configure_api_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;
  bool _isLoggedIn = false;
  String? _apiIp;

  final List<Widget> _screens = [
    HomeScreen(),
    NotificationsScreen(),
    FilesScreen(),
    AccountScreen(),
  ];

  void _checkLoginStatusAndAPIConfig() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    String? savedIp = prefs.getString('api-ip');

    if (savedIp == null || savedIp.isEmpty) {
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => ApiScreen(),
        ),
      );
      return;
    } else {
      _apiIp = savedIp;
    }

    String? token = prefs.getString('tokenKey');
    if (token == null || token.isEmpty) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => LoginScreen()),
      );
    } else {
      setState(() {
        _isLoggedIn = true;
      });
    }
  }

  void _onTabSelected(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  @override
  void initState() {
    super.initState();
    _checkLoginStatusAndAPIConfig();
  }

  @override
  Widget build(BuildContext context) {
    if (!_isLoggedIn) {
      return Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      body: _screens[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: _onTabSelected,
        items: [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.notifications),
            label: 'Notifications',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.folder),
            label: 'Files',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.account_circle),
            label: 'Profile',
          ),
        ],
        selectedItemColor: Color(0xFF364AB8),
        unselectedItemColor: Colors.grey,
      ),
    );
  }
}
