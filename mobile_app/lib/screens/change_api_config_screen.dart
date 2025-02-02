import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'main_screen.dart';

class ChangeApiScreen extends StatefulWidget {
  const ChangeApiScreen({super.key});

  @override
  _ChangeApiScreenState createState() => _ChangeApiScreenState();
}

class _ChangeApiScreenState extends State<ChangeApiScreen> {
  final TextEditingController _IPController = TextEditingController();
  final TextEditingController _portController = TextEditingController();
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadApiConfig();
  }

  Future<void> _loadApiConfig() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? apiConfig = prefs.getString('api-ip');

    if (apiConfig != null && apiConfig.contains(':')) {
      List<String> parts = apiConfig.split(':');
      if (parts.length == 2) {
        setState(() {
          _IPController.text = parts[0];
          _portController.text = parts[1];
        });
      }
    }
  }

  Future<void> _handleSaveApiConfig() async {
    setState(() {
      _isLoading = true;
    });

    final ip = _IPController.text.trim();
    final portStr = _portController.text.trim();

    final ipRegex = RegExp(
      r'^(25[0-5]|2[0-4]\d|[01]?\d?\d)'
      r'(\.(25[0-5]|2[0-4]\d|[01]?\d?\d)){3}$'
    );
    final bool isIpValid = ipRegex.hasMatch(ip);

    final port = int.tryParse(portStr);
    final bool isPortValid = (port != null && port >= 0 && port <= 65535);

    if (!isIpValid) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Niepoprawny adres IP')),
      );
      return;
    }

    if (!isPortValid) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Port musi być liczbą z zakresu 0–65535')),
      );
      return;
    }

    final ipWithPort = '$ip:$portStr';

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('api-ip', ipWithPort);

    setState(() {
      _isLoading = false;
    });

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (context) => MainScreen()),
    );
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
          child: Container(
            width: screenWidth * 0.8,
            height: screenHeight * 0.4,
            child: SingleChildScrollView(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Text(
                        'Configure API',
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
                    controller: _portController,
                    decoration: InputDecoration(
                      labelText: 'Port',
                      hintText: 'Enter Port number',
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

                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      ElevatedButton(
                        onPressed: _isLoading ? null : _handleSaveApiConfig,
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
                            : Text('Save config'),
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
