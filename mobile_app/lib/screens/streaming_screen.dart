import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_joystick/flutter_joystick.dart';
import 'package:webview_flutter/webview_flutter.dart';

class StreamingScreen extends StatefulWidget {
  final String ip;
  const StreamingScreen({super.key, required this.ip});

  @override
  State<StreamingScreen> createState() => _StreamingScreenState();
}

class _StreamingScreenState extends State<StreamingScreen> {
  late final WebViewController _webViewController;

  @override
  void initState() {
    super.initState();
    _webViewController = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {},
          onPageStarted: (String url) {},
          onPageFinished: (String url) {},
          onHttpError: (HttpResponseError error) {},
          onWebResourceError: (WebResourceError error) {},
          onNavigationRequest: (NavigationRequest request) {
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadRequest(Uri.parse('http://${widget.ip}:8080/0/stream'));
      print('${widget.ip}:8080/0/stream');

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.landscapeLeft,
      DeviceOrientation.landscapeRight,
    ]);
  }

  @override
  void dispose() {
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;

    return Scaffold(
      body: Stack(
        children: [
          WebViewWidget(controller: _webViewController),
          Positioned(
            left: 20,
            top: 20,
            child: GestureDetector(
              onTap: () {
                Navigator.pop(context);
              },
              child: CircleAvatar(
                radius: 30,
                backgroundColor: Colors.black54,
                child: Icon(
                  Icons.arrow_back,
                  color: Colors.white,
                  size: 30,
                ),
              ),
            ),
          ),
          Positioned(
            right: 20,
            bottom: 20,
            child: Joystick(
              mode: JoystickMode.all,
              listener: (details) {
                print('Degrees: ${details.x}, Distance: ${details.y}');
              },
              base: CircleAvatar(
                radius: screenWidth * 0.08,
                backgroundColor: Color(0x60000000),
              ),
              stick: CircleAvatar(
                radius: screenWidth * 0.03,
                backgroundColor: Color(0x80364AB8),
              ),
              includeInitialAnimation: false,
            ),
          ),
        ],
      ),
    );
  }
}

